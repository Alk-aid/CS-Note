1. RPC流程:

- 客户端以本地调用的方式调用远程服务
- 客户端Stub进行序列化, 以及找到远程服务的地址, 在通过网络传输发送给服务端
- 服务端Stub进行反序列化, 然后根据得到的参数调用本地方法;
- 服务端Stub将方法结果进行序列化, 并发送给客户端
- 客户端反序列化后就得到了最终结果

![img](https://www.yuque.com/api/filetransfer/images?url=http%3A%2F%2Fmy-blog-to-use.oss-cn-beijing.aliyuncs.com%2F18-12-6%2F37345851.jpg&sign=3e74c7dc34e427a81a470b333ab1a4fc2e33a3f410400c5ff07b69ab9d83bf96)

2. Http 和 rpc的区别

- 不是一个层次的东西: http是协议格式 rpc是一种通讯方式, 其中grpc可以使用http协议实现
- 功能不一样: rpc相对于http多了服务发现, 负载均衡, 熔断降级等一系列面向服务的高级特性

