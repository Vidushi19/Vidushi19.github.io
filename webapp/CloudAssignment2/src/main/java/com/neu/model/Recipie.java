package com.neu.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.data.annotation.ReadOnlyProperty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "recipie")
@JsonIgnoreProperties(value = {"created_ts", "updated_ts"})
public class Recipie {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id",unique = true,nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;
	
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_ts", nullable=false,updatable= false)
    private Date created_ts;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_ts",nullable=false,updatable= false)
    private Date updated_ts;
    
    @ReadOnlyProperty
    private UUID user_id;
    

    @Min(value = 1, message = "please provide cook_time_in_min greater than 1")
    @Column(name = "cook_time_in_min",nullable=false)
    private int cook_time_in_min;
    

    @Min(value = 1, message = "please provide prep_time_in_min greater than 1")
    @Column(name = "prep_time_in_min",nullable=false)
    private int prep_time_in_min;
    
    @Column(name = "total_time_in_min",updatable= false)
    private int total_time_in_min;
    
    @NotNull
    @Column(name = "title",nullable=false)
    private String title;
    
    
    @NotNull
    @Column(name = "cusine",nullable=false)
    private String cusine;
    
    @Column(name = "servings",nullable=false)
    @Max(value = 5, message = "servings must have a value between 1 and 5")
    @Min(value = 1, message = "servings must have a value between 1 and 5")
    private int servings;
    
    @UniqueElements
    @NotEmpty(message = "PLease provide ingredients")
    @ElementCollection
    private List<String> ingredients;

    @OneToMany(cascade = CascadeType.ALL)
    @NotEmpty(message = "Please provide the steps for recipie")
    private List<OrderedList> steps;

    @OneToOne (cascade=CascadeType.ALL)
    @JoinColumn(unique= true, insertable=true, updatable=true)
    @NotNull(message = "Please provide Nutrition Information")
    private NutritionInformation nutrition_information;
    

    @OneToOne (cascade=CascadeType.ALL)
    @JoinColumn(unique= true, insertable=true, updatable=true)
    //@NotNull(message = "Please provide Nutrition Information")
    private Image image;
    

//    @OneToOne (cascade=CascadeType.ALL)
//    @JoinColumn(unique= true, insertable=true, updatable=true)
//    @NotNull(message = "Insert value for image url")
//    @Max(value = 1, message = "Only one image is allowed for a recipie")
//    private Image image;

    public Recipie() {
    	steps = new ArrayList<>();
        ingredients = new ArrayList<>();
    }

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Date getCreated_ts() {
		return created_ts;
	}

	public void setCreated_ts(Date created_ts) {
		this.created_ts = created_ts;
	}

	public Date getUpdated_ts() {
		return updated_ts;
	}

	public void setUpdated_ts(Date updated_ts) {
		this.updated_ts = updated_ts;
	}


	public int getCook_time_in_min() {
		return cook_time_in_min;
	}

	public void setCook_time_in_min(int cook_time_in_min) {
		this.cook_time_in_min = cook_time_in_min;
	}

	public int getPrep_time_in_min() {
		return prep_time_in_min;
	}

	public void setPrep_time_in_min(int prep_time_in_min) {
		this.prep_time_in_min = prep_time_in_min;
	}

	public int getTotal_time_in_min() {
		return total_time_in_min;
	}

	public void setTotal_time_in_min(int total_time_in_min) {
		this.total_time_in_min = total_time_in_min;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCusine() {
		return cusine;
	}

	public void setCusine(String cusine) {
		this.cusine = cusine;
	}

	public int getServings() {
		return servings;
	}

	public void setServings(int servings) {
		this.servings = servings;
	}

	public UUID getUser_id() {
		return user_id;
	}

	public void setUser_id(UUID user_id) {
		this.user_id = user_id;
	}



	public List<OrderedList> getSteps() {
		return steps;
	}

	public void setSteps(List<OrderedList> steps) {
		this.steps = steps;
	}

	public NutritionInformation getNutrition_information() {
		return nutrition_information;
	}

	public void setNutrition_information(NutritionInformation nutrition_information) {
		this.nutrition_information = nutrition_information;
	}

	public List<String> getIngredients() {
		return ingredients;
	}

	public void setIngredients(List<String> ingredients) {
		this.ingredients = ingredients;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

//	public Image getImage() {
//		return image;
//	}
//
//	public void setImage(Image image) {
//		this.image = image;
//	} 
    

}
