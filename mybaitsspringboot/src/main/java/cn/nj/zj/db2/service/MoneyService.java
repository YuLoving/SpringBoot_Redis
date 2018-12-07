package cn.nj.zj.db2.service;

import cn.nj.zj.db2.dao.MoneyDao;
import cn.nj.zj.pojo.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MoneyService {

    @Autowired
    private MoneyDao moneyDao;
    /**
     * 根据名字查找用户
     */
    public Money selectMoneyById(int id) {
        return moneyDao.findMoneyById(id);
    }
}
