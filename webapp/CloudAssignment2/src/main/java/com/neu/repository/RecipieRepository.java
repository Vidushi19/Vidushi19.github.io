package com.neu.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.neu.model.Recipie;

public interface RecipieRepository extends CrudRepository<Recipie, UUID>{
	Optional<Recipie> findRecipiesById(UUID uuid);
	
	@Query(value = "select * from recipie where created_ts = (select max(created_ts) from recipie)", nativeQuery = true)
	Recipie findLatestRecipe();
	
	@Query(value = "select HEX(id) from recipie where (select replace(rtrim(replace(user_id,'0',' ')),' ','0'))=user_id;", nativeQuery = true)
	ArrayList<String> findAllRecipesForAUser(UUID uuid);
	
}
