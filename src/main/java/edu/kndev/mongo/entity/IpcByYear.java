package edu.kndev.mongo.entity;

import org.springframework.data.annotation.Id;

import lombok.Data;
@Data
public class IpcByYear {
	@Id
	private String id;
	private int year;
	private String ipcNum;
}
