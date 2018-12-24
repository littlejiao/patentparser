package edu.kndev.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.kndev.web.mdata.MData;
import edu.kndev.web.repo.MDataRepository;

@Service
public class MDataService {
	@Autowired
	private MDataRepository mdatarepo;
	
	public String getQuery(String topicId) {
    	String result = "";
        List<MData> mdata = mdatarepo.findByTopicID(topicId);
        for(MData m:mdata) {
        	if(m.getDs_id().equals("DIIDW")&&m.getTotal_count()!=0) {
        		result = m.getSearches();
        	}
        }
        return result;
    }
}
