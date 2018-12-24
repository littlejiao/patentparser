package edu.kndev.web.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import edu.kndev.dataparser.DataByApplicant;
import edu.kndev.dataparser.DataByCorePatent;
import edu.kndev.dataparser.DataByCountry;
import edu.kndev.dataparser.DataByLawStatus;
import edu.kndev.dataparser.DataByYear;
import edu.kndev.mongo.entity.CorePatent;
import edu.kndev.mongo.entity.Patent;
import edu.kndev.mongo.entity.PatentReport;
import edu.kndev.mongo.repo.PatentRepo;
import edu.kndev.mongo.repo.PatentReportRepo;
import edu.kndev.web.repo.MDataRepository;

@Service
public class TaskService {

	@Autowired
	private PatentRepo prepo;
	@Autowired
	private PatentReportRepo reportrepo;

	@Autowired
	ExecutorService executorService;

	@Autowired
	private PatentService ps;
	
	@Autowired
	private PatentReportService prs;

	public void start(String taskId,String query) {
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				PatentReport pr;
				//如果不存在该任务，重新存入一个patentreport，创建一个新的记录
				if(prs.get(taskId)==null) {
					pr = new PatentReport();
					pr.setTopicId(taskId);
					pr.setStatuscode(0);
					reportrepo.save(pr);
				}else{//如果已经存在该任务，但是没有解析完，就在之前解析的基础上继续解析，不创建新的记录
					pr = prs.get(taskId);
				}
				
				List<Patent> list = new ArrayList<>();
				try {
					list = ps.getlist(taskId);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.err.println("Read patent data error");
				}
				System.out.println("Read patent data complete");
				System.out.println("Parsing data...");
				DataByCorePatent dbcp = new DataByCorePatent();
				try {
					pr.setCorePatents(dbcp.getCite(query));
					System.out.println("CorePatent complete");
				} catch (Exception e) {
					// TODO: handle exception
					//pr.setCorePatents(new ArrayList<CorePatent>());
					System.err.println("Query Error");
				}
				
				DataByCountry dbc = new DataByCountry();
				pr.setPriorityCountrys(dbc.countryData(list));
				System.out.println("Country complete");

				DataByApplicant dba = new DataByApplicant();
				pr.setImpoApplicants(dba.trendByApplicant(list));
				System.out.println("Applicant complete");
				
				DataByLawStatus dbs = new DataByLawStatus();
				pr.setLawStatus(dbs.lawStatusData(list));
				System.out.println("Lawstatus complete");
				
				DataByYear dby = new DataByYear();
				pr.setLifeCycles(dby.lifeCycleData(list));
				pr.setIpcbyYears(dby.ipcYearData(list));
				System.out.println("Year complete");
				
				pr.setStatuscode(1);
				reportrepo.save(pr);
				

			}
		});

	}
	
}
