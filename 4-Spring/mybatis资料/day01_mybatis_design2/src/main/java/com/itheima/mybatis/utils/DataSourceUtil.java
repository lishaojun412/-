package com.itheima.mybatis.utils;

import com.itheima.mybatis.cfg.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSourceUtil {

    public static Connection getConnection(Configuration conf) {
        try {
            Class.forName(conf.getDriver());
            return DriverManager.getConnection(conf.getUrl(), conf.getUsername(), conf.getPassword());
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
