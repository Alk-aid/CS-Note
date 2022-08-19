# 1. 词频分析

基于词典进行分词: 使用JieBa

- 根据需要修改用户词典: "add_word_list.text"
- 根据需要修改停用词典: "stopwordlist.text"
- 根据需求过滤词性: "n, nz, vn, a, v, nr, ns"

- 创建同义词词典

# 2. tf-idf

tf-idf是一种统计方法, 用来评估一字词对于一个文件集或语料库中的其中一份文件的重要程度. 是一种用于信息检索 与 文本挖掘的常用加权技术

tf-idf = tf * idf

- 传统的 tf - idf	
  - tf = n / N; n 表示 字词某个文档中出现的频率; N 表示所有文档中出现频率
  - idf = log(D / d); D 表示总的文档数, d 表示词语所在文档数
- sklearn-tfidf
  - tf = n
  - idf = log(D + 1 / d + 1) + 1
  - 然后可以选用是否归一化

作用:

- 文本相似性

```python
q = "I get a coffee cup"
qtf_idf = vectorizer.transform([q])
res = cosine_similarity(tf_idf, qtf_idf)
res = res.ravel().argsort()[-3:]  #排序
print("\ntop 3 docs for '{}':\n{}".format(q, [docs[i] for i in res[::-1]]))
```



- 计算文字和词语之间的关系
- 抽取关键词语

```python
 
# 参考资料：https://github.com/fxsjy/jieba
 
# jieba.analyse.extract_tags(sentence, topK=20, withWeight=False, allowPOS=()) <br>
# sentence 为待提取的文本 <br>
# topK 为返回几个 TF/IDF 权重最大的关键词，默认值为 20 <br>
# withWeight 为是否一并返回关键词权重值，默认值为 False <br>
# allowPOS 仅包括指定词性的词，默认值为空，即不筛选 <br>
 
from jieba import analyse
text = "英语四六级是每名大学生都要经历的一项考试，每当考试结束之后，英语四六级考试都会出现不少“神翻译”。甚至有些老师调侃说：本身大量判卷是很辛苦的事情，但是这些“惊喜”真的是“苦中作乐”。"
tags = analyse.extract_tags(text, topK=20, withWeight=False, allowPOS=())
print(tags)
```



# 3. word2vec

> 做词语类比

重要假设: 文本中离的越近的词语相似度越高; 使用 CBOW 和 skip-gram 计算词向量矩阵

缺点:

- 没有考虑多义词, 窗口长度有限, 没有考虑全局的文本信息, 不是严格意义的语序

流程:

- 分词
- 模型训练
- 可视化
- 类比关系实验

# 4. glove

# 5. 情感分析

1. 数据预处理: 去重 去空值, 去除停用词	
2. 分词: jieba
3. 提取特征
4. 训练
5. 测试
6. 分析数据

分类算法:

- KNN
- 朴素贝叶斯

# 6. 主题模型分析

LDA:

