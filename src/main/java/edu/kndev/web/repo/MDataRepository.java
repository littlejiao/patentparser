package edu.kndev.web.repo;

import java.util.List;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import edu.kndev.web.mdata.MData;

public interface MDataRepository extends JpaRepository<MData, String>{
	//@Query("select searches from MData where topic_id = ")
	//public MData findById(String id);
	public List<MData> findByTopicID(String topicid);
}
