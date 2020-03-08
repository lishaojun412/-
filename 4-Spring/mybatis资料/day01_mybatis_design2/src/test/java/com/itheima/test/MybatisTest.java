package com.itheima.test;

import com.itheima.dao.UserDao;
import com.itheima.domain.User;
import com.itheima.mybatis.io.Resources;
import com.itheima.mybatis.sqlsession.SqlSession;
import com.itheima.mybatis.sqlsession.SqlSessionFactory;
import com.itheima.mybatis.sqlsession.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MybatisTest {
    public static void main(String[] args) throws IOException {
        //映射配置文件就是接口的实现类

        //Resources-->获取配置文件流对象
        InputStream is = Resources.getResourceAsStream("SqlMapConfig.xml");
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        //SqlSessionFactoryBuilder-->build()-->使用dom4j的sax解析方式,解析配置文件,封装Configuration对象
        //此处使用mapper使用put的方式,相当于二级缓存??
        SqlSessionFactory factory = builder.build(is);
        //SqlSessionFactory-->openSession()-->传递Configuration,创建sqlSession对象,建立Connection连接
        SqlSession sqlSession = factory.openSession();
        //sqlSession-->生成参数的代理对象(此处相当于根据mapper创建一个实现类对象并重写其方法)
        UserDao userDao = sqlSession.getMapper(UserDao.class);
        //选择并执行代理对象的方法selectList(sql,User.class,conn)
        List<User> list = userDao.findAll();
        for (User user : list) {
            System.out.println(user);
        }
        //sqlSession-->close()-->关闭连接Connection
        sqlSession.close();
        is.close();
    }
}
