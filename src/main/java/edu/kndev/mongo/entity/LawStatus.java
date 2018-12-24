package edu.kndev.mongo.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import lombok.Data;
@Data
public class LawStatus {
	@Id
	private String id;
	@Transient
	private List<String> publicPN;
	@Transient
	private List<String> authorizationPN; 
	private int publicCounts;
	private int authorizationCounts;
}
