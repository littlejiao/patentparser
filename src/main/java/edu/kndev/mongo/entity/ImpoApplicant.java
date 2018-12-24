package edu.kndev.mongo.entity;

import java.util.List;

import org.springframework.data.annotation.Id;

import lombok.Data;
@Data
public class ImpoApplicant {
	@Id
	private String id;
	private String applicantName;
	private String ipcNum;
	private List<PatentNumByYear> pnbyYear;
}
