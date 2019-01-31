package com.nj.service;

import com.nj.pojo.User;

import java.util.List;

public interface IUserservice {
    List<User> getall();

    int update(User user);
}
