package com.itheima.service.impl;

import com.itheima.dao.UserDao;
import com.itheima.domain.User;
import com.itheima.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public List<User> findAll() {

        List<User> list = userDao.findAll();
        System.out.println(list);
        return list;
    }

    @Override
    public void saveUser(User user) {

    }

    @Override
    public double findYearSal(int empno) {
        Map map = new HashMap<String,Object>();
        map.put("empno",empno);
        Double yearsal = null;
        map.put("yearsal",yearsal);
        userDao.p_findYearSal(map);
        yearsal = (double) map.get("yearsal");
        System.out.println(yearsal);
        return yearsal;
    }
}
