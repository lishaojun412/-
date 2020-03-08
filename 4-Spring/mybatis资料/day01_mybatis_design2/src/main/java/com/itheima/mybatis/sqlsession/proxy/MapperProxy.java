package com.itheima.mybatis.sqlsession.proxy;

import com.itheima.mybatis.cfg.Mapper;
import com.itheima.mybatis.utils.Executor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Map;

public class MapperProxy implements InvocationHandler {
    private Map<String,Mapper> mappers;
    private Connection conn;
    public MapperProxy(Map<String, Mapper> mappers, Connection conn) {
        this.mappers = mappers;
        this.conn = conn;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        String className = method.getDeclaringClass().getName();
        String key = className + "." + methodName;
        Mapper mapper = mappers.get(key);
        //判断调用的方法是否在配置文件里中存在
        if (mapper == null){
            throw  new IllegalArgumentException("参数有误");
        }
        //选择要执行sql的方法并执行
        System.out.println(mapper);
        return new Executor().selectList(mapper,conn);
    }
}
