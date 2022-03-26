# 1. 了解Spring Security

## 1.1 核心功能

1. 认证（你是谁，用户/设备/系统）
2. 验证（你能干什么，也叫权限控制/授权，允许执行的操作）

## 1.2 原理

基于Filter , Servlet, AOP 实现身份认证和权限验证

SpringSecurity本质上是一个过滤器链

主要有三个过滤器

### 1.2.1 FilterSecurityInterceptor

是一个方法级的权限过滤器, 基本位于过滤链的最底部

### 1.2.2 UsernamePasswordAuthenticationFilter

对/login的POST请求做拦截，校验表单中用户名，密码。

### 1.2.3 ExceptionTranslationFilter

是个异常过滤器，用来处理在认证授权过程中抛出的异常



# 2. 实例驱动教学

## 2.1 项目初探

### 2.1.1 环境配置

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### 2.1.2 书写Controller

```java
@RestController
@RequestMapping("/hello")
public class HelloSecurityController {
    @RequestMapping("/world")
    public String sayHello(){
        return "Hello";
    }
}
```

### 2.1.3 进行登录

在访问http://localhost:8080/hello/world会进行拦截，要先进行登录才行

![image-20210429191459348](https://gitee.com/aik-aid/picture/raw/master/image-20210429191459348.png)

用户名为：user

密码：由日志自动生成

### 2.1.4 自定义用户名和密码

在application.yml(properties)中设置

```yaml
spring:
  security:
    user:
      name: sls
      password: 123
```

### 2.1.5 关闭验证功能

排除Secuirty 的配置，让他不启用

```java
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class FirstApplication { }
```



## 2.2 使用内存中的用户信息设置用户名和密码

### 2.2.1 思路

`WebSecurityConfigurerAdapter` 这个类控制安全管理的内容

需要做的使用：继承WebSecurityConfigurerAdapter，重写方法。实现自定义的认证信息。重写下面的方法。

且这种方式会覆盖在yaml中的定义

```java
protected void configure(AuthenticationManagerBuilder auth)
```

### 2.2.2 代码

下述代码会报错，因为spring security 5 版本要求密码比较加密，不能使用明文

```java
@Configuration
@EnableWebSecurity
public class MyWebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("sls").password("c8d3e970-a0aa-4701-9f2d-c4d953dc0466.").roles();
        auth.inMemoryAuthentication().withUser("asd").password("c8d3e970-a0aa-4701-9f2d-c4d953dc0466").roles();
        auth.inMemoryAuthentication().withUser("qwe").password("c8d3e970-a0aa-4701-9f2d-c4d953dc0466.").roles();
    }
}
```

注解：
1. @Configuration ：表示当前类是一个配置类（相当于是spring 的xml配置文件），在这个类方法的返回值是java 对象，这些对象放入到spring 容器中。
2. @EnableWebSecurity：表示启用spring security 安全框架的功能
3. @Bean：把方法返回值的对象，放入到spring 容器中。

### 2.2.3 密码加密

spring security 5 版本要求密码比较加密，否则报错

```java
java.lang.IllegalArgumentException: There is no PasswordEncoder mapped for the id "null"
```

实现方法

**注意：必须使用@Bean注入PasswordEncoder对象，不然无法解密**

```java
@Configuration
@EnableWebSecurity
public class MyWebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        PasswordEncoder pe = passwordEncoder();
        auth.inMemoryAuthentication().withUser("sls").password(pe.encode("1234")).roles();
        auth.inMemoryAuthentication().withUser("asd").password(pe.encode("1234")).roles();
        auth.inMemoryAuthentication().withUser("qwe").password(pe.encode("1234")).roles();
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
```



## 2.3 内存中基于角色Role 的身份认证

同一个用户可以有不同的角色。同时可以开启对方法级别的认证。

### 2.3.1 步骤

1. 设置用户的角色

```java
auth.inMemoryAuthentication()
    .withUser("admin")
    .password(pe.encode("admin"))
    .roles("admin","normal");
```

2. 在配置类上面加上启用方法级别的注解

```java
@EnableGlobalMethodSecurity(prePostEnabled = true)
//true表示可以使用@PreAuthorize注解和@PostAuthorize
```

3. 在处理器方法的上面加入角色的信息，指定方法可以访问的角色列表

> 使用@PreAuthorize 指定在方法之前进行角色的认证。hasAnyRole('角色名称1','角色名称N')

```java
@RequestMapping("/helloUser")
@PreAuthorize(value = "hasAnyRole('admin','normal')")
public String helloCommonUser(){
    return "Hello 拥有normal, admin 角色的用户";
}
```



## 2.4 基于jdbc的用户认证

### 2.4.1 获取的信息

从数据库mysql 中获取用户的身份信息

1. 用户名称
2. 密码
3. 角色

### 2.4.2 UserDetails

#### 介绍

在Spring Security 框架对象用户信息的表示类是`UserDetails`.
`UserDetails` 是一个接口，高度抽象的用户信息类（相当于项目中的User 类）

```java
public interface UserDetails extends Serializable {
    Collection<? extends GrantedAuthority> getAuthorities();

    String getPassword();

    String getUsername();

    boolean isAccountNonExpired();

    boolean isAccountNonLocked();

    boolean isCredentialsNonExpired();

    boolean isEnabled();
}
```

`User 类`：是UserDetails 接口的实现类， 构造方法有三个参数：username，password, authorities

```java
public User(String username, String password, Collection<? extends GrantedAuthority> authorities) {
    this(username, password, true, true, true, true, authorities);
}
```



需要向spring security 提供User 对象， 这个对象的数据来自数据库的查询。

### 2.4.3 UserDetailsService

实现UserDetailsService接口

重写方法

```java
UserDetails loadUserByUsername(String var1)
```

在方法中获取数据库中的用户信息， 也就是执行数据库的查询，条件是用户名称。

### 2.4.4 实现步骤

1. 引入依赖

```xml
<dependencies>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
    </dependency>
    <!--        数据库访问框架-->
    <dependency>
        <groupId>org.mybatis.spring.boot</groupId>
        <artifactId>mybatis-spring-boot-starter</artifactId>
        <version>2.1.4</version>
    </dependency>
    <dependency>
        <groupId>io.springfox</groupId>
        <artifactId>springfox-swagger-ui</artifactId>
        <version>2.9.2</version>
    </dependency>
    <dependency>
        <groupId>io.springfox</groupId>
        <artifactId>springfox-swagger2</artifactId>
        <version>2.9.2</version>
    </dependency>
</dependencies>
```

2. 配置相对应的mybatis 的环境以及映射文件等

   注意insert时要对密码进行加密

   ```java
       @Override
       public int insertUser(UserInfo userInfo) {
           BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
           userInfo.setPassword(encoder.encode(userInfo.getPassword()));
           return userSecurityMapper.insertUser(userInfo);
       }
   ```

   

3. 编写UserDetailsService

```java
@Component
public class MyUserDetailService implements UserDetailsService {
    @Autowired
    private UserSecurityMapper userSecurityMapper;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = null;
        UserInfo userInfo = null;

        if (username != null) {
            userInfo = userSecurityMapper.SelectByName(username);

            if (userInfo != null) {

                List<GrantedAuthority> list = new ArrayList<>();
                GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + userInfo.getRole());
                list.add(authority);

                user = new User(userInfo.getUsername(), userInfo.getPassword(), list);
            }
        }
        return user;
    }
}
```

4. 编写WebSecurityConfig配置文件

```java
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MyWebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }
}

```



# 3. 基于角色的权限

## 3.1 认证和授权

### 认证

authentication：认证

认证访问者是谁；判断一个用户是不是当前系统的有效用户

### 授权

authorization：授权

访问者能做什么

### 举例

认证

> ​	比如说张三用户要访问一个公司oa系统。首先系统要判断张三是不是公司中的有效用户。认证张三是不是有效的用户，是不是公司的职员

授权

> ​	判断张三能否做某些操作。
>
> 	如果张三只是一个普通用户，只能看自己的相关数据， 只能提交请假申请等等。
>
> ```
> 如果张三是个领导可以批准下级的请假， 其他的操作。
> ```

​	

## 3.2 RBAC

### 3.2.1 概念

RBAC 是`基于角色的访问控制`（Role-Based Access Control ）

> 在RBAC 中，权限与角色相关联，用户通过成为适当角色的成员而得到这些角色的权限。这就极大地简化了权限的管理。这样管理都是层级相互依赖的，权限赋予给角色，而把角色又赋予用户，这样的权限设计很清楚，管理起来很方便。

> 其基本思想是，对系统操作的各种权限不是直接授予具体的用户，而是在用户集合与权限集合之间建立一个角色集合。每一种角色对应一组相应的权限。一旦用户被分配了适当的角色后，该用户就拥有此角色的所有操作权限。这样做的好处是，不必在每次创建用户时都进行分配权限的操作，只要分配用户相应的角色即可，而且角色的权限变更比用户的权限变更要少得多，这样将简化用户的权限管理，减少系统的开销。

RBAC： 用户是属于角色的， 角色拥有权限的集合。用户属于某个角色， 他就具有角色对应的权限。

> **权限**：能对资源的操作， 比如增加，修改，删除，查看等等。
> **角色**：自定义的， 表示权限的集合。一个角色可以有多个权限。

### 3.2.2 RBAC设计的表

1. **用户表**： 用户认证（登录用到的表）
用户名，密码，是否启用，是否锁定等信息。
2. **角色表**：定义角色信息角色名称， 角色的描述。
3. **用户和角色的关系表**： 用户和角色是多对多的关系。
   一个用户可以有多个角色， 一个角色可以有多个用户。
4. **权限表**
5.  **角色和权限的关系表**

## 3.3 spring specurity 中认证的接口和类

###  3.3.1 UserDetails接口

```java
public interface UserDetails extends Serializable {
    Collection<? extends GrantedAuthority> getAuthorities();//权限集合

    String getPassword();

    String getUsername();

    boolean isAccountNonExpired();		//账号是否没过期

    boolean isAccountNonLocked();		//账号是否没锁定

    boolean isCredentialsNonExpired();	//证书是否没过期

    boolean isEnabled();				//账号是否启用
}
```

可以：自定义类实现UserDetails 接口，作为你的系统中的用户类。这个类可以交给spring security 使用。

系统提供的实现类是User类

### 3.3.2 UserDetailsService 接口

#### 接口

主要作用：获取用户信息，得到是`UserDetails` 对象。一般项目中都需要自定义类实现这个接口，从数据库中获取数据。

方法需要实现：

```java
UserDetails loadUserByUsername(String var1)
```

#### 实现类

1. **InMemoryUserDetailsManager**：在内存中维护用户信息。

> 优点：使用方便。
> 缺点：数据不是持久的。系统重启后数据恢复原样。

2. **JdbcUserDetailsManager** ：用户信息存放在数据库中，底层使用jdbcTemplate 操作数据库。可以JdbcUserDetailsManager 中的方法完成用户的管理



## 3.4 自定义

### 3.4.0 实现步骤

1. 创建对应的表

2. 导入依赖

   1. spring-security
   2. spring-web
   3. spring和mybatis的相关依赖
   4. mysql驱动

3. 编写主配置文件

   1. 连接数据库，创建连接池

4. 创建自己的User类，代替UserDetails

5. 创建自定义的UserDetailsService类

   1. 在重写的方法中，查询数据库获取用户信息，获取角色信息 ，构建UserDetails实现类对象

6. 定义配置类，创建类继承WebSecurityConfigurerAdapter

   自定义安全的配置

7. 自定义登录
   1. 传统的form登录
   2. ajax登录
8. 创建Controller

### 3.4.1 自定义表

#### 用户表

```mysql
create table user(
    id int primary key auto_increment,
    username varchar(100),
    password varchar(100),
    realname varchar(200),
    isnoenable int,
    isnolock int,
    iscredentials int,
    create_time date,
    login_tine date
);
```

#### 角色表

```mysql
create table role(
    id int primary key auto_increment,
    rolename varchar(255),
    rolememo varchar(255)
);
```

#### 用户-角色关系表

```mysql
create table user_role(
    id int primary key ,
    user_id int,
    role_id int,
    foreign key(user_id) references user(id),
    foreign key(role_id) references role(id)
);
```

### 3.4.2 添加依赖

```xml
    <dependencies>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>
        
<!--        数据库访问框架-->
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>2.1.4</version>
        </dependency>
        
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger-ui</artifactId>
            <version>2.9.2</version>
        </dependency>
        
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger2</artifactId>
            <version>2.9.2</version>
        </dependency>
    </dependencies>
```

### 3.4.3 连接数据库

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/learn_spring_security?useUnicode=true&characterEncoding=UTF-8&serverTimezone=GMT%2B8&useSSL=false&allowMultiQueries=true
    password: Aa123Aa123
    username: root
```

### 3.4.4 创建实体类

```java
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo implements UserDetails{
    private String username;
    private String password;
    private String realname;

    private boolean isAccountNonExpired;
    private boolean isAccountNonLocked;
    private boolean isCredentialsNonExpired;
    private boolean isEnabled;

    private Date createTime;
    private Date loginTime;
    private List<GrantedAuthority> authorities;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        isAccountNonExpired = accountNonExpired;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        isAccountNonLocked = accountNonLocked;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        isCredentialsNonExpired = credentialsNonExpired;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }
}

```

```java
public class Role {
    private int id;
    private String roleName;
    private String rolememo;

    public Role() {
    }

    public Role(int id, String roleName, String rolememo) {
        this.id = id;
        this.roleName = roleName;
        this.rolememo = rolememo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRolememo() {
        return rolememo;
    }

    public void setRolememo(String rolememo) {
        this.rolememo = rolememo;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", roleName='" + roleName + '\'' +
                ", rolememo='" + rolememo + '\'' +
                '}';
    }
}
```

**记得编写Mybatis相对的数据库操作**

### 3.4.5 自定义UserDetailsService类

```java
@Service
public class MyUserDetailService implements UserDetailsService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleMapper roleMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //1.根据username获取User信息
        User user = userMapper.selectByName(username);
        if (user == null)
            return null;
        //2.根据userid获取role信息
        List<Role> roleslsit = roleMapper.selectRoleByUserId(user.getId());
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roleslsit) {
            String roleName = role.getRoleName();
            GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + roleName);
            authorities.add(authority);
        }
        user.setAuthorities(authorities);
        return user;
    }
}
```

### 3.4.6 定义配置类

```java
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class CustomSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }
}
```

### 3.4.7 自定义登陆页面

#### 配置类的修改

```java
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class CustomSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        System.out.println("authentication 登录");
        auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()//自定义自己编写的登录界面
            	.permitAll()
                .loginPage("/login.html")//登录页面设置 不需要写Controller 框架自动实现
                .loginProcessingUrl("/user/login")//登录访问路径
                .defaultSuccessUrl("/test/index")//登录成功之后，跳转路径
                .and()
            .authorizeRequests()
                .antMatchers("/","/test/hello","/user/login").permitAll()//设置哪些不需要认证，可以直接访问
                .anyRequest().authenticated()
                .and()
            .csrf().disable();
    }
}
```

#### 创建相关页面

```html
<!DOCTYPE html>
<!-- 需要添加
<html  xmlns:th="http://www.thymeleaf.org">
这样在后面的th标签就不会报错
 -->
<html xmlns:th="http://www.thymeleaf.org">
<head lang="en">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>xx</title>
</head>
<body>


<h1>表单提交</h1>
<!-- 表单提交用户信息,注意字段的设置,直接是*{} -->
<form action="/user/login" method="post">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    <input type="text" name="username"/>
    <input type="text" name="password"/>
    <input type="submit"/>
</form>
</body>
</html>
```

# 4. 配置类

```java
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //配置认证方式等
        super.configure(auth);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //http相关的配置，包括登入登出、异常处理、会话管理等
        super.configure(http);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
    }
}
```

## 4.1 认证管理器配置方法

`void configure(AuthenticationManagerBuilder auth)` 

用来配置认证管理器`AuthenticationManager`。说白了就是所有 `UserDetails` 相关的它都管，包含 `PasswordEncoder` 密码机。

## 4.2 核心过滤器配置方法

`void configure(WebSecurity web)` 

用来配置 `WebSecurity` 。而 `WebSecurity` 是基于 `Servlet Filter` 用来配置 `springSecurityFilterChain` 。而 `springSecurityFilterChain` 又被委托给了 **Spring Security 核心过滤器 Bean** `DelegatingFilterProxy` 。 相关逻辑你可以在 `WebSecurityConfiguration` 中找到。我们一般不会过多来自定义 `WebSecurity` , 使用较多的使其`ignoring()` 方法用来忽略 **Spring Security** 对静态资源的控制。

## 4.3 安全过滤器链配置方法

`void configure(HttpSecurity http)` 

这个是我们使用最多的，用来配置 `HttpSecurity` 。 `HttpSecurity` 用于构建一个安全过滤器链 `SecurityFilterChain` 。`SecurityFilterChain` 最终被注入**核心过滤器** 。 `HttpSecurity` 有许多我们需要的配置。我们可以通过它来进行自定义安全访问策略。

```java
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()//自定义自己编写的登录界面
            	.permitAll()
                .loginPage("/login.html")//登录页面设置 不需要写Controller 框架自动实现
                .loginProcessingUrl("/user/login")//登录访问路径
                .defaultSuccessUrl("/test/index")//登录成功之后，跳转路径
                .and()
            .authorizeRequests()
                .antMatchers("/","/test/hello","/user/login").permitAll()//设置哪些不需要认证，可以直接访问
                .anyRequest().authenticated()
                .and()
            .csrf()
            	.disable();
    }
```





# 5. url的Ant风格

## 1. 前言

我们经常在读到一些文章会遇到`uri` 支持 `Ant` 风格 ，而且这个东西在 **Spring MVC** 和 **Spring Security** 中经常被提及。这到底是什么呢？今天我们来学习了解一下。这对我们学习 **Spring MVC** 和 **Spring Security** 十分必要。

## 2. Ant 风格

说白了 `Ant` 风格就是一种路径匹配表达式。主要用来对`uri`的匹配。其实跟正则表达式作用是一样的，只不过正则表达式适用面更加宽泛，`Ant`仅仅用于路径匹配。

## 3. Ant 通配符

`Ant` 中的通配符有三种：

- `?` 匹配任何单字符

- `*` 匹配0或者任意数量的 **字符**

- `**` 匹配0或者更多的 **目录**

  这里注意了单个`*` 是在一个目录内进行匹配。 而`**` 是可以匹配多个目录，一定不要迷糊。

  ### 3.1 Ant 通配符示例

  | 通配符 | 示例           | 说明                                                         |
  | :----- | :------------- | :----------------------------------------------------------- |
  | `?`    | `/ant/p?ttern` | 匹配项目根路径下 `/ant/pattern` 和 `/ant/pXttern`,**但是不包括**`/ant/pttern` |
  | `*`    | `/ant/*.html`  | 匹配项目根路径下所有在`ant`路径下的`.html`文件               |
  | `*`    | `/ant/*/path`  | `/ant/path`、`/ant/a/path`、`/ant/bxx/path` 都匹配，不匹配 `/ant/axx/bxx/path` |
  | `**`   | `/ant/**/path` | `/ant/path`、`/ant/a/path`、`/ant/bxx/path` 、`/ant/axx/bxx/path`都匹配 |

  ### 3.2 最长匹配原则

  从 3.1 可以看出 `*` 和 `**` 是有冲突的情况存在的。为了解决这种冲突就规定了最长匹配原则(has more characters)。 一旦一个`uri` 同时符合两个`Ant`匹配那么走匹配规则字符最多的。为什么走最长？因为字符越长信息越多就越具体。比如 `/ant/a/path` 同时满足 `/**/path` 和 `/ant/*/path` 那么走`/ant/*/path`

  ## 4. Spring MVC 和 Spring Security 中的 Ant 风格

  接下来我们来看看 **Spring MVC** 和 **Spring Security** 下的 `Ant`风格。

  ### 4.1 Spring MVC 中的 Ant 风格

  这里也提一下在 **Spring MVC** 中 我们在控制器中写如下接口：

  ```
      /**
       * ant style test.
       *
       * @return the string
       */
      @GetMapping("/?ant")
      public String ant() {
  
          return "ant";
      }
  ```

  你使用任意合法`uri`字符替代`?` 发现都可以匹配，比如`/bant` 。 还有Spring MVC 的一些 过滤器注册、格式化器注册都用到了 `Ant` 风格。

  ### 4.2 Spring Security 中的 Ant 风格

  在 **Spring Security** 中 `WebSecurityConfigurerAdapter` 中的你可以通过如下配置进行路由权限访问控制：

  ```java
  public class SecurityConfig extends WebSecurityConfigurerAdapter {
  
      @Autowired
      public void configureGlobal(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
          authenticationManagerBuilder.inMemoryAuthentication().withUser("admin").password("admin").roles("USER");
      }
  
      @Override
      protected void configure(HttpSecurity http) throws Exception {
          http.authorizeRequests()
                  //放行静态资源 首页
                  .antMatchers("/index.html","/static/**").permitAll()
                  .anyRequest().authenticated();
      }
  }
  ```

  上面 **Spring Security** 的配置中在 `antMatchers` 方法中通过 `Ant` 通配符来控制了资源的访问权限

  ## 5. 总结

  `Ant` 风格整体东西不多,也很好理解。 很多关于`uri` 的配置、路由匹配、处理都用到了 `Ant` 风格 。对于 Web 开发人员来说是必须掌握的技能之一。	

