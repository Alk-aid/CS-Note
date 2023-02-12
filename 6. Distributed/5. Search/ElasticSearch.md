

# 1. 简介

## 1.1 产品介绍

> ElasticSearch：智能搜索，分布式的搜索引擎

是ELK的一个组成,是一个产品，而且是非常完善的产品，ELK代表的是：E就是ElasticSearch，L就是Logstach，K就是kibana

E：EalsticSearch 搜索和分析的功能

L：Logstach 搜集数据的功能，类似于flume（使用方法几乎跟flume一模一样），是日志收集系统

K：Kibana 数据可视化（分析），可以用图表的方式来去展示，文不如表，表不如图，是数据可视化平台

## 1.2 全文检索

​	全文检索是指计算机索引程序通过扫描文章中的每一个词，对每一个词建立一个索引，指明该词在文章中出现的次数和位置，当用户查询时，检索程序就根据事先建立的索引进行查找，并将查找的结果反馈给用户的检索方式。这个过程类似于通过字典中的检索字表查字的过程。

​	全文检索的方法主要分为**按字检索**和**按词检索**两种。

**按字检索**：

​	是指对于文章中的每一个字都建立索引，检索时将词分解为字的组合。对于各种不同的语言而言，字有不同的含义，比如英文中字与词实际上是合一的，而中文中字与词有很大分别。

**按词检索**：

​	指对文章中的词，即语义单位建立索引，检索时按词检索，并且可以处理同义项等。

---

​	英文等西方文字由于按照空白切分词，因此实现上与按字处理类似，添加同义处理也很容易。中文等东方文字则需要切分字词，以达到按词索引的目的，关于这方面的问题，是当前全文检索技术尤其是中文全文检索技术中的难点，在此不做详述。

## 1.3 倒排索引

以前是根据ID查内容，倒排索引之后是根据内容查ID，然后再拿着ID去查询出来真正需要的东西。

## 1.4 Restful风格

| **http方法** | **资源操作** | **幂等** | **安全** |
| ------------ | ------------ | -------- | -------- |
| GET          | SELECT       | 是       | 是       |
| POST         | INSERT       | 否       | 否       |
| PUT          | UPDATE       | 是       | 否       |
| DELETE       | DELETE       | 是       | 否       |

## 1.5 类型的基本介绍

### 1.5.1 与数据库的对比

ElasticSearch是面向文档型**数据库**,一条数据在这里就是一个文档

![image-20210501180859208](https://gitee.com/aik-aid/picture/raw/master/image-20210501180859208.png)

其中Types的概念已经被删除，**目前7.0版本，一个索引就只能创建一个类型了(_doc)**

### 1.5.2 文档(Document)

- 我们知道Java是面向对象的，而Elasticsearch是面向文档的，也就是说**文档是所有可搜索数据的最小单元**。ES的文档就像MySql中的一条记录，只是ES的文档会被序列化成json格式，保存在Elasticsearch中；
- 这个json对象是由字段组成，字段就相当于Mysql的列，每个字段都有自己的类型（**字符串、数值、布尔、二进制、日期范围类型**）；
- 当我们创建文档时，如果不指定字段的类型，Elasticsearch会帮我们自动匹配类型；
- 每个文档都有一个ID，类似MySql的主键，咱们可以自己指定，也可以让Elasticsearch自动生成；
- 文档的json格式支持数组/嵌套，在一个索引（数据库）或类型（表）里面，你可以存储任意多的文档。

> 注意：虽然在实际存储上，文档存在于某个索引里，但是**文档**必须被赋予**一个索引下的类型**才可以。

### 1.5.3 类型(Type)

类型就相当于MySql里的表，我们知道MySql里一个库下可以有很多表，最原始的时候ES也是这样，一个索引下可以有很多类型，但是从**6.0版本**开始，type已经被逐渐废弃，但是这时候一个索引仍然可以设置多个类型，一直到**7.0版本开始，一个索引就只能创建一个类型了（_doc）**

### 1.5.4 索引(Index)

- 索引就相当于MySql里的数据库，它是具有某种相似特性的文档集合。反过来说不同特性的文档一般都放在不同的索引里；
- 索引的名称必须全部是小写；
- 在单个集群中，可以定义任意多个索引；
- 索引具有mapping和setting的概念，mapping用来定义文档字段的类型，setting用来定义不同数据的分布。

### 1.5.5 节点(node)

- 一个节点就是一个ES实例，其实本质上就是一个java进程；
- 节点的名称可以通过配置文件配置，或者在启动的时候使用-E node.name=ropledata指定，默认是随机分配的。建议咱们自己指定，因为节点名称对于管理目的很重要，咱们可以通过节点名称确定网络中的哪些服务器对应于ES集群中的哪些节点；
- ES的节点类型主要分为如下几种：
  - Master Eligible节点：每个节点启动后，默认就是Master Eligible节点，可以通过设置node.master: false 来禁止。Master Eligible可以参加选主流程，并成为Master节点（当第一个节点启动后，它会将自己选为Master节点）；注意：每个节点都保存了集群的状态，只有Master节点才能修改集群的状态信息。
  - Data节点：可以保存数据的节点。主要负责保存分片数据，利于数据扩展。
  - Coordinating 节点：负责接收客户端请求，将请求发送到合适的节点，最终把结果汇集到一起

注意：每个节点默认都起到了Coordinating node的职责。一般在开发环境中一个节点可以承担多个角色，但是在生产环境中，还是设置单一的角色比较好，因为有助于提高性能。

### 1.5.6 分片(shard)

​	了解分布式或者学过mysql分库分表的应该对分片的概念比较熟悉，ES里面的索引可能存储大量数据，这些数据可能会超出单个节点的硬件限制。

​	为了解决这个问题，ES提供了将索引细分为多个碎片的功能，这就是分片。这里咱们可以简单去理解，在创建索引时，只需要咱们定义所需的碎片数量就可以了，其实每个分片都可以看作是一个完全功能性和独立的索引，可以托管在集群中的任何节点上。

**疑问二：分片有什么好处和注意事项呢？**

> 1. 通过分片技术，咱们可以水平拆分数据量，同时它还支持跨碎片（可能在多个节点上）分布和并行操作，从而提高性能/吞吐量；
> 2. ES可以完全自动管理分片的分配和文档的聚合来完成搜索请求，并且对用户完全透明；
> 3. 主分片数在索引创建时指定，后续只能通过Reindex修改，但是较麻烦，一般不进行修改。

### 1.5.7 副本分片（replica shard）

熟悉分布式的朋友应该对副本对概念不陌生，为了实现高可用、遇到问题时实现分片的故障转移机制，ElasticSearch允许将索引分片的一个或多个复制成所谓的副本分片。

**疑问三：副本分片有什么作用和注意事项呢？**

> 当分片或者节点发生故障时提供高可用性。因此，需要注意的是，副本分片永远不会分配到复制它的原始或主分片所在的节点上；
>
> 可以提高扩展搜索量和吞吐量，因为ES允许在所有副本上并行执行搜索；
>
> 默认情况下，ES中的每个索引都分配5个主分片，并为每个主分片分配1个副本分片。主分片在创建索引时指定，不能修改，副本分片可以修改。

# 2. HTTP操作

## 2.1 索引操作

### 2.1.1 创建索引

创建索引就相当于创建数据库

在Postman中，向ES服务器发起**PUT**请求

```url
http://localhost:9200/shopping
```

成功返回的数据

```json
{
    "acknowledged": true,  			#响应结果
    "shards_acknowledged": true,	#分片结果
    "index": "shopping1"			#索引名称
}
```

### 2.1.2 查看索引

#### 查看单个索引

在Postman中，向ES服务器发起**GET**请求

```url
http://localhost:9200/shopping
```

成功返回的数据

```json
{
    "shopping1": {   #索引名
        "aliases": {},#别名
        "mappings": {},#映射
        "settings": {#设置
            "index": {#设置-索引
                "creation_date": "1619864461017",#设置-索引-创建时间
                "number_of_shards": "1",		#设置-索引-主分片数量
                "number_of_replicas": "1",		#设置-索引-副分片数量
                "uuid": "FOU0ahhxSw6Ld_rG84Vqog",#设置-索引-唯一标识
                "version": {
                    "created": "7080099"		#设置-索引-版本
                },
                "provided_name": "shopping1"	#设置-索引-名称
            }
        }
    }
}
```

#### 查看所有索引

在Postman中，向ES服务器发起**GET**请求

```url
http://localhost:9200/_cat/indices?v
```

成功返回信息

![](https://gitee.com/aik-aid/picture/raw/master/image-20210501182907990.png)

![image-20210501182940362](https://gitee.com/aik-aid/picture/raw/master/image-20210501182940362.png)

### 2.1.3 删除索引

在Postman中，向ES服务器发起**delete**请求

```url
http://localhost:9200/shopping
```

成功返回的数据

```json
{
    "acknowledged": true
}
```

## 2.2 文档操作

### 2.2.1 创建文档

添加的数据要求为JSON格式

#### id是随机生成的

在Postman中，向ES服务器发起**POST**请求 

```url
http://localhost:9200/shopping/_doc
```

```json
{
    "title":"华为手机",
    "category":"华为",
    "images":"sadasd",
    "price":5499
}
```

> 只能为POST请求，因为每次生成的id都不同，不具有幂等性，所以不能用**PUT**

返回的数据

```json
{
    "_index": "shopping",
    "_type": "_doc",
    "_id": "zlMoKHkBJ2igbGcs72zl",#这个是随机生成的
    "_version": 1,
    "result": "created",
    "_shards": {
        "total": 2,
        "successful": 1,
        "failed": 0
    },
    "_seq_no": 0,
    "_primary_term": 1
}
```

#### id是自定义的

在Postman中，向ES服务器发起**POST**或者**PUT**请求 

```url
http://localhost:9200/shopping/_doc/1001
```

```json
{
    "title":"华为手机",
    "category":"华为",
    "images":"sadasd",
    "price":5499
}
```

### 2.2.2 查询

#### 单个查询

在Postman中，向ES服务器发起**GET**请求

```url
http://localhost:9200/shopping/_doc/1001
```

```json
{
    "_index": "shopping",
    "_type": "_doc",
    "_id": "1001",
    "_version": 1,
    "_seq_no": 1,
    "_primary_term": 1,
    "found": true,
    "_source": {
        "title": "华为手机",
        "category": "华为",
        "images": "sadasd",
        "price": 5499
    }
}
```

#### 全查询

在Postman中，向ES服务器发起**GET**请求

```url
http://localhost:9200/shopping/_search
```

或者

```url
http://localhost:9200/shopping/_search
```

```json
{
    "query":{
        "match_all":{

        }
    }
}
```

返回的数据

```json
{
    "took": 79,
    "timed_out": false,
    "_shards": {
        "total": 1,
        "successful": 1,
        "skipped": 0,
        "failed": 0
    },
    "hits": {
        "total": {
            "value": 3,
            "relation": "eq"
        },
        "max_score": 1.0,
        "hits": [
            {
                "_index": "shopping",
                "_type": "_doc",
                "_id": "zlMoKHkBJ2igbGcs72zl",
                "_score": 1.0,
                "_source": {
                    "title": "华为手机",
                    "category": "华为",
                    "images": "sadasd",
                    "price": 5499
                }
            },
            {
                "_index": "shopping",
                "_type": "_doc",
                "_id": "1001",
                "_score": 1.0,
                "_source": {
                    "title": "华为手机",
                    "category": "华为",
                    "images": "sadasd",
                    "price": 5499
                }
            },
            {
                "_index": "shopping",
                "_type": "_doc",
                "_id": "1002",
                "_score": 1.0,
                "_source": {
                    "title": "华为手机",
                    "category": "华为",
                    "images": "sadasd",
                    "price": 5499
                }
            }
        ]
    }
}
```

#### 条件查询

**GET**请求

##### 方法一(不推荐)

```url
http://localhost:9200/shopping/_search?q=category:华为
```

##### 方法二

```url
http://localhost:9200/shopping/_search
```

请求体中

```json
{
    "query":{
        "match":{
            "category":"华为"
        }
    }
}
```

#### 多条件查询

```url
http://localhost:9200/shopping/_search
```

```json
{
    "query":{
        "bool":{
            "must":[
                {
                    "match":{
                        "category":"华为"
                    }
                },
                {
                    "match":{
                        "price":"5499"
                    }
                }
            ]
        }
    }
}
```

```java
{
    "query":{
        "bool":{
    
            "filter":{
                "range":{
                    "price":{
                        "gt":50000
                    }
                }
            }
        }
    }
}
```

#### 分页查询

GET

```url
http://localhost:9200/shopping/_search
```

```json
{
    "query": {
        "match_all": {}
    }, 
    //分页
    "from": 0,
    "size": 3,
   	//设置显示字段
    "_source":["price"],
    //排序
	"sort":{
        "price":{
            "order":"desc"
        }
    }
}
```

#### 完全匹配

上述的条件查询是会将条件进行分词的如下述：会将小华分词为 小 和 华 从而匹配到含有小米和华为

```json
{
    "query":{
        "match":{
            "category":"小华"
        }
    }
}
```

#### 高亮显示

```json
{
    "query":{
        "bool":{
            "must":[
                {
                    "match":{
                        "category":"华为"
                    }
                }
        
            ]
        }
    },
    "highlight":{
        "fields":{
            "category":{}
        }
    }
}
```



#### 聚合查询

```json
{
    "aggs":{//聚合操作
        "price_group":{//名称，随意起的
            "terms":{//分组
                "field":"price"//分组字段
            }
        }
    }
    ,
    "size":0 // 显示原始数据的个数
}
```

```json
{
    "aggs":{//聚合操作
        "price_group":{//名称，随意起的
            "max":{//分组
                "field":"price"//分组字段
            }
        }
    }
    ,
    "size":0
}
```

还可以使用 min,max,avg

### 2.2.3 修改

#### 完全覆盖

在Postman中，向ES服务器发起**PUT**请求 

```url
http://localhost:9200/shopping/_doc/1001
```

```json
{
    "_index": "shopping",
    "_type": "_doc",
    "_id": "1001",
    "_version": 2,		#原来是1
    "result": "updated",#原来是created
    "_shards": {
        "total": 2,
        "successful": 1,
        "failed": 0
    },
    "_seq_no": 6,
    "_primary_term": 1
}
```

#### 局部更新

不是幂等性，所以使用**POST**

```URL
http://localhost:9200/shopping/_update/1001
```

```json
{
    "doc":{
        "title": "小米手机"
    }
}
```

返回的数据

```json
{
    "_index": "shopping",
    "_type": "_doc",
    "_id": "1001",
    "_version": 4,
    "result": "updated",
    "_shards": {
        "total": 2,
        "successful": 1,
        "failed": 0
    },
    "_seq_no": 8,
    "_primary_term": 1
}
```

### 2.2.4 删除

在Postman中，向ES服务器发起**DELETE**请求

```url
http://localhost:9200/shopping/_doc/1001
```

## 2.3 映射

我们知道index就相当于数据库

那么mapping就相对于表结构

### 2.3.1 创建映射

PUT请求

```url
http://localhost:9200/user/_mapping
```

```json
{
    "properties":{
        "name":{
            "type":"text",
            "index":true
        },
        "sex":{
            "type":"keyword",
            "index":true
        },
        "tel":{
            "type":"keyword",
            "index":false
        }
    }
}
```

![image-20210502092009961](https://gitee.com/aik-aid/picture/raw/master/image-20210502092009961.png)

### 2.3.2 查询映射

GET请求

```url
http://localhost:9200/user/_mapping
```

# 3. Java API

## 3.1 环境准备

### 3.1.1 导入依赖

```xml
    <dependencies>
        <dependency>
            <groupId>org.elasticsearch</groupId>
            <artifactId>elasticsearch</artifactId>
            <version>7.8.0</version>
        </dependency>
<!--        客户端-->
        <dependency>
            <groupId>org.elasticsearch.client</groupId>
            <artifactId>elasticsearch-rest-high-level-client</artifactId>
            <version>7.8.0</version>
        </dependency>
<!--        es依赖的log4j-->
        <dependency>
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-api</artifactId>
            <version>2.8.2</version>
        </dependency>
        <dependency>
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-core</artifactId>
            <version>2.8.2</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.9.9</version>
        </dependency>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.13.1</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
```

### 3.1.2 客户端对象

```java
public class esTest {
    public static void main(String[] args) throws IOException {
//        创建ES客户端
        RestHighLevelClient esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost",9200,"http"))
        );
//         关闭ES客户端
        esClient.close();
    }
}
```

## 3.2 索引操作

### 3.2.1 创建索引

```java
public class esTest {
    public static void main(String[] args) throws IOException {
//        创建ES客户端
        RestHighLevelClient esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost",9200,"http"))
        );
//        创建索引
        CreateIndexRequest request = new CreateIndexRequest("user1");
        CreateIndexResponse response = esClient.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(response.isAcknowledged());
//         关闭ES客户端
        esClient.close();
    }
}

```

### 3.2.2 查询索引

```java
public class esTest {
    public static void main(String[] args) throws IOException {
//        创建ES客户端
        RestHighLevelClient esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost",9200,"http"))
        );
//        查询索引
        GetIndexRequest request = new GetIndexRequest("user");
        GetIndexResponse response = esClient.indices().get(request, RequestOptions.DEFAULT);
        System.out.println(response.getAliases());
        System.out.println(response.getMappings());
        System.out.println(response.getSettings());
//         关闭ES客户端
        esClient.close();
    }
}
```

### 3.2.3 删除索引

```java
 public class esTest {
    public static void main(String[] args) throws IOException {
//        创建ES客户端
        RestHighLevelClient esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost",9200,"http"))
        );
//        查询索引

        DeleteIndexRequest request = new DeleteIndexRequest("user1");
        AcknowledgedResponse response = esClient.indices().delete(request, RequestOptions.DEFAULT);
        System.out.println(response.isAcknowledged());
//         关闭ES客户端
        esClient.close();
    }
}
```

## 3.3 文档操作

### 3.3.1 新增

#### 单个增加

```java
public class esTest {
    public static void main(String[] args) throws IOException {
//        创建ES客户端
        RestHighLevelClient esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost",9200,"http"))
        );
//        新增文档
        IndexRequest request = new IndexRequest();
        request.index("user").id("1003");
        User user=new User("zhangsan","男",30);
        //将User转化为jason格式
        ObjectMapper mapper = new ObjectMapper();
        String userJson = mapper.writeValueAsString(user);
        request.source(userJson, XContentType.JSON);
        IndexResponse response = esClient.index(request, RequestOptions.DEFAULT);
        System.out.println(response.getResult());

//         关闭ES客户端
        esClient.close();
    }
}
```

#### 批量增加

```java
public class esTest {
    public static void main(String[] args) throws IOException {
//        创建ES客户端
        RestHighLevelClient esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost",9200,"http"))
        );


        BulkRequest request = new BulkRequest();
        request.add(new IndexRequest().index("user").id("1001").source(XContentType.JSON,"name","zhangsan"));
        request.add(new IndexRequest().index("user").id("1002").source(XContentType.JSON,"name","lisi"));
        request.add(new IndexRequest().index("user").id("1003").source(XContentType.JSON,"name","wangwu"));
        BulkResponse response = esClient.bulk(request, RequestOptions.DEFAULT);
        System.out.println(response.getTook());
        System.out.println(response.getItems());

//         关闭ES客户端
        esClient.close();
    }
}
```



### 3.3.2 修改

#### 局部修改

```java
public class esTest {
    public static void main(String[] args) throws IOException {
//        创建ES客户端
        RestHighLevelClient esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost",9200,"http"))
        );
//        新增文档
        UpdateRequest request = new UpdateRequest();
        request.index("user").id("1003");
        request.doc(XContentType.JSON,"sex","女");
        UpdateResponse response = esClient.update(request, RequestOptions.DEFAULT);
        System.out.println(response.getResult());

//         关闭ES客户端
        esClient.close();
    }
}
```

### 3.3.3 查询数据

#### 简单查询

```java
public class esTest {
    public static void main(String[] args) throws IOException {
//        创建ES客户端
        RestHighLevelClient esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost",9200,"http"))
        );

        GetRequest request = new GetRequest();
        request.index("user").id("1003");
        GetResponse response = esClient.get(request, RequestOptions.DEFAULT);
        System.out.println(response.getSourceAsString());

//         关闭ES客户端
        esClient.close();
    }
}

```

#### 全量查询

```java
public class esTest {
    public static void main(String[] args) throws IOException {
//        创建ES客户端
        RestHighLevelClient esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost",9200,"http"))
        );
        SearchRequest request = new SearchRequest();
        request.indices("user");
        request.source(new SearchSourceBuilder().query(QueryBuilders.matchAllQuery()));
        SearchResponse response = esClient.search(request, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        System.out.println(hits.getTotalHits());
        for (SearchHit searchHits : hits) {
            System.out.println(searchHits.getSourceAsString());
        }

        System.out.println(response.getTook());

//         关闭ES客户端
        esClient.close();
    }
}
```

#### 条件查询

```java
public class esTest {
    public static void main(String[] args) throws IOException {
//        创建ES客户端
        RestHighLevelClient esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost",9200,"http"))
        );
        
        SearchRequest request = new SearchRequest();
        request.indices("user");
        request.source(new SearchSourceBuilder().query(QueryBuilders.termQuery("age",30)));
        SearchResponse response = esClient.search(request, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
//         关闭ES客户端
        esClient.close();
    }
}
```

#### 分页查询

```java
public class esTest {
    public static void main(String[] args) throws IOException {
//        创建ES客户端
        RestHighLevelClient esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost",9200,"http"))
        );

        SearchRequest request = new SearchRequest();
        request.indices("user");
        SearchSourceBuilder builder = new SearchSourceBuilder().query(QueryBuilders.matchAllQuery());
        builder.from(0);
        builder.size(2);
        request.source(builder);
        SearchResponse response = esClient.search(request, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
//         关闭ES客户端
        esClient.close();
    }
}
```

#### 查询排序

```java

public class esTest {
    public static void main(String[] args) throws IOException {
//        创建ES客户端
        RestHighLevelClient esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost",9200,"http"))
        );

        SearchRequest request = new SearchRequest();
        request.indices("user");
        SearchSourceBuilder builder = new SearchSourceBuilder().query(QueryBuilders.matchAllQuery());

        builder.sort("age", SortOrder.DESC);
        request.source(builder);
        SearchResponse response = esClient.search(request, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
//         关闭ES客户端
        esClient.close();
    }
}

```

#### 过滤字段

```
public class esTest {
    public static void main(String[] args) throws IOException {
//        创建ES客户端
        RestHighLevelClient esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))
        );

        SearchRequest request = new SearchRequest();
        request.indices("user");
        SearchSourceBuilder builder = new SearchSourceBuilder().query(QueryBuilders.matchAllQuery());
        String[] includes = {};
        String[] excludes = {"age"};
        builder.fetchSource(includes,excludes);
        request.source(builder);
        SearchResponse response = esClient.search(request, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
//         关闭ES客户端
        esClient.close();
    }
}
```

#### 组合查询

```java
public class esTest {
    public static void main(String[] args) throws IOException {
//        创建ES客户端
        RestHighLevelClient esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))
        );

        SearchRequest request = new SearchRequest();
        request.indices("user");
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.should(QueryBuilders.matchQuery("age",30));
        boolQueryBuilder.should(QueryBuilders.matchQuery("sex","男"));
        builder.query(boolQueryBuilder);
        request.source(builder);
        SearchResponse response = esClient.search(request, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
//         关闭ES客户端
        esClient.close();
    }
```

#### 范围查询

```java
public class esTest {
    public static void main(String[] args) throws IOException {
//        创建ES客户端
        RestHighLevelClient esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))
        );

        SearchRequest request = new SearchRequest();
        request.indices("user");
        SearchSourceBuilder builder = new SearchSourceBuilder();

        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("age");

        rangeQueryBuilder.gte(30);
        rangeQueryBuilder.lte(40);

        builder.query(rangeQueryBuilder);

        request.source(builder);
        SearchResponse response = esClient.search(request, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
//         关闭ES客户端
        esClient.close();
    }
}
```

#### 模糊查询

```java
public class esTest {
    public static void main(String[] args) throws IOException {
//        创建ES客户端
        RestHighLevelClient esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))
        );

        SearchRequest request = new SearchRequest();
        request.indices("user");
        SearchSourceBuilder builder = new SearchSourceBuilder();

        FuzzyQueryBuilder fuzziness = QueryBuilders.fuzzyQuery("name", "wangwu").fuzziness(Fuzziness.ONE);


        builder.query(fuzziness);

        request.source(builder);
        SearchResponse response = esClient.search(request, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
//         关闭ES客户端
        esClient.close();
    }
}
```

#### 高亮查询

```java
```

#### 聚合查询

```java
public class esTest {
    public static void main(String[] args) throws IOException {
//        创建ES客户端
        RestHighLevelClient esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))
        );

        SearchRequest request = new SearchRequest();
        request.indices("user");

        SearchSourceBuilder builder = new SearchSourceBuilder();

        AggregationBuilder maxAggregationBuilder = AggregationBuilders.max("maxAge").field("age");

        builder.aggregation(maxAggregationBuilder);

        request.source(builder);
        SearchResponse response = esClient.search(request, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
//         关闭ES客户端
        esClient.close();
    }
}

```

#### 分组查询

```java
```



### 3.3.4 删除数据

#### 单个删除

```java
public class esTest {
    public static void main(String[] args) throws IOException {
//        创建ES客户端
        RestHighLevelClient esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost",9200,"http"))
        );

        DeleteRequest request = new DeleteRequest();
        request.index("user").id("1001");
        esClient.delete(request,RequestOptions.DEFAULT);
        
//         关闭ES客户端
        esClient.close();
    }
}

```

#### 批量删除

```java
public class esTest {
    public static void main(String[] args) throws IOException {
//        创建ES客户端
        RestHighLevelClient esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost",9200,"http"))
        );


        BulkRequest request = new BulkRequest();
        request.add(new DeleteRequest().index("user").id("1001"));
        request.add(new DeleteRequest().index("user").id("1002"));
        request.add(new DeleteRequest().index("user").id("1003"));
        BulkResponse response = esClient.bulk(request, RequestOptions.DEFAULT);
        System.out.println(response.getTook());
        System.out.println(response.getItems());

//         关闭ES客户端
        esClient.close();
    }
}

```

# 4. 部署环境

## 4.1 集群部署

### Windows部署

### Linux单节点部署

### Linux集群部署