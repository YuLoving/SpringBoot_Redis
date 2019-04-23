package com.nj.controller;

import com.github.pagehelper.PageHelper;
import com.nj.dao.Usermapper;
import com.nj.pojo.User;
import com.nj.service.IUserservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class Usercontroller {
    @Autowired
    private IUserservice userservice;

    @Autowired
    private Usermapper mapper;

    @GetMapping("/getuser")
    @ResponseBody
    public List<User> getall(){
         PageHelper.startPage(1,4);
        return userservice.getall();
    }

    @PostMapping("/update")
    @ResponseBody
    public Integer update(@RequestBody User user){
        int update = userservice.update(user);
        return update;
    }

    @GetMapping("/getall")
    @ResponseBody
    public List<User> geta(){
        PageHelper.startPage(1,4);
        return mapper.getall();
    }


}
