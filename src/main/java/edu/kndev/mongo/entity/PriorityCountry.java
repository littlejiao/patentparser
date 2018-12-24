package edu.kndev.mongo.entity;

import org.springframework.data.annotation.Id;

import lombok.Data;
@Data
public class PriorityCountry {
	@Id
	private String id;
	private String country;
	private int number;
	
}
