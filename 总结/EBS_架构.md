# 1. 架构

## 1.1 EBS 架构

逻辑上理解:

- 云盘本质上是一个数组(在盘古开辟一个文件, 然后和磁盘进行一比一映射), 然后在逻辑上氛围一个个 Segment Group(128G)
- 然后 Segment Group 氛围一个个 Segment(32G), 这个划分通过条带化提高并行能力
  - 条带化的好处: 可以增加并发度 和 防止热点数据进行聚集
- Segment 在逻辑上是由一个个 Block 组成的, 一个 Block 由 Data + Meta 组成
- 数据的真正写入是在盘古层面, 块存储所做的是一层层抽象 以及记录映射关系



写入操作:

- 客户端读写 IO 通过客户端发到 TDC, TDC 转化为 IO请求为后端可以识别的形式发送到 BS, BS 选择可用的 Segment
- Segment 选择追加写, 同时更新 Meta; Meta 记录的是本次数据对应的 LBA: DatafileID + range
- 由 pangu 的 client 写入到三个 cs 保证三副本, 每个 cs 里维护文件数据到实际 chunk 的映射

读取操作:

- 需要先从 BM 获取 segment 对应的 BS 地址, 然后向 Bs 发起读请求
- BS 根据文件的 offset 和 length 查询indexMap, 确定 chunk 位置

## 1.2 盘古

开源知识:

- HDFS
- LSM tree
- Level DB
- HBase

LSDB: Log-Structured Block Device

- 使用 LSM (Log-Structured Merge Tree)方式组织数据

BlockServer: 对外提供 Segment 的 IO 接口

- 用户的 IO 发送给相应的 Block Server, Block Server 根据 segment 的元数据决定将 IO 请求转化为对应的盘古文件读写操作
- Blocke Server 是无状态的

BlockMaster: 是管理云盘的元数据, 负责调度 Segment 以及分配 GC 等后台任务

## 1.3 TDC

作为块存储的服务入口 和 IO 流量入口, 负责云盘在计算节点的整体工作

TDC 服务包括: 虚拟化接入层, 数据处理层, 存储转发层, 网络转发

## 1.4 管控

ocean: EBS region 级别管控服务, 负责选取哪个 region进行创盘

river master: EBS azone 级别管控服务

river cluster & river server: 1.0 集群管理服务 和 后端服务, NC 层面提供的块存储后端服务

block master  & block server: 2.0 集群管理服务 和 后端服务, NC 层面提供的块存储后端服务

## 1.5 产品

块存储: ESSD, SSD, 高效云盘, 本地盘



# 2. IOHang

IOhang 告警的定义: 块设备一分钟无法读写

IOhang 在 TDC 的原因:

- 线程 IOhang
- IO 越界
- 存储后端 hang

IOhang 在 FPGA 的原因:

- FPGA 故障, 以及停止硬件 Queue
- 检查到非法 IO 描述符

# 各类存储

OSS: 海量图片, 网页图片 类似的海量小文件

块存储: 虚拟机云盘, 如 Ceph 高性能 高可用 高可靠

文件存储: 