package edu.kndev.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.mongodb.WriteResult;

import edu.kndev.mongo.entity.PatentReport;
import edu.kndev.mongo.repo.PatentReportRepo;

@Service
public class PatentReportService {
	@Autowired
	private PatentReportRepo reportrepo;
	
	@Autowired
    //@Qualifier("articleMongoTemplate")
    MongoTemplate mongoTemplate;
	
	public PatentReport get(String taskId) {
		PatentReport pr = reportrepo.findByTopicId(taskId);
		return pr;
	}
	
	public void update(String reportId,int statuscode) {
		WriteResult writeResult = mongoTemplate.updateFirst(
                new Query(Criteria.where("_id").is(reportId)),
                new Update().set("statuscode",statuscode),
                "patentreport"
        );
        System.out.println(writeResult);
		
	}
	
	
	
}
