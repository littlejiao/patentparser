package edu.kndev.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.kndev.web.service.MDataService;



@RestController
public class MDataController {
    
    @Autowired
    MDataService ms;
//    //测试插入新的数据
//    @GetMapping(path="/add")
//    public @ResponseBody String addNewUser (@RequestParam String name
//            , @RequestParam String email) {
//        
//        User n = new User();
//        n.setName(name);
//        n.setEmail(email);
//        userRepository.save(n);
//        return "保存成功";
//    }
    
//    //测试获取全部的数据
//    @GetMapping(path="/all")
//    public Iterable<User> getAllUsers() {
//        return userRepository.findAll();
//    }
    
    @GetMapping(path="/get")
    public String get(String taskId) {
    	System.out.println("hhhhhhhhhhhhhh");
    	return ms.getQuery(taskId);
    }
   
}
