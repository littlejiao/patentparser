package edu.kndev.mongo.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import edu.kndev.mongo.entity.Patent;


public interface PatentRepo extends MongoRepository<Patent,String>{
	public Patent findById(String id);
	
	//@Query(value = "{'topicId':'ff8081815fbdab6e015fbe7568460000'}")
	public Page<Patent> findAll(Pageable pageable);

	
	Page<Patent> findAllPatentBytopicId(String topicId,Pageable pageable);
	List<Patent> findByTopicId(String topicId);
}
