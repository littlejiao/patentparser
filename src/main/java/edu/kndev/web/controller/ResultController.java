package edu.kndev.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.kndev.mongo.entity.PatentReport;
import edu.kndev.web.service.PatentReportService;

@Controller
@ComponentScan("edu.kndev.mongo.config")
public class ResultController {
	@Autowired
	private PatentReportService prservice;
	
	@RequestMapping("/result")
	public String getpr(HttpServletRequest request,Model model) {
		String taskId = request.getParameter("taskId");
		PatentReport pr = prservice.get(taskId);
		model.addAttribute("patentrepo",pr);
		return "user/patentreport";
	}
}
