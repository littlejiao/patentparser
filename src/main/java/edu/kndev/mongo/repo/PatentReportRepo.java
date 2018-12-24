package edu.kndev.mongo.repo;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import edu.kndev.mongo.entity.PatentReport;


public interface PatentReportRepo extends MongoRepository<PatentReport,String>{
	public PatentReport findById(String id);
	public Page<PatentReport> findAll(Pageable pageable);
	public PatentReport findByTopicId(String topicId);
}
