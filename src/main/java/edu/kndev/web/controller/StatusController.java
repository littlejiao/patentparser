package edu.kndev.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.kndev.mongo.entity.PatentReport;
import edu.kndev.mongo.repo.PatentReportRepo;
import edu.kndev.web.service.PatentReportService;

@Controller
@ComponentScan("edu.kndev.mongo.config")
public class StatusController {
	@Autowired
	private PatentReportService prservice;
	
	@SuppressWarnings("finally")
	@RequestMapping(value="/status")
	@ResponseBody
	public int status(HttpServletRequest request,HttpServletResponse response) {
    	response.setHeader("Access-Control-Allow-Origin", "*"); 
    	String taskId=request.getParameter("taskId");	
    	if(prservice.get(taskId)!=null) {
    		PatentReport pr = prservice.get(taskId);
    		int status= pr.getStatuscode();
    		return status;
    	}else {
    		return -1;
    	}

		      
    }
}
