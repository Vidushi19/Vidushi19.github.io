package com.neu.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.ReadOnlyProperty;

@Entity
@Table(name = "image")
public class Image {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@ReadOnlyProperty
	@Column(name = "id", unique = true, nullable = false, columnDefinition = "BINARY(16)")
	private UUID id;
	
	@ReadOnlyProperty
	@Column(name = "url",nullable=false)
	private String url;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	
}
