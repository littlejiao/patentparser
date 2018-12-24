package edu.kndev.mongo.entity;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LifeCycle {
	@Id
	private String id;
	private String year;
	private int peopleCounts;
	private int applicationCounts;
	
}
