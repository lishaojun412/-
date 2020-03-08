package com.itheima.mybatis.sqlsession.impl;

import com.itheima.dao.UserDao;
import com.itheima.mybatis.cfg.Configuration;
import com.itheima.mybatis.sqlsession.SqlSession;
import com.itheima.mybatis.sqlsession.proxy.MapperProxy;
import com.itheima.mybatis.utils.DataSourceUtil;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

public class SqlSessionImpl implements SqlSession {
    private Configuration conf;
    private Connection conn ;

    public SqlSessionImpl(Configuration conf) {
        this.conf = conf;
        conn = DataSourceUtil.getConnection(conf);
    }

    public <T> T getMapper(Class<T> daoClass) {
        return (T) Proxy.newProxyInstance(daoClass.getClassLoader(), new Class[]{daoClass}, new MapperProxy(conf.getMappers(),conn));
    }

    public void close() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
