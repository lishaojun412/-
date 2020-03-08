package com.itheima.mybatis.sqlsession.impl;

import com.itheima.mybatis.cfg.Configuration;
import com.itheima.mybatis.sqlsession.SqlSession;
import com.itheima.mybatis.sqlsession.SqlSessionFactory;

public class SqlSessionFactoryImpl implements SqlSessionFactory {
    private Configuration conf;

    public SqlSessionFactoryImpl(Configuration conf) {
        this.conf = conf;
    }

    public SqlSession openSession() {
        return new SqlSessionImpl(conf);
    }
}
