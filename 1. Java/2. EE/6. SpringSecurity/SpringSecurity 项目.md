https://blog.csdn.net/I_am_Hutengfei/article/details/100561564?ops_request_misc=%257B%2522request%255Fid%2522%253A%2522161984879416780262597955%2522%252C%2522scm%2522%253A%252220140713.130102334..%2522%257D&request_id=161984879416780262597955&biz_id=0&utm_medium=distribute.pc_search_result.none-task-blog-2~all~top_positive~default-1-100561564.nonecase&utm_term=springsecurity



# 1. 准备工作

## 1.1 统一错误码枚举

```java
/**
 * @Author: Hutengfei
 * @Description: 返回码定义
 * 规定:
 * #1表示成功
 * #1001～1999 区间表示参数错误
 * #2001～2999 区间表示用户错误
 * #3001～3999 区间表示接口异常
 * @Date Create in 2019/7/22 19:28
 */
public enum ResultCode {
    /* 成功 */
    SUCCESS(200, "成功"),

    /* 默认失败 */
    COMMON_FAIL(999, "失败"),

    /* 参数错误：1000～1999 */
    PARAM_NOT_VALID(1001, "参数无效"),
    PARAM_IS_BLANK(1002, "参数为空"),
    PARAM_TYPE_ERROR(1003, "参数类型错误"),
    PARAM_NOT_COMPLETE(1004, "参数缺失"),

    /* 用户错误 */
    USER_NOT_LOGIN(2001, "用户未登录"),
    USER_ACCOUNT_EXPIRED(2002, "账号已过期"),
    USER_CREDENTIALS_ERROR(2003, "密码错误"),
    USER_CREDENTIALS_EXPIRED(2004, "密码过期"),
    USER_ACCOUNT_DISABLE(2005, "账号不可用"),
    USER_ACCOUNT_LOCKED(2006, "账号被锁定"),
    USER_ACCOUNT_NOT_EXIST(2007, "账号不存在"),
    USER_ACCOUNT_ALREADY_EXIST(2008, "账号已存在"),
    USER_ACCOUNT_USE_BY_OTHERS(2009, "账号下线"),

    /* 业务错误 */
    NO_PERMISSION(3001, "没有权限");
    
    private Integer code;
    private String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 根据code获取message
     *
     * @param code
     * @return
     */
    public static String getMessageByCode(Integer code) {
        for (ResultCode ele : values()) {
            if (ele.getCode().equals(code)) {
                return ele.getMessage();
            }
        }
        return null;
    }
}

```



## 1.2 统一json返回体

```java

public class JsonResult<T> implements Serializable {
    private Boolean success;
    private Integer errorCode;
    private String errorMsg;
    private T data;

    public JsonResult() {
    }

    public JsonResult(boolean success) {
        this.success = success;
        this.errorCode = success ? ResultCode.SUCCESS.getCode() : ResultCode.COMMON_FAIL.getCode();
        this.errorMsg = success ? ResultCode.SUCCESS.getMessage() : ResultCode.COMMON_FAIL.getMessage();
    }

    public JsonResult(boolean success, ResultCode resultEnum) {
        this.success = success;
        this.errorCode = success ? ResultCode.SUCCESS.getCode() : (resultEnum == null ? ResultCode.COMMON_FAIL.getCode() : resultEnum.getCode());
        this.errorMsg = success ? ResultCode.SUCCESS.getMessage() : (resultEnum == null ? ResultCode.COMMON_FAIL.getMessage() : resultEnum.getMessage());
    }

    public JsonResult(boolean success, T data) {
        this.success = success;
        this.errorCode = success ? ResultCode.SUCCESS.getCode() : ResultCode.COMMON_FAIL.getCode();
        this.errorMsg = success ? ResultCode.SUCCESS.getMessage() : ResultCode.COMMON_FAIL.getMessage();
        this.data = data;
    }

    public JsonResult(boolean success, ResultCode resultEnum, T data) {
        this.success = success;
        this.errorCode = success ? ResultCode.SUCCESS.getCode() : (resultEnum == null ? ResultCode.COMMON_FAIL.getCode() : resultEnum.getCode());
        this.errorMsg = success ? ResultCode.SUCCESS.getMessage() : (resultEnum == null ? ResultCode.COMMON_FAIL.getMessage() : resultEnum.getMessage());
        this.data = data;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

```

## 1.3 返回体构造工具

```java

public class ResultTool {
    public static JsonResult success() {
        return new JsonResult(true);
    }

    public static <T> JsonResult<T> success(T data) {
        return new JsonResult(true, data);
    }

    public static JsonResult fail() {
        return new JsonResult(false);
    }

    public static JsonResult fail(ResultCode resultEnum) {
        return new JsonResult(false, resultEnum);
    }
}

```

## 1.4 pom文件

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.mybatis.spring.boot</groupId>
        <artifactId>mybatis-spring-boot-starter</artifactId>
        <version>2.1.4</version>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## 1.5 主配置文件

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/learn_spring_security?useUnicode=true&characterEncoding=UTF-8&serverTimezone=GMT%2B8&useSSL=false&allowMultiQueries=true
    password: Aa123Aa123
    username: root
mybatis:
  mapper-locations: classpath:mapper/*.xml

```

# 2. 数据库表设计

![image-20210501141016970](https://gitee.com/aik-aid/picture/raw/master/image-20210501141016970.png)

```mysql
create table sys_user
(
    id                      int auto_increment
        primary key,
    account                 varchar(32)          not null comment '账号',
    user_name               varchar(32)          not null comment '用户名',
    password                varchar(64)          null comment '用户密码',
    last_login_time         datetime             null comment '上一次登录时间',
    enabled                 tinyint(1) default 1 null comment '账号是否可用。默认为1（可用）',
    not_expired             tinyint(1) default 1 null comment '是否过期。默认为1（没有过期）',
    account_not_locked      tinyint(1) default 1 null comment '账号是否锁定。默认为1（没有锁定）',
    credentials_not_expired tinyint(1) default 1 null comment '证书（密码）是否过期。默认为1（没有过期）',
    create_time             datetime             null comment '创建时间',
    update_time             datetime             null comment '修改时间',
    create_user             int                  null comment '创建人',
    update_user             int                  null comment '修改人'
)
    comment '用户表';

```

```mysql
create table sys_role
(
    id               int auto_increment comment '主键id'
        primary key,
    role_code 		 varchar(32) null
    role_name        varchar(32) null comment '角色名',
    role_description varchar(64) null comment '角色说明'
)
    comment '用户角色表';

```

```mysql
create table sys_permission
(
    id              int auto_increment comment '主键id'
        primary key,
    permission_code varchar(32) null comment '权限code',
    permission_name varchar(32) null comment '权限名'
)
    comment '权限表';

```

```mysql
create table sys_user_role_relation
(
    id      int auto_increment comment '主键id'
        primary key,
    user_id int null comment '用户id',
    role_id int null comment '角色id'
)
    comment '用户角色关联关系表';

```

```mysql
create table sys_role_permission_relation
(
    id            int auto_increment comment '主键id'
        primary key,
    role_id       int null comment '角色id',
    permission_id int null comment '权限id'
)
    comment '角色-权限关联关系表';

```

```mysql
create table sys_request_path
(
    id          int auto_increment comment '主键id'
        primary key,
    url         varchar(64)  not null comment '请求路径',
    description varchar(128) null comment '路径描述'
)
    comment '请求路径';

```

```mysql
create table sys_request_path_permission_relation
(
    id            int null comment '主键id',
    url_id        int null comment '请求路径id',
    permission_id int null comment '权限id'
)
    comment '路径权限关联表';

```

表的数据初始化

```mysql
-- 用户
INSERT INTO sys_user (id, account, user_name, password, last_login_time, enabled, account_non_expired, account_non_locked, credentials_non_expired, create_time, update_time, create_user, update_user)VALUES (1, 'user1', '用户1', '$2a$10$47lsFAUlWixWG17Ca3M/r.EPJVIb7Tv26ZaxhzqN65nXVcAhHQM4i', '2019-09-04 20:25:36', 1, 1, 1, 1, '2019-08-29 06:28:36', '2019-09-04 20:25:36', 1, 1);
INSERT INTO sys_user (id, account, user_name, password, last_login_time, enabled, account_non_expired, account_non_locked, credentials_non_expired, create_time, update_time, create_user, update_user)VALUES (2, 'user2', '用户2', '$2a$10$uSLAeON6HWrPbPCtyqPRj.hvZfeM.tiVDZm24/gRqm4opVze1cVvC', '2019-09-05 00:07:12', 1, 1, 1, 1, '2019-08-29 06:29:24', '2019-09-05 00:07:12', 1, 2);
-- 角色
INSERT INTO sys_role (id, role_code, role_name, role_description) VALUES (1, 'admin', '管理员', '管理员，拥有所有权限');
INSERT INTO sys_role (id, role_code, role_name, role_description) VALUES (2, 'user', '普通用户', '普通用户，拥有部分权限');
-- 权限
INSERT INTO sys_permission (id, permission_code, permission_name) VALUES (1, 'create_user', '创建用户');
INSERT INTO sys_permission (id, permission_code, permission_name) VALUES (2, 'query_user', '查看用户');
INSERT INTO sys_permission (id, permission_code, permission_name) VALUES (3, 'delete_user', '删除用户');
INSERT INTO sys_permission (id, permission_code, permission_name) VALUES (4, 'modify_user', '修改用户');
-- 请求路径
INSERT INTO sys_request_path (id, url, description) VALUES (1, '/getUser', '查询用户');
-- 用户角色关联关系
INSERT INTO sys_user_role_relation (id, user_id, role_id) VALUES (1, 1, 1);
INSERT INTO sys_user_role_relation (id, user_id, role_id) VALUES (2, 2, 2);
-- 角色权限关联关系
INSERT INTO sys_role_permission_relation (id, role_id, permission_id) VALUES (1, 1, 1);
INSERT INTO sys_role_permission_relation (id, role_id, permission_id) VALUES (2, 1, 2);
INSERT INTO sys_role_permission_relation (id, role_id, permission_id) VALUES (3, 1, 3);
INSERT INTO sys_role_permission_relation (id, role_id, permission_id) VALUES (4, 1, 4);
INSERT INTO sys_role_permission_relation (id, role_id, permission_id) VALUES (5, 2, 1);
INSERT INTO sys_role_permission_relation (id, role_id, permission_id) VALUES (6, 2, 2);
-- 请求路径权限关联关系
INSERT INTO sys_request_path_permission_relation (id, url_id, permission_id) VALUES (null, 1, 2);

```

# 3. SpringSecurity核心配置：WebSecurityConfig

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
}
```

# 4. 用户登录认证逻辑：UserDetailsService

## 4.1 创建自定义UserDetailsService

​	这是实现自定义用户认证的核心逻辑，loadUserByUsername(String username)的参数就是登录时提交的用户名，返回类型是一个叫UserDetails 的接口，需要在这里构造出他的一个实现类User，这是Spring security提供的用户信息实体。

```java
public class UserDetailsServiceImpl implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //需要构造出 org.springframework.security.core.userdetails.User 对象并返回
        return null;
    }
}
```

## 4.2 准备service和dao层方法

