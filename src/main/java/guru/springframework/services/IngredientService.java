package guru.springframework.services;

import guru.springframework.domain.Ingredient;
import reactor.core.publisher.Mono;
import java.util.concurrent.ExecutionException;

/**
 * Created by jt on 6/27/17.
 */
public interface IngredientService {

    Mono<Ingredient> findByRecipeIdAndIngredientId(String recipeId, String ingredientId);

    Mono<Ingredient> saveIngredientCommand(Ingredient ingredient) throws ExecutionException, InterruptedException;

    Mono<Void> deleteById(String recipeId, String idToDelete) throws ExecutionException, InterruptedException;
}
