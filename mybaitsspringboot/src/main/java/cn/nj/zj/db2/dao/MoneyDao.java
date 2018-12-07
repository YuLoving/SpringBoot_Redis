package cn.nj.zj.db2.dao;

import cn.nj.zj.pojo.Money;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Qualifier;

@Mapper
@Qualifier("db2SqlSessionTemplate")
public interface MoneyDao {
    /**
     * 通过id 查看工资详情
     */
    Money findMoneyById(@Param("id") int id);



}
