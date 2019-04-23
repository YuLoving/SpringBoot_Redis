package com.nj.controller;

import com.nj.pojo.ExcelData;
import com.nj.pojo.User;
import com.nj.service.IUserservice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;


import static com.nj.utill.ExcelUtil.exportExcel;

@Controller
public class valueTest {
    private  final static Logger logger= LoggerFactory.getLogger(valueTest.class);

    @Autowired
    private IUserservice service;


    @Value("${spring.datasource.url}")
    private String aa;

    @RequestMapping("/get")
    @ResponseBody
    public String getvalue(){
        logger.info("==============value取配置文件的值=============:"+aa);
        return aa;
    }

    @GetMapping("/getbyname")
    @ResponseBody
    public List<User> getvalue( String name){
            logger.info("============参数name："+name);
        List<User> list = service.getbyname(name);

        logger.info("=======取出的值："+list);
        return list;
    }

    @GetMapping("/excelout")
    @ResponseBody
    public void excelout(HttpServletResponse response){
        try {
            ExcelData data = new ExcelData();
            data.setFileName("用户表.xlsx");
            String [] aa={"号码","名称","年龄"};
            data.setHead(aa);
            List<String[]> list = new ArrayList<>();
            User user = new User();
            user.setId(1);
            user.setAge(22);
            user.setYourname("好烦");
            String [] bb={user.getId()+"",user.getYourname(),user.getAge()+""};
            User user2 = new User();
            user2.setId(2);
            user2.setAge(32);
            user2.setYourname("好烦123");
            String [] cc={user2.getId()+"",user2.getYourname(),user2.getAge()+""};
            list.add(bb);
            list.add(cc);
            data.setData(list);
            exportExcel(response,data);
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    }



