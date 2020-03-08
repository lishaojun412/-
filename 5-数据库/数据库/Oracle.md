# Oracle基础 #

## 1 什么是oracle ##

	用户：管理表的最小单位      ----   对应Mysql数据库
	
	数据文件：存储用户表数据	
	
	表空间：多个数据文件组成表空间
	
	一个实例可以有多个表空间和多个用户 
	一个表空间可以有多个用户  多个数据文件
	一个数据文件可以有多个表信息
	一个用户可以有多个表信息

## 2 如何使用oracle使用（链接 / PL/sql） ##
# oracle表的基本操作 # 

## 1 表空间的操作 ## 

--创建表空间

	create tablespace itheima
	datafile 'c:\itheima.dbf'
	size 100m
	autoextend on
	next 10m;

--删除表空间
	drop tablespace itheima;

## 2 用户的操作

--创建用户

	create user itheima
	identified by itheima
	default tablespace itheima;

--给用户授权

--oracle数据库中常用角色

connect--连接角色，基本角色

resource--开发者角色

dba--超级管理员角色

--给itheima用户授予dba角色
	grant dba to itheima;

---切换到itheima用户下

---创建一个person表

	create table person(
	       pid number(20),
	       pname varchar2(10)
	);

### --开启Scott用户

----scott用户，密码itcast。

--解锁scott用户

```
alter user scott account unlock;
```

--解锁scott用户的密码【此句也可以用来重置密码】

```
alter user scott identified by itcast;
```

## 3 表的操作

---修改表结构

---添加一列
	alter table person add (gender number(1));

---修改列类型

	alter table person modify gender char(1);

---修改列名称

	alter table person rename column gender to sex;
---删除一列

	alter table person drop column sex;

---查询表中记录

	select * from person;
----添加一条记录

	insert into person (pid, pname) values (1, '小明');
	commit;

----修改一条记录

	update person set pname = '小马' where pid = 1;
	commit;

----三个删除

--删除表中全部记录

	delete from person;

--删除表结构

	drop table person;

--先删除表，再次创建表。效果等同于删除表中全部记录。

**--在数据量大的情况下，尤其在表中带有索引的情况下，该操作效率高。**

--索引可以提供查询效率，但是会影响增删改效率。

	truncate table person;

## 4 序列

**----序列不真的属于任何一张表，但是可以逻辑和表做绑定。在实际项目中每一张表会配一个序列**

----序列：默认从1开始，依次递增，主要用来给主键赋值使用。

```plsql
create sequence s_person;  		  --创建一个s_person的序列,
select s_person.nextval from dual; --查看序列 dual：虚表，只是为了补全语法，没有任何意义。
```

----添加一条记录,id值使用序列中的值

```plsql
insert into person (pid, pname) values (s_person.nextval, '小明'); --pid使用序列中的值
commit;
```

---结合触发器实现自增长

## 5 表的Select查询

### 单行函数：作用于一行，返回一个值。 ###

---字符函数

	select upper('yes') from dual;--YES
	select lower('YES') from dual;--yes

----数值函数

	select round(56.16, -2) from dual;---四舍五入，后面的参数表示保留的位数
	select trunc(56.16, -1) from dual;---直接截取，不在看后面位数的数字是否大于5.
	select mod(10, 3) from dual;---求余数

----日期函数

----查询出emp表中所有员工入职距离现在几天。

	select sysdate-e.hiredate from emp e;

----算出明天此刻

	select sysdate+1 from dual;

----查询出emp表中所有员工入职距离现在几月。

	select months_between(sysdate,e.hiredate) from emp e;

----查询出emp表中所有员工入职距离现在几年。

	select months_between(sysdate,e.hiredate)/12 from emp e;

----查询出emp表中所有员工入职距离现在几周。

	select round((sysdate-e.hiredate)/7) from emp e;

----转换函数
---日期转字符串

	select to_char(sysdate, 'fm yyyy-mm-dd hh24:mi:ss') from dual;

---字符串转日期

	select to_date('2018-6-7 16:39:50', 'fm yyyy-mm-dd hh24:mi:ss') from dual;

----通用函数
---算出emp表中所有员工的年薪
----奖金里面有null值，如果null值和任意数字做算术运算，结果都是null。

	select e.sal*12+nvl(e.comm, 0) from emp e;

---条件表达式
---条件表达式的通用写法，mysql和oracle通用

---给emp表中员工起中文名

	select e.ename, 
	       case e.ename
	         when 'SMITH' then '曹贼'
	           when 'ALLEN' then '大耳贼'
	             when 'WARD' then '诸葛小儿'
	               --else '无名'
	                 end
	from emp e;

---判断emp表中员工工资，如果高于3000显示高收入，如果高于1500低于3000显示中等收入，

-----其余显示低收入

	select e.sal, 
	       case 
	         when e.sal>3000 then '高收入'
	           when e.sal>1500 then '中等收入'
	               else '低收入'
	                 end
	from emp e;

----oracle中除了起别名，都用单引号。

----oracle专用条件表达式

	select e.ename, 
	        decode(e.ename,
	          'SMITH',  '曹贼',
	            'ALLEN',  '大耳贼',
	              'WARD',  '诸葛小儿',
	                '无名') "中文名"             
	from emp e;


### 多行函数【聚合函数】：作用于多行，返回一个值。 ###

	select count(1) from emp;---查询总数量
	select sum(sal) from emp;---工资总和
	select max(sal) from emp;---最大工资
	select min(sal) from emp;---最低工资
	select avg(sal) from emp;---平均工资


### 分组查询 ###

---查询出每个部门的平均工资

**---分组查询中，出现在group by后面的原始列，才能出现在select后面**

**---没有出现在group by后面的列，想在select后面，必须加上聚合函数。**

---聚合函数有一个特性，可以把多行记录变成一个值。

	select e.deptno, avg(e.sal)--, e.ename
	from emp e
	group by e.deptno;

---查询出平均工资高于2000的部门信息

	select e.deptno, avg(e.sal) asal
	from emp e
	group by e.deptno
	having avg(e.sal)>2000;

---所有条件都不能使用别名来判断。
--比如下面的条件语句也不能使用别名当条件

	select ename, sal s from emp where sal>1500;

---查询出每个部门工资高于800的员工的平均工资

	select e.deptno, avg(e.sal) asal
	from emp e
	where e.sal>800
	group by e.deptno;

**----where是过滤分组前的数据，having是过滤分组后的数据。**

**---表现形式：where必须在group by之前，having是在group by之后。**

---查询出每个部门工资高于800的员工的平均工资

---然后再查询出平均工资高于2000的部门

	select e.deptno, avg(e.sal) asal
	from emp e
	where e.sal>800
	group by e.deptno
	having avg(e.sal)>2000;

### 多表查询中的一些概念

---笛卡尔积

	select * from emp e, dept d;

---等值连接

	select * from emp e, dept d where e.deptno=d.deptno;

**---内连接**

	select * from emp e inner join dept d on e.deptno = d.deptno;

---查询出所有部门，以及部门下的员工信息。【**外连接**】
	select * from emp e right join dept d on e.deptno=d.deptno;

---查询所有员工信息，以及员工所属部门

	select * from emp e left join dept d on e.deptno=d.deptno;

---oracle中专用外连接

	select * from emp e, dept d where e.deptno(+) = d.deptno;

---查询出员工姓名，员工领导姓名

---自连接：自连接其实就是站在不同的角度把一张表看成多张表。

	select e1.ename, e2.ename from emp e1, emp e2 where e1.mgr = e2.empno;

------查询出员工姓名，员工部门名称，员工领导姓名，员工领导部门名称

	select e1.ename, d1.dname, e2.ename, d2.dname
	from emp e1, emp e2, dept d1, dept d2
	where e1.mgr = e2.empno
	and e1.deptno=d1.deptno
	and e2.deptno=d2.deptno;

**---子查询**

---子查询返回一个值

---查询出工资和SCOTT一样的员工信息

	select * from emp where sal in (select sal from emp where ename = 'SCOTT')

---子查询返回一个集合

---查询出工资和10号部门任意员工一样的员工信息

	select * from emp where sal in (select sal from emp where deptno = 10);

---子查询返回一张表

---查询出每个部门最低工资，和最低工资员工姓名，和该员工所在部门名称

---1，先查询出每个部门最低工资

	select deptno, min(sal) msal from emp group by deptno;

---2，三表联查，得到最终结果。

	select t.deptno, t.msal, e.ename, d.dname
	from (select deptno, min(sal) msal
	      from emp 
	      group by deptno) t, emp e, dept d
	where t.deptno = e.deptno
	and t.msal = e.sal
	and e.deptno = d.deptno;

**----oracle中的分页**

---rownum行号：当我们做select操作的时候，

--每查询出一行记录，就会在该行上加上一个行号，

**--行号从1开始，依次递增，不能跳着走。**

**----排序操作会影响rownum的顺序**

	select rownum, e.* from emp e order by e.sal desc

----如果涉及到排序，但是还要使用rownum的话，我们可以再次嵌套查询。

	select rownum, t.* from(
	select rownum, e.* from emp e order by e.sal desc) t;

----emp表工资倒叙排列后，每页五条记录，查询第二页。

----rownum行号不能写上大于一个正数。

	select * from(
	    select rownum rn, tt.* from(
	          select * from emp order by sal desc
	    ) tt where rownum<11
	) where rn>5

**--按照主键分页显示(empno)**

select * 
from (select rownum r,t.* from emp t where rownum<11)
where r>5

**--分页显示(薪资)排名在6-10位的人员信息**
select * 
from(select rownum r,t.* 
	  from (select * from emp order by sal desc) t
       	  where rownum<11)
where r>5

### Select语句执行顺序 ###

	from--where--group by--having--select--order by,  
	from:需要从哪个数据表检索数据 
	where:过滤表中数据的条件 
	group by:如何将上面过滤出的数据分组 
	having:对上面已经分组的数据进行过滤的条件  
	select:查看结果集中的哪个列，或列的计算结果 
	order by :按照什么样的顺序来查看返回的数据 
	 
	2.from后面的表关联，是自右向左解析的 
	而where条件的解析顺序是自下而上的。 

# 视图

---视图的概念：视图就是提供一个查询的窗口，所有数据来自于原表。
**---对原有表的引用，用户敏感数据隐藏，以及数据同步。**

---查询语句创建表

```
create table emp as select * from scott.emp;
select * from emp;
```

---创建视图【必须有dba权限】

```
create view v_emp as select ename, job from emp;
```

---查询视图

```
select * from v_emp;
```

---修改视图[不推荐]

```
update v_emp set job='CLERK' where ename='ALLEN';
commit;
```

---创建只读视图

```
create view v_emp1 as select ename, job from emp with read only;
```

---视图的作用？

**---第一：视图可以屏蔽掉一些敏感字段。**

**---第二：保证总部和分部数据及时统一。**

# 索引

**--索引的概念：索引就是在表的列上构建一个二叉树**

**----达到大幅度提高查询效率的目的，但是索引会影响增删改的效率。(增删改时需要重写构建二叉树)**

## 1 单列索引

---创建单列索引

```
create index idx_ename on emp(ename);
```

**---单列索引触发规则，条件必须是索引列中的原始值。**

**---单行函数，模糊查询，都会影响索引的触发。**

```
select * from emp where ename='SCOTT'
```

## 2 复合索引

---创建复合索引

```
create index idx_enamejob on emp(ename, job);
```

**---复合索引中第一列为优先检索列**

**---如果要触发复合索引，必须包含有优先检索列中的原始值。**

```
select * from emp where ename='SCOTT' and job='xx';---触发复合索引
select * from emp where ename='SCOTT' or job='xx';---不触发索引
select * from emp where 	ename='SCOTT';---触发单列索引。
```

# pl/sql编程语言

---pl/sql编程语言是对sql语言的扩展，使得sql语言具有过程化编程的特性。

---pl/sql编程语言比一般的过程化编程语言，更加灵活高效。

**---pl/sql编程语言主要用来编写存储过程和存储函数等。**

---声明方法

---赋值操作可以使用:=也可以使用into查询语句赋值

```
declare
    i number(2) := 10;
    s varchar2(10) := '小明';
    ena emp.ename%type;---引用型变量
    emprow emp%rowtype;---记录型变量
begin
	dbms_output.put_line(i);
	dbms_output.put_line(s);
	select ename into ena from emp where empno = 7788;
	dbms_output.put_line(ena);
	select * into emprow from emp where empno = 7788;
	dbms_output.put_line(emprow.ename || '的工作为：' || emprow.job); -- 使用||拼接字符串
end;
```

### pl/sql中的if判断

---输入小于18的数字，输出未成年

---输入大于18小于40的数字，输出中年人

---输入大于40的数字，输出老年人

```
declare
  i number(3) := &ii;
begin
  if i<18 then
    dbms_output.put_line('未成年');
  elsif i<40 then
    dbms_output.put_line('中年人');
  else
    dbms_output.put_line('老年人');
  end if;
end;
```

### pl/sql中的loop循环

---用三种方式输出1到10 10个数字

---while循环

```
declare
  i number(2) := 1;
begin
  while i<11 loop
     dbms_output.put_line(i);
     i := i+1;
  end loop;  
end;
```

---exit循环

```
declare
  i number(2) := 1;
begin
  loop
    exit when i>10;
    dbms_output.put_line(i);
    i := i+1;
  end loop;
end;
```

---for循环

```
declare

begin
  for i in 1..10 loop
     dbms_output.put_line(i);  
  end loop;
end;
```

### 游标

游标：可以存放多个对象，多行记录。**游标相当于数组**

---输出emp表中所有员工的姓名

```plsql
declare
  cursor c1 is select * from emp; --定义游标,存放数据
  emprow emp%rowtype;
begin
  open c1;						--遍历游标,操作数据
     loop
         fetch c1 into emprow;
         exit when c1%notfound;
         dbms_output.put_line(emprow.ename);
     end loop;
  close c1;
end;
```

-----给指定部门员工涨工资

```
declare
  cursor c2(eno emp.deptno%type) 
  is select empno from emp where deptno = eno;
  en emp.empno%type;
begin
  open c2(10);
     loop
        fetch c2 into en;
        exit when c2%notfound;
        update emp set sal=sal+100 where empno=en;
        commit;
     end loop;  
  close c2;
end;
```

----查询10号部门员工信息

```
select * from emp where deptno = 10;
```

## 1 存储过程Stored Procedure

--存储过程：存储过程就是提前已经编译好的一段pl/sql语言，放置在数据库端

--可以直接被调用。这一段pl/sql一般都是固定步骤的业务。

**--包括Procedure和Function两种定义方式**

### procedure存储过程

----给指定员工涨100块钱

```
create or replace procedure p1(eno emp.empno%type)
is
begin
   update emp set sal=sal+100 where empno = eno;
   commit;
end;
```

----测试p1

```
declare
begin
  p1(7788);
end;
```

### function存储函数

----通过存储函数实现计算指定员工的年薪

----存储过程和存储函数的参数都不能带长度

----存储函数的返回值类型不能带长度

```
create or replace function f_yearsal(eno emp.empno%type) return number
is
  s number(10);     
begin
  select sal*12+nvl(comm, 0) into s from emp where empno = eno;
  return s;
end;
```

----测试f_yearsal

----存储函数在调用的时候，返回值需要接收。

```
declare
  s number(10); 
begin
  s := f_yearsal(7788);
  dbms_output.put_line(s);
end;
```

---out类型参数如何使用

---使用存储过程来算年薪

```
create or replace procedure p_yearsal(eno emp.empno%type, yearsal out number)
is
   s number(10);
   c emp.comm%type;
begin
   select sal*12, nvl(comm, 0) into s, c from emp where empno = eno;
   yearsal := s+c;
end;
```

---测试p_yearsal

```
declare
  yearsal number(10);
begin
  p_yearsal(7788, yearsal);
  dbms_output.put_line(yearsal);
end;
```

----in和out类型参数的区别是什么？

---凡是涉及到into查询语句赋值或者:=赋值操作的参数，都必须使用out来修饰。

### 存储过程procedure和存储函数function的区别

---语法区别：关键字不一样，

------------存储函数比存储过程多了两个return。

---本质区别：存储函数有返回值，而存储过程没有返回值。

----------如果存储过程想实现有返回值的业务，我们就必须使用out类型的参数。

----------即便是存储过程使用了out类型的参数，其本质也不是真的有了返回值，

----------而是在存储过程内部给out类型参数赋值，在执行完毕后，我们直接拿到输出类型参数的值。

----我们可以使用存储函数有返回值的特性，来自定义函数。

----而存储过程不能用来自定义函数。

----案例需求：查询出员工姓名，员工所在部门名称。

----案例准备工作：把scott用户下的dept表复制到当前用户下。

```
create table dept as select * from scott.dept;
```

----使用传统方式来实现案例需求

```
select e.ename, d.dname
from emp e, dept d
where e.deptno=d.deptno;
```

----使用存储函数来实现提供一个部门编号，输出一个部门名称。

```
create or replace function fdna(dno dept.deptno%type) return dept.dname%type
is
  dna dept.dname%type;
begin
  select dname into dna from dept where deptno = dno;
  return dna;
end;
```

---使用fdna存储函数来实现案例需求：查询出员工姓名，员工所在部门名称。

```
select e.ename, fdna(e.deptno)
from emp e;
```

## 2 触发器

----触发器就是制定一个规则，在我们做增删改操作的时候，只要满足该规则，自动触发，无需调用。(监听器)

----语句级触发器：不包含有for each row的触发器。

----行级触发器：包含有for each row的就是行级触发器。

**-----------加for each row是为了使用:old或者:new对象或者一行记录。**

### 语句级触发器

----插入一条记录，输出一个新员工入职

```
create or replace trigger t1
after
insert
on person
declare

begin
  dbms_output.put_line('一个新员工入职');
end;
```

---触发t1

```
insert into person values (1, '小红');
commit;
select * from person;
```

### **行级别触发器**

---不能给员工降薪(抛异常)

---raise_application_error(-20001~-20999之间, '错误提示信息');

```
create or replace trigger t2
before
update
on emp
for each row
declare

begin
  if :old.sal>:new.sal then
     raise_application_error(-20001, '不能给员工降薪');
  end if;
end;
```

----触发t2

```
select * from emp where empno = 7788;
update emp set sal=sal-1 where empno = 7788;
commit;
```

### 触发器实现主键自增

---分析：在用户做插入操作的之前，在主键中插入序列值, 【需要结合序列】

------定义触发器给该数据中的主键列赋值。

```plsql
create or replace trigger auid
before
insert
on person
for each row
declare

begin
  select s_person.nextval into :new.pid from dual;   --需要需要为该表创建一个s_person序列
end;
```

--查询person表数据

```
select * from person;
```

---使用auid实现主键自增

```
insert into person (pname) values ('a');
commit;
insert into person values (1, 'b');
commit;
```

# OJDBC

**----oracle10g    ojdbc14.jar**
----oracle11g    ojdbc6.jar

mvn install:install-file -DgroupId=com.oracle -DartifactId=ojdbc14 -Dversion=10.2.0.4.0 -Dpackaging=jar -Dfile=ojdbc14.jar

## 1 oracle数据库链接信息

```
String driver = "oracle.jdbc.driver.OracleDriver";
String url = "jdbc:oracle:thin:@192.168.80.88:1521:orcl";
String username = "scott";
String password = "itcast";
```

## 2 使用oracle 完成基本的数据库操作

```
String url="jdbc:oracle:thin:@192.168.80.88:1521:orcl";
    String user="itheima";
    String pass="itheima";

    //1.注册驱动
    Class.forName("oracle.jdbc.driver.OracleDriver");
    //2.获得链接
    Connection conn = DriverManager.getConnection(url, user, pass);
    //3.创建preperstatment
    PreparedStatement ps = conn.prepareStatement("select * from emp where empno = ?");
    ps.setString(1,"7788");
    //4.执行sql语句
    ResultSet resultSet = ps.executeQuery();
    //遍历集合
    while(resultSet.next()){
        Object ename = resultSet.getObject("ename");
        System.out.println(ename);
    }
    //5.关闭链接  -- 先开后关
    resultSet.close();
    ps.close();
    conn.close();

```

## 3 使用jdbc调用oracle存储过程和存储函数

 {?= call <procedure-name>[(<arg1>,<arg2>, ...)]}  -- 存储函数
 {call <procedure-name>[(<arg1>,<arg2>, ...)]} --存储过程的调用

```
     //1.注册驱动
    Class.forName("oracle.jdbc.driver.OracleDriver");
    //2.获得链接
    Connection conn = DriverManager.getConnection(url, user, pass);
    //3.创建preperstatment
    /**
     * {?= call <procedure-name>[(<arg1>,<arg2>, ...)]}  -- 存储函数
     *    {call <procedure-name>[(<arg1>,<arg2>, ...)]} --存储过程的调用
     *    procedure-name
     *    declare
     * 	  s number(10);
     * 	begin
     * 	  s := f_yearsal(7788);
     * 	  dbms_output.put_line(s);
     * 	end;
     * p_yearsal(eno emp.empno%type, yearsal out number)
     */
    CallableStatement ps = conn.prepareCall("{call P_YEARSAL(?,?)}");
    ps.setString(1,"7788");  //in
    //因为第二个之是out值，所以需要制定值得类型以及位置
    ps.registerOutParameter(2, OracleTypes.NUMBER);  // out
    //4.执行sql语句
    ps.execute();
    //遍历集合
    Object yearsal = ps.getObject(2);
    System.out.println("年薪:"+yearsal);
    ps.close();
    conn.close();
```

## 4 使用mybatis调用存储过程以及存储函数

**原理:mybatis调用存储过程和存储函数都是通过传递一个Map集合,mybatis从Map集合中获取参数,并将结果封装到Map结合中**

**-- xml配置文件中的mode和jdbcType都要时大写**

```
存储函数调用
1） <!-- statementType="CALLABLE" -->
<select id="f_YEARSAL" statementType="CALLABLE">
    {#{yearsal ,mode=OUT,jdbcType=DOUBLE}= call f_yearsal(#{eno,mode=IN,jdbcType=DOUBLE})}
</select>
2）测试
	@Test
	public void test(){
    	Map<String,Object> map = new HashMap<>();
    	map.put("yearsal",null);
    	map.put("eno","7788");
    	itemsMapper.f_YEARSAL(map);		    -- 将参数和返回值放到Map集合并作为参数传递给mybatis
    	Object yearsal = map.get("yearsal"); --获取Map结合中的属性
    	System.out.println(yearsal);
	}
	
存储过程调用
1) <!-- statementType="CALLABLE" -->
<select id="d_YEARSAL" statementType="CALLABLE">
    {call f_yearsal(#{eno,mode=IN,jdbcType=DOUBLE},#{yearsal ,mode=OUT,jdbcType=DOUBLE})}
</select>
2)	@Test
    public void tes2(){
        Map<String,Object> map = new HashMap<>();
        map.put("eno","7788");
        map.put("yearsal",null);
        itemsMapper.f_YEARSAL(map);
        Object yearsal = map.get("yearsal");
        System.out.println(yearsal);
    }
```



# 重点内容总结：

```
1.oracle 子查询  （重点）
2.oracle分页查询（面试题） (重点)
3.了解视图的概念以及视图的使用场景（屏蔽敏感字段，数据同步） create view (视图名称) AS 创建视图的例信息（了解）
4.了解索引概念，已经索引优势。  create index (索引名称) on 索引字段（了解）
5.存储过程   ---  一段写好得pl/sql脚本 （无返回值）  （了解）
6.存储函数	 ---  一段写好得pl/sql脚本 （有返回值）（了解）
7.触发器    ----  在执行增删改前或者之后后，触发一段pl/sql脚本 (了解)
8.基于mybatis调用存储过程以及存储函数得流程   ----  （理解）

```

练习

1.查询所有部门得平均工资

select d.dname ,a
from (select deptno dno,avg(sal) a from emp  group by deptno)
left join  dept d on dno = d.deptno

2.查询指定员工得部门信息以及薪资等级

select e.ename,e.sal,d.*,
       case
         when e.sal>3000 then 'high'
           when e.sal>1500 then 'middle'
             else 'low'
               end
from emp e,dept d  
where e.empno=7788 and e.deptno=d.deptno;

3.查询工资最低得五名员工得姓名以及部门名称

select  *
from (select ename,dname,sal
     from emp e, dept d
     where e.deptno = d.deptno
     order by sal ) t
where rownum<6;












