package com.nj.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class valueTest {
    private  final static Logger logger= LoggerFactory.getLogger(valueTest.class);

    @Value("${spring.datasource.url}")
    private String aa;

    @RequestMapping("/get")
    @ResponseBody
    public String getvalue(){
        logger.info("==============value取配置文件的值=============:"+aa);
        return aa;
    }
    }



