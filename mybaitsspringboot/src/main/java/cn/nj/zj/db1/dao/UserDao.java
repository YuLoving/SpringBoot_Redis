package cn.nj.zj.db1.dao;

import cn.nj.zj.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
@Qualifier("db1SqlSessionTemplate")
@Mapper
public interface UserDao {
    /**
     * 通过名字查询用户信息
     */
    User findUserByName(String name);


    /**
     * 插入用户信息
     * */
    int add(@Param("name") String name,@Param("age") Integer age,@Param("money") Double money);

    /**
     * 批量添加
     * */
    int pladd(List<User> list);
}
