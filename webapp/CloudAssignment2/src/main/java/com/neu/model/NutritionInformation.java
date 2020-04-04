package com.neu.model;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "nutrition_information")
public class NutritionInformation {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id",unique = true,nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "calories",nullable=false)
    @NotNull(message = "Calories section can not be null")
    private int calories;
    
    @Column(name = "cholesterol_in_mg",nullable=false)
    @NotNull(message = "Cholestrol section can not be null")
    private float cholesterol_in_mg;
    
    @Column(name = "sodium_in_mg",nullable=false)
    @NotNull(message = "Sodium section can not be null")
    private int sodium_in_mg;
    
    @Column(name = "carbohydrates_in_grams",nullable=false)
    @NotNull(message = "Carbohydrate section can not be null")
    private float carbohydrates_in_grams;
    
    @Column(name = "protein_in_grams",nullable=false)
    @NotNull(message = "Protein section can not be null")
    private float protein_in_grams;   

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public int getCalories() {
		return calories;
	}

	public void setCalories(int calories) {
		this.calories = calories;
	}

	public float getCholesterol_in_mg() {
		return cholesterol_in_mg;
	}

	public void setCholesterol_in_mg(float cholesterol_in_mg) {
		this.cholesterol_in_mg = cholesterol_in_mg;
	}

	public int getSodium_in_mg() {
		return sodium_in_mg;
	}

	public void setSodium_in_mg(int sodium_in_mg) {
		this.sodium_in_mg = sodium_in_mg;
	}

	public float getCarbohydrates_in_grams() {
		return carbohydrates_in_grams;
	}

	public void setCarbohydrates_in_grams(float carbohydrates_in_grams) {
		this.carbohydrates_in_grams = carbohydrates_in_grams;
	}

	public float getProtein_in_grams() {
		return protein_in_grams;
	}

	public void setProtein_in_grams(float protein_in_grams) {
		this.protein_in_grams = protein_in_grams;
	}

	}
