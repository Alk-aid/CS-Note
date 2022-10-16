# 1. Concept

1. RPC 组成部分:

- 客户端: 服务消费端, 调用远程方法的一端
- 服务端: 服务提供端, 提供远程方法的一端
- 客户端 stub: 代理类, 把调用方法的信息传递到服务端
- 服务端 stub: 接收到客户端执行方法的请求后，去指定对应的方法然后返回结果给客户端的类 

2. RPC 流程:

- 客户端: 以本地调用的方式调用远程服务
- 客户端 Stub: 将方法、参数等组装成能够进行网络传输的消息体（`序列化`):RpcRequest
- 客户端 Stub: 找到远程服务的地址，并将消息发送到服务提供端
- 服务端Stub: 进行反序列化得到RpcRequest对象, 然后根据得到的参数调用本地方法
- 服务端Stub: 得到 return value 并组装成能够进行网络传输的消息体: RpcResponse, 然后发送给客户端
- 客户端: 反序列化后就得到了最终结果

![img](https://www.yuque.com/api/filetransfer/images?url=http%3A%2F%2Fmy-blog-to-use.oss-cn-beijing.aliyuncs.com%2F18-12-6%2F37345851.jpg&sign=3e74c7dc34e427a81a470b333ab1a4fc2e33a3f410400c5ff07b69ab9d83bf96)

3. Http 和 rpc的区别

- 不是一个层次的东西: http是协议格式 rpc是一种通讯方式, 其中grpc可以使用http协议实现
- 功能不一样: rpc相对于http多了服务发现, 负载均衡, 熔断降级等一系列面向服务的高级特性

# 2. own-rpc

<img src="https://aikaid-img.oss-cn-shanghai.aliyuncs.com/img-2022/image-20221008222415071.png" alt="image-20221008222415071" style="zoom:50%;" />

RPC框架的组成:

- 注册中心: 负责服务地址的注册与查找
- 负载均衡: 
- 动态代理: 屏蔽远程方法调用的底层细节
- 传输协议: 客户端 和 服务器交流的基础
- 序列化 / 反序列化:
- 网络传输: Netty

## 2.1 序列化

为什么不使用 JDK 序列化: (TODO)

- 性能差: 序列化生成的字节数组体积大, 传输成本大
- 不支持跨语言调用

Kryo:

- 其变长存储特性并使用了字节码生成机制，拥有较高的运行速度和较小的字节码体积
- Dubbo官方推荐, 非常成熟
- 只支持 Java 语言

Protobuf:

- 跨平台, 没有序列化漏洞风险
- 使用繁琐, 要自己定义 IDL 文件和生成对应的序列化代码
