package edu.kndev.mongo.entity;

import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import com.mongodb.annotations.NotThreadSafe;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class Applicant {
	@Id
	private String id;
	@NonNull
	private String name;
	@Transient
	private Map<Integer,List<String>> patentNumByYear;
	private Map<Integer,Integer> patentCountsByYear;
	private List<String> ipcNums;
	private List<String> patentNumsCN;
	
	
}
