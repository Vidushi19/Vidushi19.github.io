package com.neu.model;

public class Ingredients {
	   private int id;
	   private String name; 

	   public Ingredients() {}
	   
	   public Ingredients(String name) {
	      this.name = name;
	   }
	   
	   public int getId() {
	      return id;
	   }
	   
	   public void setId( int id ) {
	      this.id = id;
	   }
	   
	   public String getName() {
	      return name;
	   }
	   
	   public void setName( String name ) {
	      this.name = name;
	   }

}
