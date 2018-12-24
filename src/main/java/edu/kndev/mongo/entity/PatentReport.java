package edu.kndev.mongo.entity;

import java.util.List;

import org.springframework.data.annotation.Id;

import lombok.Data;
@Data
public class PatentReport {
	@Id
	private String id;
	private List<LifeCycle> lifeCycles;
	private List<IpcByYear> ipcbyYears;
	private List<CorePatent> corePatents;
	private List<PriorityCountry> priorityCountrys;
	private List<ImpoApplicant> impoApplicants;
	private LawStatus lawStatus;
	private String topicId;
	private int statuscode;
}
