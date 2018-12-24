package edu.kndev.mongo.entity;

import org.springframework.data.annotation.Id;

import lombok.Data;
@Data
public class PatentNumByYear {
	@Id
	private String id;
	private int year;
	private int patentCounts;
}
