结巴分词(java版) jieba-analysis
===============================

首先感谢jieba分词原作者[fxsjy](https://github.com/fxsjy)，没有他的无私贡献，我们也不会结识到结巴
分词，更不会有现在的java版本。

结巴分词的原始版本为python编写，目前该项目在github上的关注量为170，
打星727次（最新的数据以原仓库为准），Fork238次，可以说已经有一定的用户群。

结巴分词(java版)只保留的原项目针对搜索引擎分词的功能(cut~forindex~、cut~forsearch~)，词性标注，关键词提取没有实现(今后如用到，可以考虑实现)。


原项目见：[https://github.com/huaban/jieba-analysis](https://github.com/huaban/jieba-analysis), 本项目为了使用简便合并了 https://github.com/sing1ee/jieba-solr，
本项目修复了原项目的部分 Bug 和 api，更方便集成 Lucene 或者 Solr 来使用。同时扩展了用户词库，能更方便的添加用户词库；

简介
====

支持分词模式
------------

-   Search模式，用于对用户查询词分词
-   Index模式，用于对索引文档分词

特性
----

-   支持多种分词模式
-   全角统一转成半角
-   用户词典功能
-   conf 目录有整理的搜狗细胞词库
-   因为性能原因，最新的快照版本去除词性标注，也希望有更好的 Pull
    Request 可以提供该功能。

如何获取
========

-   当前稳定版本

    ``` {.xml}
    <dependency>
      <groupId>com.huaban</groupId>
      <artifactId>jieba-analysis</artifactId>
      <version>1.0.2</version>
    </dependency>
    ```

-   当前快照版本

    ``` {.xml}
    <dependency>
      <groupId>com.huaban</groupId>
      <artifactId>jieba-analysis</artifactId>
      <version>1.0.3a</version>
    </dependency>
    ```

如何使用
========

-   Demo

### Java Analysis

``` {.java}

@Test
public void testDemo() {
    JiebaSegmenter segmenter = new JiebaSegmenter();
    String[] sentences =
        new String[] {"这是一个伸手不见五指的黑夜。我叫孙悟空，我爱北京，我爱Python和C++。", "我不喜欢日本和服。", "雷猴回归人间。",
                      "工信处女干事每月经过下属科室都要亲口交代24口交换机等技术性器件的安装工作", "结果婚的和尚未结过婚的"};
    for (String sentence : sentences) {
        System.out.println(segmenter.process(sentence, SegMode.INDEX).toString());
    }
}
```

### Solr结巴分词

```xml

    <fieldType name="text_jieba" class="solr.TextField" positionIncrementGap="100">
      <analyzer type="index">
        <tokenizer class="jieba.solr.JiebaTokenizerFactory"  segMode="SEARCH"/>
        <filter class="solr.StopFilterFactory" ignoreCase="true" words="stopwords.txt" />
        <filter class="solr.LowerCaseFilterFactory"/>
        <filter class="solr.SnowballPorterFilterFactory" language="English"/>
      </analyzer>
      <analyzer type="query">
        <tokenizer class="jieba.solr.JiebaTokenizerFactory"  segMode="SEARCH"/>
        <filter class="solr.StopFilterFactory" ignoreCase="true" words="stopwords.txt" />
        <filter class="solr.SynonymFilterFactory" synonyms="synonyms.txt" ignoreCase="true" expand="true"/>
        <filter class="solr.LowerCaseFilterFactory"/>
        <filter class="solr.SnowballPorterFilterFactory" language="English"/>
      </analyzer>
    </fieldType>

```

算法(wiki补充...)
=================

-   \[ \] 基于 `trie` 树结构实现高效词图扫描
-   \[ \] 生成所有切词可能的有向无环图 `DAG`
-   \[ \] 采用动态规划算法计算最佳切词组合
-   \[ \] 基于 `HMM` 模型，采用 `Viterbi` (维特比)算法实现未登录词识别

性能评估
========

-   测试机配置

``` {.screen}
Processor 2 Intel(R) Pentium(R) CPU G620 @ 2.60GHz
Memory：8GB

分词测试时机器开了许多应用(eclipse、emacs、chrome...)，可能
会影响到测试速度
```

-   *测试文本*
-   测试结果(单线程，对测试文本逐行分词，并循环调用上万次)

    ``` {.screen}
    循环调用一万次
    第一次测试结果：
    time elapsed:12373, rate:2486.986533kb/s, words:917319.94/s
    第二次测试结果：
    time elapsed:12284, rate:2505.005241kb/s, words:923966.10/s
    第三次测试结果：
    time elapsed:12336, rate:2494.445880kb/s, words:920071.30/s

    循环调用2万次
    第一次测试结果：
    time elapsed:22237, rate:2767.593144kb/s, words:1020821.12/s
    第二次测试结果：
    time elapsed:22435, rate:2743.167762kb/s, words:1011811.87/s
    第三次测试结果：
    time elapsed:22102, rate:2784.497726kb/s, words:1027056.34/s
    统计结果:词典加载时间1.8s左右，分词效率每秒2Mb多，近100万词。

    2 Processor Intel(R) Core(TM) i3-2100 CPU @ 3.10GHz
    12G 测试效果
    time elapsed:19597, rate:3140.428063kb/s, words:1158340.52/s
    time elapsed:20122, rate:3058.491639kb/s, words:1128118.44/s

    ```

使用本库项目
============

-   [analyzer-solr](https://github.com/sing1ee/analyzer-solr) @sing1ee

捐赠
===========

**一顿黄焖鸡**

![](http://7xkgzh.com1.z0.glb.clouddn.com/0a9db33a25bce898c088462ddb726e57.png?imageView2/5/w/300/h/300)

**请我喝一杯**

![](http://7xkgzh.com1.z0.glb.clouddn.com/01e2fc2635f7ac26a9e8b21157dc2840.png?imageView2/5/w/300/h/300)

**或者随君意**

![](http://7xkgzh.com1.z0.glb.clouddn.com/2344d83c9be4b56cb66f696dcfb25ceb.png?imageView2/5/w/300/h/300)


许可证
======

jieba(python版本)的许可证为MIT，jieba(java版本)的许可证为ApacheLicence
2.0

``` {.screen}
Copyright (C) 2013 Huaban Inc

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
```
