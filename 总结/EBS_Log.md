# 1.  SLS概念

最大的概念是 model, model 下面有 project, project 下面有 logstore

1. model: 

- 常见的有 tdc, geo, river, kuafu, bm, bs, snapshot, gc, ocean, tiangang

1. Region & Project:

- SLS针对不同地区有不同的project
- 收集日志的project:都以ali-ecs-disk开头, 后面接地域； 见sls_ali_ecs_disk_projects
- 汇聚日志的projcet:  ebs_log_system 开头为最终汇聚了的日志, 见 sls_project 和 log_service_projcect_by+region
  - 主要有 log_system1 2 3, 分别位于 上海 乌兰察布 杭州

2. logstore

- 每个 project 下都有 logstore, 存储了不同的日志
- 收集的 logstore: 以 ebsls 来头
- logstore 汇聚到哪个 log_system 中, 见 log_system_union_log

3. logtail

- 配置的 logtail 为 logstotre 中日志的采集规则
- 流程: 在白屏 create/update 配置, 通过 sls_op dump 出配置文件; 然后首先推新加坡, 观察日志收集情况, 在全网推送

# 2. logstore

日志库采集多种日志