package com.neu.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import com.neu.model.NutritionInformation;

public interface NutritionInfoRepository extends CrudRepository<NutritionInformation, UUID>{

}
