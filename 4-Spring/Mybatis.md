# Mybatis

## 实现代码及原理:

```java
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
        //SqlSessionFactoryBuilder-->build()-->使用dom4j的sax解析方式,解析配置文件,封装				Configuration对象
        //此处使用mapper使用put的方式,相当于二级缓存??
        SqlSessionFactory factory = builder.build(is);
        //SqlSessionFactory-->openSession()-->传递Configuration,创建sqlSession对象,建立Connection		连接
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
```

#### JDBC事务

mybatis在连接池中取出的连接，都会将调用 connection.setAutoCommit(false)方法,所以在进行增删改操作时,需要手动提交事务:  sqlSession.commit()

可以在获取sqlSession对象时设置事务自动提交:factory.openSession(true);

#### 延迟加载

在需要创建 SqlSession 对象并需要执行 SQL 语句时，这时候 MyBatis 才会去调用 dataSource 对象来创建java.sql.Connection对象。也就是说，java.sql.Connection对象的创建一直延迟到执行SQL语句的时候。

#### 缓存

一级缓存: 

l  范围 

​	SqlSession对象, 如果关闭了sqlsession,缓存清空,一级缓存以对象的形式保存

l  默认情况下一级缓存可以直接使用

l  什么时候清空缓存?

​	1, 增删改

​	2, 使用sqlSession的commit()

​	3, 使用sqlSession的close()

二级缓存: 安全??

​	Mapper级别的缓存 多个 SqlSession 去操作同一个 Mapper 映射的 sql 语句，多个SqlSession 可以共用二级缓存，二级缓存是跨 SqlSession 的。

​	二级缓存所缓存的类一定要实现 java.io.Serializable 接口,因为二级缓存是以键值字符串的形式保存

## **主配置文件:**

```xml-dtd
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>

 	<!--配置properties:可以在标签内部配置连接数据库的信息。也可以通过属性引用外部properties配置文件
		有两种方式,resource属性:本地配置文件;url属性:网络配置文件-->
    <properties resource="jdbcConfig.properties"> </properties>

    <settings>
        <!--开启二级缓存:第一步,共三步-->
        <setting name="cacheEnabled" value="true"/>

	   	<!--开启延迟加载-->
       	<!--设置懒加载，默认为false。如果为false：则所有相关联的都会被初始化加载-->
		<setting name="lazyLoadingEnabled" value="true"/>
	    <!--默认为true。当设置为true时，懒加载的对象可能被任何懒属性全部加载；否则，每个属性按需加载-->
		<setting name="aggressiveLazyLoading" value="false"/>
    </settings>

       <!--使用typeAliases配置别名，它只能配置domain中类的别名 -->
    <typeAliases>
       <!--方式1: typeAlias用于配置别名。type属性指定的是实体类全限定类名。alias属性指定别名，当指定了			 别名就不再区分大小写 -->
        <typeAlias type="com.itheima.domain.User" alias="user"></typeAlias>
        <!--方式2: 用于指定要配置别名的包，当指定之后，该包下的实体类都会注册别名，并且类名就是别名，不			   再区分大小写-->
        <package name="com.itheima.domain"></package>
    </typeAliases>

    <!--配置环境-->
    <environments default="mysql">
        <!-- 配置mysql的环境-->
        <environment id="mysql">
            <!-- 配置事务 -->
            <transactionManager type="JDBC"></transactionManager>
            <!--配置连接池 type属性可取POOLED:连接池,UNPOOLED:不使用连接池,JNDI在服务上查找ds-->
            <dataSource type="POOLED">
                <property name="driver" value="${jdbc.driver}"></property>
                <property name="url" value="${jdbc.url}"></property>
                <property name="username" value="${jdbc.username}"></property>
                <property name="password" value="${jdbc.password}"></property>
            </dataSource>
        </environment>
    </environments>

    <!-- 配置映射文件的位置 -->
    <mappers>
		<!--mapper的两个属性:
				resource,使用xml映射文件开发时,指定映射配置文件,
				class,使用注解开发时,指定dao接口,或者使用xml映射时,放在同一个-->
		<!--方式1: 指定映射配置文件(或class)的位置-->
        <mapper resource="com/itheima/dao/IUserDao.xml"></mapper>
        <!--方式2: package标签是用于指定dao接口所在的包,当指定了之后就不需要在写mapper以及resource或			 者class了 -->
        <package name="com.itheima.dao"></package>
    </mappers>
</configuration>
```

## 单表查询

**映射配置文件**

要求:映射配置文件的**包结构**及**文件名**要与对应的dao接口保持一致

映射文件相当于dao的实现类,每个sql标签,相当于重写了接口的方法: id为方法名,resultType相当于返回值类型,parameterType相当于参数类型,参数类型可以省略??,

#### sql语句传参的方式

- #代表占位符，相当于原来 jdbc 部分所学的?，都是用于执行语句时替换实际的数据。具体的数据是由#{}里面的内容决定的。

	 {}内为ognl表达式:	如果是基本数据类型,包装类型,string类型,随便写, 如果是javabean类型, 写该类型的属性名

- ognl 表达式：它是 apache 提供的一种表达式语言，全称是：Object Graphic Navigation Language 对象图导航语言它是按照一定的语法格式来获取数据的。语法格式就是使用 #{对象.对象}的方式

- 如: #{user.username}它会先去找 user 对象，然后在 user 对象中找到 username 属性，并调用
  getUsername()方法把值取出来。但是我们在 parameterType 属性上指定了实体类名称，所以可以省略 user.而直接写 username。

- 模糊查询参数的配置方式有两种: 1,传参时已经匹配好模糊条件  2, 使用${value}固定写法,如:'%${value}%'

#### sql语句中使用#{} 与${} 的区别

- #{} 表示一个占位符号(预处理)

  通过#{}可以实现 preparedStatement 向占位符中设置值，自动进行 java 类型和 jdbc 类型转换，#{}可以有效防止 sql 注入。 #{}可以接收简单类型值或 pojo 属性值。 如果 parameterType 传输单个简单类型值，#{}括号中可以随意名称。

- ${} 表示拼接 sql 串(无预处理)

  通过${}可以将 parameterType 传入的内容拼接在 sql中且不进行 jdbc 类型转换， ${}可以接收简单类型值或 pojo 属性值，如果 parameterType 传输单个简单类型值，${}括号中只能是 value。

#### pojo包装对象

开发中通过 pojo 传递查询条件 ，查询条件是综合的查询条件，不仅包括用户查询条件还包括其它的查询条件（比如将用户购买商品信息也作为查询条件），这时可以使用包装对象传递输入参数。可**实现多个条件只传递一个参数**


``` xml-dtd
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<!--namespace:需要实现的接口的全类名-->
<mapper namespace="com.itheima.dao.IAccountDao">

	<!--使用二级缓存:第二步,共三步-->
	<cache/>

	<!-- 配置 查询结果的列名和实体类的属性名的对应关系 -->
	<!-- 当属性名和字段名相同时可以省略result,mybatis可以实现自动映射 -->
    <resultMap id="userMap" type="uSeR">
        <!-- 主键字段的对应 -->
        <id property="userId" column="id"></id>
        <!--非主键字段的对应-->
        <result property="userName" column="username"></result>
        <result property="userAddress" column="address"></result>
        <result property="userSex" column="sex"></result>
        <result property="userBirthday" column="birthday"></result>
    </resultMap>

	<!--select标签书写sql语句
		属性:id:对应dao接口中的方法名
		parameterType:方法的参数类型,不区分大小写
		resultType:返回值的额全限定类名(别名不区分大小写)
		resultMap:引入外部映射关系(类中的属性和数据库字段)-->
	<!--开启二级缓存:第三步,共三步-->
    <select id="findById" parameterType="string" resultMap="userMap" useCache="true">

	<!--在使用参数时:使用#进行引入ognl表达式-->
        select * from user where username = #{name}
    </select>
</mapper>
```

## 多表查询

一对一的多表查询有两种方法: 

1, 定义一个新的实体类, 设置属性包含所有表的字段,按照单表查询

2, 在一个实体类中属性中设置另外一个实体类,

#### 一对一|多对一

一对一: 返回值为一个实体类,实体类属性包含另一个实体类( 方法2 )

多对一: 看作一对一关系对待

```xml-dtd
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.itheima.dao.IAccountDao">
    <resultMap id="accountUserMap" type="account">
        <id property="id" column="aid"/>
        <result property="money" column="money"/>
        <result property="uid" column="uid"/>
		<!--两张表为一对一关系:使用association标签映射类属性与sql查询结果的关系,设置属性javaType-->
        <association property="user" javaType="user">
            <id property="id" column="id"/>
            <result property="username" column="username"/>
            <result property="birthday" column="birthday"/>
            <result property="sex" column="sex"/>
            <result property="address" column="address"/>
        </association>
    </resultMap>
<!--两种方法实现两张一对一关系表的查询-->
<!--方法1: 定义一个新的AccountUser类,包含两个表的属性,通过在sql语句中起别名的方式查询并封装结果 -->
    <select id="findAll" resultType="AccountUser">
        SELECT a.*,u.username,u.address FROM USER u , account a WHERE u.id= a.uid;
    </select>
<!--方法2: 在Account类中添加User类型的属性,通过accountUserMap映射关系的方式封装查询结果 -->
    <select id="findAllAccount" resultMap="accountUserMap">
?????????
        <selectKey keyProperty="id" keyColumn="id" resultType="int">
            select last_insert_id()
        </selectKey>
        SELECT a.id aid,a.money,a.UID,u.* FROM USER u , account a WHERE u.id= a.uid;
    </select>
</mapper>
```

#### 一对多|多对多

一对多: 返回值为一个实体类,实体类属性包含另一个实体类集合

多对多: 返回值为一个实体类集合,实体类属性包含另一个实体类集合

``` xml-dtd
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.itheima.dao.IUserDao">
    <resultMap id="userAccountMap" type="user">
        <id property="id" column="id"/>
        <result property="username" column="username"/>
        <result property="birthday" column="birthday"/>
        <result property="sex" column="sex"/>
        <result property="address" column="address"/>
		<!--两张表为一对多关系:使用collection标签映射类属性与sql结果集的关系,设置属性ofType-->
        <collection property="list" ofType="account">
            <id property="id" column="aid"/>
            <result property="money" column="money"/>
        </collection>
    </resultMap>
	<!--在User类中添加List<Account>属性,通过userAccountMap映射关系的方式封装查询结果-->
    <select id="findAll" resultMap="userAccountMap">
        SELECT u.*,a.id aid,a.money FROM USER u LEFT JOIN account a ON u.id=a.uid;
    </select>
</mapper>
```

## 动态sql

作用:动态拼接sql语句

标签分类:

- sql&include: 引入

- if: 条件判断

- where: 拼接where

- foreach: 循环遍历

  映射配置文件

```xml-dtd
<mapper namespace="com.itheima.dao.IUserDao">

    <resultMap id="userMap" type="uSeR">
        <id property="userId" column="id"></id>
        <result property="userName" column="username"></result>
        <result property="userAddress" column="address"></result>
        <result property="userSex" column="sex"></result>
        <result property="userBirthday" column="birthday"></result>
    </resultMap>

    <!--sql标签(了解):抽取重复的sql语句-->
    <sql id="defaultUser">
        select * from user
    </sql>

    <select id="findAll" resultMap="userMap">
		<!--include标签的作用,引入sql标签体的内容-->
        <include refid="defaultUser"></include>
    </select>

    <select id="findUserByCondition" resultMap="userMap" parameterType="user">
        select * from user
		<!--where标签作用:添加where关键字,并将标签体内的第一个and去掉-->
        <where>
			<!--if标签的作用:根据test属性中的条件判断是否拼接sql语句  -->
            <if test="userName != null">
                and username = #{userName}
            </if>
            <if test="userSex != null">
                and sex = #{userSex}
            </if>
        </where>
    </select>

    <select id="findUserInIds" resultMap="userMap" parameterType="queryvo">
        <include refid="defaultUser"></include>
        <where>
            <if test="ids != null and ids.size()>0">

			<!--foreach标签作用:循环遍历结合元素
			属性:Collection : 设置的就是要遍历的集合
				Open: sql语句拼接其实sql
				Item: 集合中元素的名称
				Close:sql结束时拼接的sql语句
				Separator: 每一次循环使用的分割符-->
                <foreach collection="ids" open="and id in (" close=")" item="uid" separator=",">
                    #{uid}
                </foreach>
            </if>
        </where>
    </select>
</mapper>
```

## 注解开发

**注解开发仍然需要配置主配置文件,映射配置和注解配置不能同时进行配置, 只能有一个配置**

注解与xml映射文件的过渡及对应关系: xml中的namespace+id为注解所在类的权限定类名,resultType为方法的返回值,sql语句为标签的方法体,从而涵盖了xml的所有信息

#### **一对一**|多对一

```java
public interface IAccountDao {
    /**
    * 查询所有账户，采用延迟加载的方式查询账户的所属用户, 一对一关系
    * @return
    */
	@Select("select * from account")
	@Results(id="accountMap",value= {
	    @Result(id=true,column="id",property="id"),
        @Result(column="uid",property="uid"),
        @Result(column="money",property="money"),
         /*column为表中的字段,此处的column将值传递给one中的查询方法,也就是findById(Integer userId)*/ 
        @Result(column="uid",property="user", 
                one=@One(select="com.itheima.dao.IUserDao.findById",fetchType=FetchType.LAZY))})
	List<Account> findAll();
}

public interface IUserDao {

    @Select("select * from user where id = #{uid} ")
    User findById(Integer userId);
}

```

#### 一对多|多对多|注解



```java
import com.itheima.domain.User;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.mapping.FetchType;
import java.util.List;
//配置使用二级缓存,使用前要在主配置文件中开启
@CacheNamespace(blocking=true)
public interface IUserDao {
	//指定执行的查询语句
    //@Select查 @Insert增 @Delete删 @Update改
    @Select("select * from user")
    //配置实体类属性和查询结果的映射关系
    @Results(
        	//配置属性id,实现了该Results可以被复用
            id = "resultMap",value = {
            //配置映射关系, 当属性值和表字段相同时,可以省略相应的@Result映射不写
            //属性:id,值为true为主键
            @Result(id = true,column = "id",property = "id"),
            @Result(column = "username",property = "username"),
            @Result(column = "address" ,property = "address"),
            //配置一对多关系映射:
            //属性:column为需要传递的值,one对应@one注解,many对象@many注解,fetchType设置延迟加载
            //@One 注解(一对一)代替了<assocation> 标签,在注解中用来指定子查询返回单一对象。
            //@Many注解(一对多)代替了<collection> 标签,在注解中用来指定子查询返回对象集合。 
            @Result(property = "list",column ="id",
            many = @Many(select = "com.itheima.dao.IAccountDao.findByUid",
            fetchType = FetchType.LAZY))})
    List<User> findAll();

   	@Select("select * from user where id = #{uid} ")
    //引用id值为userMap的映射关系
	@ResultMap("userMap")
    User findById(Integer userId);
}

public interface IAccountDao {

	@Select("select * from account where uid = #{uid} ")
	List<Account> findByUid(Integer userId);
}
```

