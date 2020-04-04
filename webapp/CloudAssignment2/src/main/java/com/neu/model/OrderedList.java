package com.neu.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "orderedList")
public class OrderedList {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id",unique = true,nullable = false, columnDefinition = "BINARY(16)")
    @JsonIgnore
    private UUID id;
	
	@Column(name = "position")
	@Min(value = 1, message = "Please provide position value greater than 1")
    private int position;
	
	@NotNull(message = "Item field can not be null")
	@Column(name = "items")
    private String items;

	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public String getItems() {
		return items;
	}
	public void setItems(String items) {
		this.items = items;
	}
}
