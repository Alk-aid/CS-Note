# 0. 技术栈

1. 分布式理论:

- cap、raft、zab、paxos

- 2pc，3pc，tcc、base

- 一致性 hash，负载均衡，数据分片

2. 中间件

- netty
- rocketmq、dubbo
- apollo
- zk，eureka
- springCloud



# 1. MIT6.824

Lab1 MapReduce

MapReduce是著名的分布式计算模型，用于处理海量数据问题，例如单词统计，虽然可以单机多线程遍历输入文件，但受到单机限制，花费时间很长，而利用此分布式编程模型可以极大减少时间，开发者只需要编写Map与Reduce函数。主要难点在于理解模型架构与映射规则。节点分为Master和Worker，Master负责记录Worker状态，调度Map和Reduce任务给Worker以及记录系统运行阶段，并且Master需要支持容错，在Worker超时或宕机的情况下仍完成任务。Worker则负责完成下发任务，并将完成结果报告给Master。Map输出中间文件以及Reduce获取中间文件输入都需要满足映射规则才可工作。



Lab2 Raft

Raft是著名的分布一致性算法，目的是解决在多个分布式机器上维持数据一致性的问题。主要难点在于实现领导人投票选举，且领导人宕机后能够重新选举；领导人发送日志，心跳和快照，使所有节点的日志都与领导人保持一致；状态持久化，保证节点宕机恢复后可正常工作；日志压缩和快照安装，清除冗余日志，节点重启后快速恢复。理解任期，候选人，多数投票，投票限制，随机化原理等。

# 2. 高性能缓存系统

高性能缓存: 自主设计实现一个缓存, 并逐步进行优化

- 利用ConcurrentHashMap, Future类 和 putIfAbsent等JUC工具逐步优化, 解决了并发安全 和 缓存重复计算问题
- 通过指定随机过期时间, 实现了缓存过期功能, 避免了缓存不一致和缓存雪崩问题; 利用CountDownLatch 和 线程池进行了测试

# 3. 简易版Web服务器

简易版Web服务器

- 通过连接器 Connnector 配合 处理器 Processor, 实现对你请求静态资源和动态资源servlet的处理
- 基于BIO 和 NIO 两种IO情景, 设计并代码实现不同场景需求下的Java网络编程
