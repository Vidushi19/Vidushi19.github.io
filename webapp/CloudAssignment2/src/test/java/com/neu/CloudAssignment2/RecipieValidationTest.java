package com.neu.CloudAssignment2;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.BeforeClass;
import org.junit.Test;

import com.neu.model.NutritionInformation;
import com.neu.model.OrderedList;
import com.neu.model.Recipie;


public class RecipieValidationTest {
	
    private static Validator validator;

    @BeforeClass
    public static void setupValidatorInstance() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }
    
    //Setting up test recipie data for validations
    public Recipie getDefaultRecipieObject() {
        Recipie testRec = new Recipie();
        testRec.setCook_time_in_min(3);
        testRec.setPrep_time_in_min(3);
        testRec.setTitle("Chicken curry");
        testRec.setCusine("Indian");
        testRec.setServings(4);

        List<OrderedList> testSteps = new ArrayList<>();
        OrderedList testOL = new OrderedList();
        testOL.setPosition(2);
        testOL.setItems("All");
        testSteps.add(testOL);
        testRec.setSteps(testSteps);

        NutritionInformation testNI = new NutritionInformation();
        testNI.setCalories(200);
        testNI.setCholesterol_in_mg(4);
        testNI.setSodium_in_mg(100);
        testNI.setCarbohydrates_in_grams(30);
        testNI.setProtein_in_grams(30);
        testRec.setNutrition_information(testNI);

        List<String> testIngredient = new ArrayList<>();
        testIngredient.add("Chicken");
        testIngredient.add("Other ingredients");
        testRec.setIngredients(testIngredient);

        return testRec;
    }
    
    @Test //Testing if the above set default values are correct
    public void testingConstraintViolationsinRecipie() {
      
    	Recipie r = getDefaultRecipieObject();

        Set<ConstraintViolation<Recipie>> v = validator.validate(r);
        assertThat(v.size()).isEqualTo(0);
    }
    
    @Test //Testing : Cook_time_in_min must be not null
    public void validatingRecipieCookTime() {

    	Recipie r = getDefaultRecipieObject();
        r.setCook_time_in_min(0);

        Set<ConstraintViolation<Recipie>> v = validator.validate(r);
        assertThat(v.size()).isEqualTo(1);
    }
    
    @Test //Testing : Prep_time_in_min must be not null
    public void validateRecipiePrepTime() {

        Recipie r = getDefaultRecipieObject();
        r.setPrep_time_in_min(0);

        Set<ConstraintViolation<Recipie>> v = validator.validate(r);
        assertThat(v.size()).isEqualTo(1);
    }

    @Test //Testing : Title must be not null
    public void validateRecipieTitle() {

        Recipie r = getDefaultRecipieObject();
        r.setTitle(null);

        Set<ConstraintViolation<Recipie>> v = validator.validate(r);
        assertThat(v.size()).isEqualTo(1);
    }

    @Test //Testing : Cusine must be not null
    public void validateRecipieCuisineNotNull() {
     
        Recipie r = getDefaultRecipieObject();
        r.setCusine(null);

        Set<ConstraintViolation<Recipie>> v = validator.validate(r);
        assertThat(v.size()).isEqualTo(1);
    }
    
    @Test //Testing : Servings must have a positive value
    public void validateRecipieServingNotNegative() {

        Recipie r = getDefaultRecipieObject();
        r.setServings(-2);

        Set<ConstraintViolation<Recipie>> v = validator.validate(r);
        assertThat(v.size()).isEqualTo(1);
    }

    @Test //Testing : Steps must be not null
    public void validateRecipieStepsNotNull() {
        
        Recipie r = getDefaultRecipieObject();
        List<OrderedList> ol = new ArrayList<>();
        r.setSteps(ol);

        Set<ConstraintViolation<Recipie>> v = validator.validate(r);
        assertThat(v.size()).isEqualTo(1);
    }

    @Test //Testing : Nutrition_information must be not null
    public void validateRecipieNutritionInfoNotNull() {

        Recipie r = getDefaultRecipieObject();
        r.setNutrition_information(null);

        Set<ConstraintViolation<Recipie>> v = validator.validate(r);
        assertThat(v.size()).isEqualTo(1);
    }

    @Test //Testing : Ingredients must be not null
    public void validateRecipieIngredientsNotNull() {
        
        Recipie r = getDefaultRecipieObject();
        List<String> ingredients = new ArrayList<>();
        r.setIngredients(ingredients);

        Set<ConstraintViolation<Recipie>> v = validator.validate(r);
        assertThat(v.size()).isEqualTo(1);
    }


}
