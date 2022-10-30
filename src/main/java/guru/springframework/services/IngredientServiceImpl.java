package guru.springframework.services;

import guru.springframework.domain.Ingredient;
import guru.springframework.domain.Recipe;
import guru.springframework.repositories.reactive.RecipeReactiveRepository;
import guru.springframework.repositories.reactive.UnitOfMeasureReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Created by jt on 6/28/17.
 */
@Slf4j
@Service
public class IngredientServiceImpl implements IngredientService {
    private final RecipeReactiveRepository recipeReactiveRepository;
    private final UnitOfMeasureReactiveRepository unitOfMeasureRepository;

    public IngredientServiceImpl(RecipeReactiveRepository recipeReactiveRepository,
                                 UnitOfMeasureReactiveRepository unitOfMeasureRepository){
        this.recipeReactiveRepository = recipeReactiveRepository;
        this.unitOfMeasureRepository = unitOfMeasureRepository;
    }

    @Override
    public Mono<Ingredient> findByRecipeIdAndIngredientId(String recipeId, String ingredientId) {

        return recipeReactiveRepository
                .findById(recipeId)
                .flatMapIterable(Recipe::getIngredients)
                .filter(ingredient -> ingredient.getId().equalsIgnoreCase(ingredientId))
                .single()
                .map(ingredient -> {
                    if(recipeId != null)
                    ingredient.setRecipeId(recipeId);
//                    IngredientCommand command = ingredientToIngredientCommand.convert(ingredient);
                    return ingredient;
                });
    }


    @Override
    public Mono<Ingredient> saveIngredientCommand(Ingredient ingredient) throws ExecutionException, InterruptedException {


            Recipe recipe = recipeReactiveRepository.findById(ingredient.getRecipeId()).toFuture().get();


        if (recipe == null) {

            //todo toss error if not found!
            log.error("Recipe not found for id: " + ingredient.getRecipeId());
            return Mono.just(new Ingredient());
        } else {


            Optional<Ingredient> ingredientOptional = recipe
                    .getIngredients()
                    .stream()
                    .filter(ingredient1 -> ingredient1.getId().equals(ingredient.getId()))
                    .findFirst();

            if (ingredientOptional.isPresent()) {
                Ingredient ingredientFound = ingredientOptional.get();
                ingredientFound.setDescription(ingredient.getDescription());
                ingredientFound.setAmount(ingredient.getAmount());
                ingredientFound.setUom(unitOfMeasureRepository
                        .findById(ingredient.getUom().getId()).toFuture().get());

                if (ingredientFound.getUom() == null) {
                    new RuntimeException("UOM NOT FOUND");
                }
            } else {
                //add new Ingredient
                //Ingredient ingredient2 = ingredientCommandToIngredient.convert(ingredient);
                recipe.addIngredient(ingredient);
            }
            Recipe savedRecipe = null;
            try {
                savedRecipe = recipeReactiveRepository.save(recipe).toFuture().get();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Optional<Ingredient> savedIngredientOptional = savedRecipe.getIngredients().stream()
                    .filter(recipeIngredients -> recipeIngredients.getId().equals(ingredient.getId()))
                    .findFirst();

            //check by description
            if (!savedIngredientOptional.isPresent()) {
                //not totally safe... But best guess
                savedIngredientOptional = savedRecipe.getIngredients().stream()
                        .filter(recipeIngredients -> recipeIngredients.getDescription().equals(ingredient.getDescription()))
                        .filter(recipeIngredients -> recipeIngredients.getAmount().equals(ingredient.getAmount()))
                        .filter(recipeIngredients -> recipeIngredients.getUom().getId().equals(ingredient.getUom().getId()))
                        .findFirst();
            }

            //todo check for fail

            //enhance with id value
            //IngredientCommand ingredientCommandSaved = ingredientToIngredientCommand.convert(savedIngredientOptional.get());
            //ingredientCommandSaved.setRecipeId(recipe.getId());
            Ingredient ingredientSaved = savedIngredientOptional.get();
            ingredientSaved.setRecipeId(recipe.getId());




            return Mono.just(ingredientSaved);
        }
    }

    @Override
    public Mono<Void> deleteById(String recipeId, String idToDelete) throws ExecutionException, InterruptedException {

        log.debug("Deleting ingredient: " + recipeId + ":" + idToDelete);

        Recipe recipe = recipeReactiveRepository.findById(recipeId).toFuture().get();

        if (recipe != null) {

            log.debug("found recipe");

            Optional<Ingredient> ingredientOptional = recipe
                    .getIngredients()
                    .stream()
                    .filter(ingredient -> ingredient.getId().equals(idToDelete))
                    .findFirst();

            if (ingredientOptional.isPresent()) {
                log.debug("found Ingredient");

                recipe.getIngredients().remove(ingredientOptional.get());
                recipeReactiveRepository.save(recipe).toFuture().get();
            }
        } else {
            log.debug("Recipe Id Not found. Id:" + recipeId);
        }

        return Mono.empty();
    }
}
