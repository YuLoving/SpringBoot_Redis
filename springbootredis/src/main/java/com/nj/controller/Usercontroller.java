package com.nj.controller;

import com.nj.pojo.User;
import com.nj.service.IUserservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class Usercontroller {
    @Autowired
    private IUserservice userservice;

    @GetMapping("/getuser")
    @ResponseBody
    public List<User> getall(){
       return userservice.getall();
    }

    @PostMapping("/update")
    @ResponseBody
    public Integer update(User user){
        int update = userservice.update(user);
        return update;
    }
}
