package guru.springframework.controllers;

import guru.springframework.domain.Recipe;
import guru.springframework.services.RecipeService;
import org.junit.Before;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.ui.Model;
import org.springframework.web.reactive.function.server.RouterFunction;
import reactor.core.publisher.Flux;


import java.util.*;



/**
 * Created by jt on 6/17/17.
 */

@ExtendWith(MockitoExtension.class)
@WebFluxTest(controllers = {IndexController.class})
@Import(RecipeService.class)
public class IndexControllerTest {


    @MockBean
    RecipeService recipeService;


    private WebTestClient webTestClient;

    @Mock
    Model model;

    IndexController controller;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        controller = new IndexController(recipeService);
//        RouterFunction<?> routerFunction = controller.routes(recipeService);
        webTestClient = WebTestClient.bindToController(controller).build();
    }

    @Test
    public void getIndexPage() throws Exception {
        //given
        List<Recipe> recipes = new ArrayList<>();
        Recipe recipe1 = new Recipe();
        recipe1.setId("1");
        recipes.add(recipe1);

        Recipe recipe2 = new Recipe();
        recipe2.setId("1");
        recipes.add(recipe2);

        Flux<Recipe> recipeFlux = Flux.fromIterable(recipes);
        Mockito.when(recipeService.getRecipes()).thenReturn(recipeFlux);


        webTestClient.get().uri("/")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
//                .expectBodyList(Recipe.class)
//                .returnResult()
//                .getResponseBody();
//        Assertions.assertEquals(2, actual.size());




//        ArgumentCaptor<List<Recipe>> argumentCaptor = ArgumentCaptor.forClass(List.class);
//
//        when
//        String viewName = controller.getIndexPage(model);
//        System.out.println("viewName"+viewName);

//        //then
//        assertEquals("index", viewName);
//        Mockito.verify(recipeService, times(1)).getRecipes();
//        verify(model, times(1)).addAttribute(eq("recipes"), model.getAttribute("recipes"));
//        List<Recipe> setInController = (List<Recipe>) model.getAttribute("recipes");
//        assertEquals(2, recipes.size());
    }

}