package edu.kndev.mongo.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class YearTrend {
	@Id
	private String id;
	private int year;
	@Transient
	private List<String> applications;
	@Transient
	private List<String> applicants;
	private int applicationCounts;
	private int applicantCounts;
	private List<String> ipcNums;

	//private String topicId;
}
