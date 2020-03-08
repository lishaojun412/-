package com.itheima.mybatis.sqlsession;

import com.itheima.dao.UserDao;

public interface SqlSession {
    <T> T getMapper(Class<T> userDaoClass);

    void close();

}
