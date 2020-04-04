package com.neu.repository;


import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.neu.model.Image;

public interface ImageRepository extends CrudRepository<Image, UUID>{
	
	Optional<Image> findImageById(UUID uuid);

}
