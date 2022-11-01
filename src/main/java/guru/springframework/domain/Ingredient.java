package guru.springframework.domain;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by jt on 6/13/17.
 */
@Getter
@Setter
public class Ingredient {

    private String id = UUID.randomUUID().toString();
    @NotBlank
    private String description;
    @NotNull
    @Min(1)
    private BigDecimal amount;
    @NotNull
    private UnitOfMeasure uom;

    private String recipeId;

    public Ingredient() {

    }

    public Ingredient(String description, BigDecimal amount, UnitOfMeasure uom, String recipeId) {
        this.recipeId = recipeId;
        this.description = description;
        this.amount = amount;
        this.uom = uom;
    }
}
