package edu.kndev.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.kndev.mongo.entity.Patent;
import edu.kndev.web.service.MDataService;
import edu.kndev.web.service.PatentReportService;
import edu.kndev.web.service.PatentService;
import edu.kndev.web.service.TaskService;

@Controller
@ComponentScan("edu.kndev.mongo.config")
public class WebStartController {
	@Autowired
	private TaskService taskservice;

	@Autowired
	private MDataService mservice;

	@Autowired
	private PatentReportService reportservice;

	@RequestMapping(value = "/start")
	@ResponseBody
	public Boolean start(HttpServletRequest request) {
		String taskId = request.getParameter("taskId");

		String query = mservice.getQuery(taskId);
		System.out.println(query);
		//如果任务已经存在，并且已经解析完，就不用再重新启动解析该任务
		if(reportservice.get(taskId)==null||reportservice.get(taskId).getStatuscode()!=1||reportservice.get(taskId).getCorePatents()==null) {
			if (taskId.length() != 0) {
				taskservice.start(taskId, query);
			}
		}

		return true;
	}
}
