

# 2. InnoDB存储引擎

## 2.1 特点

1. 行锁设计
2. 支持MVVCC
3. 支持外键
4. 提供一致性非锁定读
5. 最有效的利用以及使用内存和CPU

## 2.2 后台线程

1. Master Thread
2. IO Thread
3. Purge Thread
4. Page Cleaner Thread

### I Master Thread

负责将缓冲池中的数据异步刷新到磁盘，保证数据的一致性，包括脏页的刷新，合并插入缓冲(INSERT BUFFER),UNDO页的回收

### II IO Thread

**InnoDB存储引擎大量使用了 AIO（Async IO） 来处理写IO请求**，这样可以极大提高数据库的性能

IO Thread的工作是负责这些IO请求的回调(call back)处理。

1.0版本以前有4个IO Thread

- write  1.0.x增加到4个
- read   1.0.x增加到4个
- insert buffer
- log thread

命令

```mysql
-- 查看IO thread
show variables like 'innodb_%io_threads'\G;
show engine innodb status
```

### III Purge Thread

Purge: 清除

作用：回收已经使用并分配的undo页



1.1版本以前，purge操作是在Master Thread中完成的

1.1版本以后，purge操作可以独立到单独的线程中进行，减轻Master Thread的工作，从而提高CPU的使用率以及提升存储引擎的性能

```mysql
-- 启动purge线程
在Mysql的配置文件中添加如下命令
innodb_purge_threads=1 // 1.2版本开始，支持多个线程，可以进一步加快undo页的回收
```

### IV Page Cleaner Thread

1.2.x版本引入的

作用是将 

- 将之前版本中脏页的刷新操作 都放入到单独的线程中来完成。
- 减轻Master Thread的工作以及对于用户查询线程的阻塞，进一步提高引擎的性能

## 2.3 内存

### 2.3.1 缓冲池

#### I 出现的原因

> - InnoDB引擎是基于磁盘存储的，并将其中的记录按照页的方式进行管理
> - 但是CPU速度与磁盘之间有很大差距
> - 所以需要使用缓冲池技术来提高数据库的整体性能
> - 缓冲池简单来说是一块内存区域，通过内存的速度来弥补磁盘速度较慢对数据库性能的影响

#### II 使用过程

> 在数据库读取页的操作中
>
> 1. 首先将从磁盘读到的页存放在缓冲池中，称为 将页FIX在缓冲池中
> 2. 下一次再读相同的页，首先判断该页是否在缓冲池中
>    1. 若在，则命中，直接读取该页
>    2. 否则读取磁盘上的页
>
> 数据库页的修改
>
> 1. 首先修改缓冲池中的页
> 2. 然后再以一定频率刷新到磁盘上
> 3. 但是这个刷新不是每次页改变就触发，而是通过Checkpoint的机制刷新回磁盘

#### III 缓冲池的构造

1. 主要存放的数据页类型为
   - 索引页
   - 数据页
   - undo页
   - 插入缓存(insert buffer)
   - 自适应哈希索引(adaptive hash index)
   - 锁信息(lock info)
   - 数据字典信息(data dictionary)

![img](https://gitee.com/aik-aid/picture/raw/master/aHR0cHM6Ly91cGxvYWQtaW1hZ2VzLmppYW5zaHUuaW8vdXBsb2FkX2ltYWdlcy82NTg0NTMtOGVkNzk3ZGM4YzAwMDRjZi5wbmc_aW1hZ2VNb2dyMi9hdXRvLW9yaWVudC9zdHJpcCU3Q2ltYWdlVmlldzIvMi93LzgzMS9mb3JtYXQvd2VicA)

2. 缓冲池实例的个数

**从1.0.x版本开始，允许有多个缓冲池实例。**

那么每个页如何选定存放到哪个缓冲池中呢？

> 每个页根据哈希值平均分配到不同缓冲池实例中。
>
> 这样做的好处是：减少数据库内部的资源竞争，增加数据库的并发处理能力。

### 2.3.2 管理缓冲池

#### I LRU

通常来说，数据库的缓冲池是通过LRU(Latest Recent Used 最近最少使用算法)。

1. LRU的管理对象

> 1. 主要是数据页 和 索引页 中
> 2. **用来管理已经读取的页**
>
> 2. 自适应哈希索引 lock信息 insert buffer 等页 不需要LRU算法进行维护

2. 朴素的LRU过程

> 该算法赋予每个[页面](https://baike.baidu.com/item/页面/5544813)一个访问字段，用来记录一个页面自上次被访问以来所经历的时间 t，当须淘汰一个页面时，选择现有页面中其 t 值最大的，即最近最少使用的页面予以淘汰。
>
> 1. 最频繁使用的页在LRU列表的前端，而最少使用的页在LRU列表的尾端
> 2. 当缓冲池不能存放新读取到的数据页时，将首先释放LRU列表中尾端的页

2. 优化的LRU

> 1. 加入了midpoint的位置，新读取到的页不是放入首部，而是放入midpoint位置，如innodb_old_blocks_pct为37，则新读取到的页插入到LRU咧白哦尾端的37%的位置。也就是长度的5/8处
>    - 其中midpoint之后的称为old列表，之前的称为new列表。可以认为new列表都是最为活跃热点数据
>
> 2. 有一个参数innodb_old_blocks_time，用于表示读取到mid位置后需要等待多久才会被加入到LRU列表的热端

3. 为什么要优化呢

> 1. 如果不优化的话，全表扫描等操作可能会访问到很多页，而这些页大多数仅在这次查询中需要，并不是活跃的热点数据。如果放入首部，可能会将所需的热点数据页从LRU列表中移除，导致污染，下一次要读取该页，需要从磁盘中读

#### II Free List

当需要从缓冲池中分页时，首先从Free列表中查找是否有可用的空闲页

1. 若有，则将该页从Free列表中删除，放入到LRU列表中
2. 否则，根据LRU算法，淘汰LRU列表末尾的页，将该内存空间分配给新的页

Free buffers:Free列表的页数

Database pages：LRU页的数量

#### III Flush List

Flush List ：脏页列表

在LRU列表中的页被修改后，称该页为脏页(dirty page),即缓冲池中的页和磁盘上的页的数据产生了不一致

脏页即存在于 LRU列表 又存在于Flush List中

- LRU中的脏页主要用于管理缓冲池中页的可用性
- Flush列表用来管理将页刷新回磁盘  通过CHECKPOINT机制将脏页刷新回磁盘
- 二者互不影响

### 2.3.3 重做日志缓存

内存中还有 重做日志缓存(redo log buffer) 

InnoDB引擎首先将重做日志信息先放入到这个缓冲区，然后按照一定频率将其刷新到重做日志文件中。

一般大小不需要设置很大，因为每秒都会刷新到日志文件中去。

以下三种情况 重做日志缓存 会将内容刷新到磁盘中的重做日志中去

- Master Thread每一秒将重做日志缓冲刷新到重做日志文件
- 每个事务提交时会将重做日志缓冲刷新到重做日志文件
- 当重做日志缓冲池中的剩余空间小于1/2时

### 2.3.4 额外的内存池

TODO

## 2.4 CheckPoint

### 2.4.1 WAL策略

Write Ahead Log

出现原因

> 1. 因为有缓冲区的存在，，会出现脏页，所以我们需要将新版本的页从缓冲区刷新到磁盘中去
> 2. 但是每个页发生变化，就将新页刷新到磁盘去，那么这个开销是非常大的，会导致数据库的性能很差
> 3. 同时，如果在从缓冲区刷新到磁盘中发生了宕机，那么数据就不能恢复了

所以我们采用了WAL策略。

- 当事务提交时，先写重做日志，再修改页。
- 当发生宕机时，可以通过重做日志来完成数据的恢复
- 满足了事务的持久性要求

通过redo log恢复数据库系统中的数据到宕机发生的时刻需要两个前提条件

- 缓冲池可以存放所有数据
- 重做日志可以无限大

即使两个都满足了如果redo log特别大，恢复的代价也很多。

所以我们需要一种机制来决定将缓冲池中的脏页刷会到磁盘中去，这个就是`checkpoint`

### 2.4.2 CheckPoint

Checkpoint的作用就是将缓冲池中的脏页刷回到磁盘中去

不同之处在于

> - 每次刷新多少页到磁盘
> - 每次从哪里取脏页
> - 什么时间触发CheckPoint

分类

> - Sharp CheckPoint:发生再数据库关闭时，将所有脏页刷新回磁盘
> - Fuzzy CheckPoint：只刷新一部分脏页
>   - Master Thread CheckPoint：每s或者每10s从缓冲池中的`脏页列表`刷新一定比例的页，`异步的`
>   - FLUSH_LRU_LIST CheckPoint
>   - Async/Sync Flush CheckPoint
>   - Dirty Page too much CheckPoint：脏页的数量太多，强制进行CheckPoint

目的是为了解决以下几个问题

> - 缩短数据库的恢复时间
> - 缓冲池不够用时，将脏页刷新到磁盘中
> - 重做日志不可用时，刷新脏页

#### I 缩短恢复时间

当数据库发生宕机时，不需要重做所有日志，因为CheckPoint之前的页都已经刷新回磁盘，只需要对CheckPoint之后的重做日志进行恢复

#### II 缓冲池不够用时

根据LRU算法回溢出最近最少使用的页，如果此页为脏页，那么需要强制执行CheckPoint.

也就是 FLUSH_LRU_LIST CheckPoint

#### III 重做日志不可用

为什么不可用

> 因为重做日志是循环使用的，并不是无限增大的。
>
> ![img](https://static001.geekbang.org/resource/image/b0/9c/b075250cad8d9f6c791a52b6a600f69c.jpg)
>
> write pos 右边 和checkpoint左边的数据都是可重用的，因为这些重做日志已经不需要了，已经刷新回磁盘了。

如果write pos追上checkpoint，表示“粉板”满了，这时候不能再执行新的更新，得停下来先擦掉一些记录，把checkpoint推进一下。也就是进行 `Async/Sync Flush CheckPoint`

## 2.5 Master Thread工作方式

To Do

### 2.5.1 1.0.x版本之前的

### 2.5.2

###  

### 2.5.3

## 2.6 InnoDB关键特性

关键特性包括

- 插入缓冲(Insert Buffer)
- 两次写(Double Write)
- 自适应哈希索引(Adaptive Hash Index)
- 异步IO(Async IO)
- 刷新邻接页(Flush Neighbor Page)

### 2.6.1 插入缓冲

#### I insert buffer

使用条件

- 索引是辅助索引
- 索引不是唯一的

对于非聚集索引的插入 或者更新操作

- 不是每一次直接插入到索引页中
- 而是先判断插入的非聚集索引是否在缓冲池中
  - 若在，直接插入
  - 不在，则先放入一个Insert Buffer对象中，然后再根据一定的频率和情况进行Insert Buffer和辅助索引叶子结点的merge操作

辅助索引不能是唯一的

> 数据库并不去查找索引页来判断插入的记录的唯一性，如果去查找，肯定又会有离散读取的情况发生

#### II change buffer

对Insert Buffer进行升级，有如下buffer

- insert buffer
- delete buffer
- purge buffer

对一条记录进行Update分为两个过程

- 将记录标记位已删除   delete buffer对应这个
- 真正将记录删除       purge buffer对应这个

