package guru.springframework.controllers;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.services.RecipeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * Created by jt on 6/19/17.
 */
@Slf4j
@Controller
public class RecipeController {
    private static final String RECIPE_RECIPEFORM_URL = "recipe/recipeform";
    private final RecipeService recipeService;
    private WebDataBinder webDataBinder;

    private String value;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }
    @InitBinder
    public void initBinder(WebDataBinder webDataBinder){
        this.webDataBinder=webDataBinder;
    }

    @GetMapping("/recipe/{id}/show")
    public String showById(@PathVariable String id, Model model){

        model.addAttribute("recipe", recipeService.findById(id));

        return "recipe/show";
    }

    @GetMapping("recipe/new")
    public String newRecipe(Model model){
        model.addAttribute("recipe", new RecipeCommand());

        return "recipe/recipeform";
    }

    @GetMapping("recipe/{id}/update")
    public String updateRecipe(@PathVariable String id, Model model){
        model.addAttribute("recipe", recipeService.findCommandById(id));
        return RECIPE_RECIPEFORM_URL;
    }

    @PostMapping("recipe")
    public String saveOrUpdate(@ModelAttribute("recipe") RecipeCommand command){
        webDataBinder.validate();
        BindingResult bindingResult = webDataBinder.getBindingResult();
        if(bindingResult.hasErrors()){

            bindingResult.getAllErrors().forEach(objectError -> {
                log.debug(objectError.toString());
            });

            return RECIPE_RECIPEFORM_URL;
        }

        Mono<RecipeCommand> savedCommandMono = recipeService.saveRecipeCommand(command);
        Mono<String> idMono = savedCommandMono.map((RecipeCommand::getId));
        idMono.subscribe(v -> this.value = v);

        return "redirect:/recipe/" + value + "/show";
    }

    @GetMapping("recipe/{id}/delete")
    public String deleteById(@PathVariable String id){

        log.debug("Deleting id: " + id);

        recipeService.deleteById(id);
        return "redirect:/";
    }

//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    @ExceptionHandler(NotFoundException.class)
//    public ModelAndView handleNotFound(Exception exception){
//
//        log.error("Handling not found exception");
//        log.error(exception.getMessage());
//
//        ModelAndView modelAndView = new ModelAndView();
//
//        modelAndView.setViewName("404error");
//        modelAndView.addObject("exception", exception);
//
//        return modelAndView;
//    }

}
