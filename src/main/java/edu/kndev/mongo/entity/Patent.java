package edu.kndev.mongo.entity;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Patent implements Serializable{
	private static final long serialVersionUID = -3381201451456940004L;
	@Id
	private String id;
	private String dn;
	private List<Integer> pdDate;
	private String fs;
	private String searchText;
	private String mc;
	private List<String> cpnums;
	String fingerprint;
	String datasourceId;
	List<Integer> pdYear;
	String ga;
	String ea;
	String taskId;
	String ab;
	String mn;
	String ad;
	String ae;
	List<String> ip;
	List<String> ipc1;
	List<String> ipc2;
	List<String> ipc3;
	List<String> ipc4;
	List<String> countries;
	Long fetcherTime;
	String cp;
	String cr;
	String tf;
	String topicId;
	List<String> au;
	String pd;
	String rg;
	String ti;
	String ri;
	String pi;
	String fd;
	String dc;
	Long updatetime_nano;
	List<String> pn;
	
	
	
	
	
}
