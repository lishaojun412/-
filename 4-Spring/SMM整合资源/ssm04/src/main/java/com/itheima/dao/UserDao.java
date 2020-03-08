package com.itheima.dao;

import com.itheima.domain.User;

import java.util.List;
import java.util.Map;

public interface UserDao {

    List<User> findAll();

    void saveUser(User user);

    void findYearSal(Map map);

    void p_findYearSal(Map map);
}
