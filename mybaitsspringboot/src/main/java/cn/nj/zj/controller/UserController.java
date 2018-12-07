package cn.nj.zj.controller;

import cn.nj.zj.pojo.User;
import cn.nj.zj.db1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping(value = "query",method = RequestMethod.GET)
    @ResponseBody
    public User testQuery() {
        System.out.println("debug进来了！！！！");
        return userService.selectUserByName("中云");
    }

    @RequestMapping(value = "add",method = RequestMethod.POST)
    @ResponseBody
    public Integer add(String name,Integer age,Double money)
    {
        int i = userService.add(name, age, money);
        if (i>0) {
            System.out.println("成功添加:"+i);
        }else {
            System.out.println("添加失败了:"+i);
        }
        return i;
    }


    @RequestMapping(value = "pladd",method = RequestMethod.POST)
    @ResponseBody
    public Integer pladd()
    {
        List<User> list = new ArrayList<User>();
        for (int i=10;i<20;i++){
            User user = new User();
            user.setName("名称"+i);
            user.setAge(i);
            user.setMoney(10*i);
            list.add(user);
        }
      int pladd = userService.pladd(list);
        if (pladd>0) {
            System.out.println("批量成功添加:"+pladd);
        }else {
            System.out.println("批量添加失败了:"+pladd);
        }
        return pladd;
    }

}


