package com.nj.service.impl;

import com.nj.dao.Usermapper;
import com.nj.pojo.User;
import com.nj.redisconfig.JedisClient;
import com.nj.service.IUserservice;
import com.nj.utill.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserserviceImpl implements IUserservice {
    private final static Logger logger= LoggerFactory.getLogger(UserserviceImpl.class);

    @Autowired
    private Usermapper usermapper;

    @Autowired
    private JedisClient jedisClient;
    
    @Value("${USER_KEY}")
    private String USER_KEY;

    @Value("${USER_KRY_EXPIRE}")
    private Integer USER_KRY_EXPIRE;

    /**
     * 把所有查出来的数据放入Redis中
     * @return
     */
    @Override
    public List<User> getall() {
        //1.先查Redis中是否有数据，如果的话直接返回
        String json = jedisClient.get(USER_KEY);
        if(StringUtils.isNotBlank(json)) {
                List<User> users = JsonUtils.jsonToList(json, User.class);
                if (users != null) {
                    logger.info("===redis中取出user信息==:" + json);
                    return users;
                }
        }
        //没有的话查询数据库
        logger.info("===redis无user信息，从数据库里面读取======");
        //2.从数据库中查到以后，顺便插入Redis中，然后返回
        List<User> listall = usermapper.getall();
        jedisClient.set(USER_KEY, JsonUtils.objectToJson(listall));
        jedisClient.expire(USER_KEY,USER_KRY_EXPIRE);
        return  listall;
    }

    /**
     * 更新操作的同时，要同步redis中数据，可以直接删除
     * @param user
     * @return
     */
    @Override
    public int update(User user) {
        int i = usermapper.update(user);
        //同步redis中的数据，
        jedisClient.del(USER_KEY);
        return i;
    }

    @Override
    public List<User> getbyname(String name) {
        return usermapper.getbyname(name);
    }
}
