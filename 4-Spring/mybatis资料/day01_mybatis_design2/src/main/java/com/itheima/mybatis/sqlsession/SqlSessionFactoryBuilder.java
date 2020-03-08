package com.itheima.mybatis.sqlsession;

import com.itheima.mybatis.cfg.Configuration;
import com.itheima.mybatis.sqlsession.impl.SqlSessionFactoryImpl;
import com.itheima.mybatis.utils.XMLConfigBuilder;

import java.io.InputStream;

public class SqlSessionFactoryBuilder {

    public SqlSessionFactory build(InputStream in) {
        Configuration conf = XMLConfigBuilder.loadConfiguration(in);
        return new SqlSessionFactoryImpl(conf);
    }
}
