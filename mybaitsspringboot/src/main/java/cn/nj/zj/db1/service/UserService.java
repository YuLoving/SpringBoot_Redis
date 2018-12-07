package cn.nj.zj.db1.service;

import cn.nj.zj.db1.dao.UserDao;
import cn.nj.zj.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;


    /**
     * 根据名字查找用户
     */
    public User selectUserByName(String name) {
        return userDao.findUserByName(name);
    }

    /**
     * 添加信息
     * */
    public int add(String name,Integer age,Double money){
        return userDao.add(name, age, money);
    }

    /**
     * 批量添加信息
     * */
    public int pladd(List<User> list){
        return userDao.pladd(list);
    }
}
