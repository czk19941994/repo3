package com.xuecheng.search;

import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestIndex {
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Autowired
    private RestClient restClient;
    //创建索引库
    @Test
    public void createIndex() throws IOException {
        //创建索引对象
        CreateIndexRequest createIndexRequest=new CreateIndexRequest("xc_course");
        //设置参数
        createIndexRequest.settings(Settings.builder().put("number_of_shards","1").put("number_of_replicas","0"));
        //制定yingshe
        createIndexRequest.mapping("doc","{\n" +
                "\"properties\": {\n" +
                "\"name\": {\n" +
                "\"type\": \"text\",\n" +
                "\"analyzer\":\"ik_max_word\",\n" +
                "\"search_analyzer\":\"ik_smart\"\n" +
                "},\n" +
                "\"description\": {\n" +
                "\"type\": \"text\",\n" +
                "\"analyzer\":\"ik_max_word\",\n" +
                "\"search_analyzer\":\"ik_smart\"\n" +
                "},\n" +
                "\"pic\":{\n" +
                "\"type\":\"text\",\n" +
                "\"index\":false\n" +
                "},\n" +
                "\"studymodel\":{\n" +
                "\"type\":\"text\"\n" +
                "}\n" +
                "}\n" +
                "}", XContentType.JSON);

        //操作索引的客户端
        IndicesClient indices = restHighLevelClient.indices();
        //创建索引库
        CreateIndexResponse createIndexResponse = indices.create(createIndexRequest);
        //得到结果
        boolean acknowledged = createIndexResponse.isAcknowledged();
        System.out.println(acknowledged);
    }
    //删除索引库
    @Test
    public void deleteIndex() throws IOException {
        //删除索引对象
        DeleteIndexRequest deleteIndexRequest=new DeleteIndexRequest("xc_course");
        //操作索引的客户端
        IndicesClient indicesClient=restHighLevelClient.indices();
        //删除索引
        DeleteIndexResponse delete = indicesClient.delete(deleteIndexRequest);
        //得到相应
        boolean acknowledged = delete.isAcknowledged();
        System.out.println(acknowledged);
    }
    //添加文档
    @Test
    public void addDoc() throws IOException {
        //文档内容
        //准备json数据
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("name", "spring cloud实战");
        jsonMap.put("description", "本课程主要从四个章节进行讲解： 1.微服务架构入门 2.spring cloud 基础入门 3.实战Spring Boot 4.注册中心eureka。");
        jsonMap.put("studymodel", "201001");
        SimpleDateFormat dateFormat =new SimpleDateFormat("yyyy‐MM‐dd HH:mm:ss");
        jsonMap.put("timestamp", dateFormat.format(new Date()));
        jsonMap.put("price", 5.6f);
        //创建索引创建对象
        IndexRequest indexRequest=new IndexRequest("xc_course","doc");
        //文档内容
        indexRequest.source(jsonMap);
        //通过client进行http的请求
        IndexResponse index = restHighLevelClient.index(indexRequest);
        DocWriteResponse.Result result = index.getResult();
        System.out.println(result);
    }
    //查询文档
    @Test
    public void getDoc() throws IOException {
        GetRequest getRequest=new GetRequest("xc_course","doc","KB-b-m4BjO7acw6V4ctZ");
        GetResponse documentFields = restHighLevelClient.get(getRequest);
        Map<String, Object> sourceAsMap = documentFields.getSourceAsMap();
        System.out.println(sourceAsMap);
    }
}
