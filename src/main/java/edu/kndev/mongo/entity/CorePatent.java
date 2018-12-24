package edu.kndev.mongo.entity;

import org.springframework.data.annotation.Id;

import lombok.Data;
@Data
public class CorePatent {
	@Id
	private String id;
	private String patentNum;
	private int citedCount;
	private String applicant;
	private String title;
	
}
