

>论文
>
>《Drain: An Online Log Parsing Approach with Fixed Depth Tree》

# 1. 算法简介

Drain: An Online Log Parsing Approach with Fixed Depth Tree

1. 目标：将raw log message 转化为 structured log message

2. 本质：以 streaming manner 将具有相同log event的原始日志消息 集群到 一个日志组中，进行一个聚类；

3. 专业术语：

- `log`: usually the only data resource available that records service runtime information

- `constant`: is tokens that describe a system operation template(e.g log event)

- `variable`: is the remaining token that carry dynamic runtime system information
- `Root Node and internal nodes:` 根节点和内部节点编码专门设计的规则来指导搜索过程，它们不存储日志组
- `Leaf Node`: store a list of log groups
- `log groups`: Each log group has two parts, `log event` and `log IDs`
- `log event`:  the template that best describes the log messages in this group, which consists of the constant part of a log message
- `log IDs`:    records the IDs of log messages in this group
- `depth`: 规定所有叶子结点的深度为depth, 从而限制了搜索过程中的节点数量，从而大大提高搜索效率
- `maxChild`: 为了避免树分支爆炸, 限制一个节点的最大子节点数量

# 2. METHODOLOGY

1. search a log group (i.e., leaf node of the tree) by following the specially-designed rules encoded in the internal nodes of the tree
2. If a suitable log group is found, the log message will be matched with the log event stored in that log group
3. Otherwise, a new log group will be created based on the log message

## 4.1 Overall Tree Structure

使用具有固定长度的解析树来指导log group search，从而有效的限制了a raw log message需要比较的log group数量

<img src="/Users/alkaid/Library/Application Support/typora-user-images/image-20220729121006367.png" alt="image-20220729121006367" style="zoom: 33%;" />

1. Root Node and internal nodes：

- encode specially-designed rules to guide the search process
- They do not contain any log groups

2. Leaf Node: 

- which stores a list of log groups

3. log groups: Each log group has two parts

- `log event`: the template that best describes the log messages in this group, which consists of the constant part of a log message
- `log IDs`:   records the IDs of log messages in this group

4. depth & maxChild:

- `depth`: 规定所有叶子结点的深度为depth, 从而限制了搜索过程中的节点数量，从而大大提高搜索效率
- `maxChild`: 为了避免树分支爆炸, 限制一个节点的最大子节点数量

## 2.2 Step1: Preprocess by Domain Knowledge

> 预处理是否就是找到variable部门

预处理可以提高解析精度

- 基于domain knowledge 提供简单的Regex, 比如IP address and block ID
- 然后从raw log message中删除这些Regex所匹配的token

## 2.3 Step2: Search by Log Message Length

如何根据编码的规则遍历parse tree并最终找到一个叶子结点

- 用经过预处理的log message 从解析树的根节点开始
- 解析树中的The 1-st layer node代表日志消息长度不同的日志组
- log message length: the number of tokens in a log message
- 这是基于一种假说：具有相同日志事件的日志消息可能具有相同的日志消息长度

## 2.4 Step3: Search by Preceding Tokens

基于的假说：日志消息开始的token更有可能是一个常量

- Drain在此步骤遍历的内部节点数为 deep - 2
- 因此也会将log message的前depth - 2个token 进行encode

如果日志消息以参数开头，如数字

- 在这一步对于含有参数的token进行特殊对待，it will match a special internal node "*"
- 如果一个节点有maxChild 子节点了，那么之后任何不匹配的token都将match node "*"

##  2.5 Step4: Search by Token Similarity

背景：

- Befor this step, Drain has traversed to a leaf node, which contains a list of log groups

- 这些日志组中的消息遵守沿路径在内部节点中的编码规则

流程：

- Drain从日志组列表中选择最合适的日志组,判断的依据是相似性simSeq
- 计算log message 和每个日志组中的 log event 的 相似性

$$
simSeq\,=\,\frac{ {\textstyle \sum_{i=1}^{n}equ(seq_1(i),seq_2(i))} }{n}
$$

- $seq_1$ 和 $seq_2$ 分表代表log message 和 log event； seq(i)是序列的第i个token； n是序列的log message length

$$
equ(t_1,t_2)=\begin{cases}
    & 1\,\,\,\text{ if } t_1=t_2 \\
    & 0\,\,\,\text otherwise
  \end{cases}
$$

- 找到largest SimSeq以后，将其和predefined similarity threshold st进行对比，如果大于st则将对应log group作为最适合的日志组
- 否则返回一个flag(e.g None in Python) to indicate no suitable log group

## 2.6 Step5: Update the Parse Tree

1. 如果找到了一个suitable log group

- 那么会将log message的log ID加入到 log group中的 log IDs中；
- 此外将更新对应日志组中的log event；具体来说，就是扫描log event 和 log message 如果相同则不修改。如果不相同，则将log event中的对应位置修改为 *

2. 如果没找到对应的suitable log group

- 将基于当前的log message创建一个一个log group, 其中log IDs只包含当前log message id, log event就是 log message
- 然后使用new log group 去更新parse tree
  - 将从根节点遍历到应该包含新日志组的叶节点
  - 并沿路径相应地添加缺失的内部节点和叶子结点
