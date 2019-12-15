package com.xuecheng.search;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.naming.directory.SearchResult;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestSearch {
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Autowired
    private RestClient restClient;
    //搜索全部记录
    @Test
    public void testSearch() throws IOException, ParseException {
        //设置搜索请求对象
        SearchRequest searchRequest=new SearchRequest("xc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索构造源对象
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        //搜索方法
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        //设置字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});
        //设置搜索源
        searchRequest.source(searchSourceBuilder);
        //向es发送请求
        SearchResponse search = restHighLevelClient.search(searchRequest);
        //搜索结果
        SearchHits hits = search.getHits();
        //匹配得到总记录数
        long totalHits = hits.getTotalHits();
        //得到匹配度高的
        SearchHit[] hits1 = hits.getHits();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (SearchHit hit : hits1) {
            //文档主键
            String id = hit.getId();
            //元文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            //学习模式
            String studymodel = (String) sourceAsMap.get("studymodel");
            Double price = (Double) sourceAsMap.get("price");
            //Date timestamp = simpleDateFormat.parse((String) sourceAsMap.get("timestamp"));
            System.out.println(name+"  "+studymodel+"  "+price+"  ");
        }
    }
    //分页查询
    @Test
    public void testSearchPage() throws IOException, ParseException {
        //设置搜索请求对象
        SearchRequest searchRequest=new SearchRequest("xc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索构造源对象
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        //搜索方法
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        //设置分页参数
        //页码
        int page=1;
        //每页记录数
        int size=1;
        int from=(page-1)*size;
        searchSourceBuilder.from(from);//起始记录下表，从0开始
        searchSourceBuilder.size(size);//每页显示记录数
        // 设置字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});
        //设置搜索源
        searchRequest.source(searchSourceBuilder);
        //向es发送请求
        SearchResponse search = restHighLevelClient.search(searchRequest);
        //搜索结果
        SearchHits hits = search.getHits();
        //匹配得到总记录数
        long totalHits = hits.getTotalHits();
        //得到匹配度高的
        SearchHit[] hits1 = hits.getHits();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (SearchHit hit : hits1) {
            //文档主键
            String id = hit.getId();
            //元文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            //学习模式
            String studymodel = (String) sourceAsMap.get("studymodel");
            Double price = (Double) sourceAsMap.get("price");
            //Date timestamp = simpleDateFormat.parse((String) sourceAsMap.get("timestamp"));
            System.out.println(name+"  "+studymodel+"  "+price+"  ");
        }
    }
    //term查询
    @Test
    public void testSearchTerm() throws IOException, ParseException {
        //设置搜索请求对象
        SearchRequest searchRequest=new SearchRequest("xc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索构造源对象
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        //搜索方法
        searchSourceBuilder.query(QueryBuilders.termQuery("name","spring"));
        //设置分页参数
        //页码
        int page=1;
        //每页记录数
        int size=1;
        int from=(page-1)*size;
        searchSourceBuilder.from(from);//起始记录下表，从0开始
        searchSourceBuilder.size(size);//每页显示记录数
        // 设置字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});
        //设置搜索源
        searchRequest.source(searchSourceBuilder);
        //向es发送请求
        SearchResponse search = restHighLevelClient.search(searchRequest);
        //搜索结果
        SearchHits hits = search.getHits();
        //匹配得到总记录数
        long totalHits = hits.getTotalHits();
        //得到匹配度高的
        SearchHit[] hits1 = hits.getHits();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (SearchHit hit : hits1) {
            //文档主键
            String id = hit.getId();
            //元文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            //学习模式
            String studymodel = (String) sourceAsMap.get("studymodel");
            Double price = (Double) sourceAsMap.get("price");
            //Date timestamp = simpleDateFormat.parse((String) sourceAsMap.get("timestamp"));
            System.out.println(name+"  "+studymodel+"  "+price+"  ");
        }
    }
    //根据id查询
    @Test
    public void testSearchById() throws IOException, ParseException {
        //设置搜索请求对象
        SearchRequest searchRequest=new SearchRequest("xc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索构造源对象
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        //定义数组id集合
        String[] ids=new String[]{"1","2"};
        //搜索方法
        searchSourceBuilder.query(QueryBuilders.termsQuery("_id",ids));
        //设置分页参数
        //页码
        int page=1;
        //每页记录数
        int size=1;
        int from=(page-1)*size;
        searchSourceBuilder.from(from);//起始记录下表，从0开始
        searchSourceBuilder.size(size);//每页显示记录数
        // 设置字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});
        //设置搜索源
        searchRequest.source(searchSourceBuilder);
        //向es发送请求
        SearchResponse search = restHighLevelClient.search(searchRequest);
        //搜索结果
        SearchHits hits = search.getHits();
        //匹配得到总记录数
        long totalHits = hits.getTotalHits();
        //得到匹配度高的
        SearchHit[] hits1 = hits.getHits();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (SearchHit hit : hits1) {
            //文档主键
            String id = hit.getId();
            //元文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            //学习模式
            String studymodel = (String) sourceAsMap.get("studymodel");
            Double price = (Double) sourceAsMap.get("price");
            //Date timestamp = simpleDateFormat.parse((String) sourceAsMap.get("timestamp"));
            System.out.println(name+"  "+studymodel+"  "+price+"  ");
        }
    }
    //multimatchqurey 多种映射查询
    @Test
    public void testSearchmultiMatchqurey() throws IOException, ParseException {
        //设置搜索请求对象
        SearchRequest searchRequest=new SearchRequest("xc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索构造源对象
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        //定义数组id集合
        String[] ids=new String[]{"1","2"};
        //搜索方法
        searchSourceBuilder.query(QueryBuilders.multiMatchQuery("spring 课","name","description").minimumShouldMatch("50%").field("name",10));
        // 设置字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp","description"},new String[]{});
        //设置搜索源
        searchRequest.source(searchSourceBuilder);
        //向es发送请求
        SearchResponse search = restHighLevelClient.search(searchRequest);
        //搜索结果
        SearchHits hits = search.getHits();
        //匹配得到总记录数
        long totalHits = hits.getTotalHits();
        //得到匹配度高的
        SearchHit[] hits1 = hits.getHits();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (SearchHit hit : hits1) {
            //文档主键
            String id = hit.getId();
            //元文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            //学习模式
            String studymodel = (String) sourceAsMap.get("studymodel");
            Double price = (Double) sourceAsMap.get("price");
            //Date timestamp = simpleDateFormat.parse((String) sourceAsMap.get("timestamp"));
            String description =(String) sourceAsMap.get("description");
            System.out.println(name+"  "+studymodel+"  "+price+"  "+description);
        }
    }
    //bollean查询
    @Test
    public void testSearchBoolean() throws IOException, ParseException {
        //设置搜索请求对象
        SearchRequest searchRequest=new SearchRequest("xc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索构造源对象
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        //定义数组id集合
        String[] ids=new String[]{"1","2"};
        //定义mutipartqurey
        MultiMatchQueryBuilder multipart = QueryBuilders.multiMatchQuery("spring 课", "name", "description").minimumShouldMatch("50%").field("name", 10);
        //定义一个term查询
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("description", "spring");
        //定义一个boolean查询
        BoolQueryBuilder boolQueryBuilder=QueryBuilders.boolQuery();
        boolQueryBuilder.must(multipart);
        boolQueryBuilder.must(termQueryBuilder);
        //搜索方法
        searchSourceBuilder.query(boolQueryBuilder);
        // 设置字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp","description"},new String[]{});
        //设置搜索源
        searchRequest.source(searchSourceBuilder);
        //向es发送请求
        SearchResponse search = restHighLevelClient.search(searchRequest);
        //搜索结果
        SearchHits hits = search.getHits();
        //匹配得到总记录数
        long totalHits = hits.getTotalHits();
        //得到匹配度高的
        SearchHit[] hits1 = hits.getHits();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (SearchHit hit : hits1) {
            //文档主键
            String id = hit.getId();
            //元文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            //学习模式
            String studymodel = (String) sourceAsMap.get("studymodel");
            Double price = (Double) sourceAsMap.get("price");
            //Date timestamp = simpleDateFormat.parse((String) sourceAsMap.get("timestamp"));
            String description =(String) sourceAsMap.get("description");
            System.out.println(name+"  "+studymodel+"  "+price+"  "+description);
        }
    }
    //过滤器查询
    @Test
    public void testSearchByFilter() throws IOException, ParseException {
        //设置搜索请求对象
        SearchRequest searchRequest=new SearchRequest("xc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索构造源对象
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        //定义数组id集合
        String[] ids=new String[]{"1","2"};
        //定义mutipartqurey
        MultiMatchQueryBuilder multipart = QueryBuilders.multiMatchQuery("spring 课", "name", "description").minimumShouldMatch("50%").field("name", 10);
        //定义一个term查询
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("description", "spring");
        //定义一个boolean查询
        BoolQueryBuilder boolQueryBuilder=QueryBuilders.boolQuery();
        boolQueryBuilder.must(multipart);
        boolQueryBuilder.must(termQueryBuilder);
        //定义锅炉条件
        boolQueryBuilder.filter(QueryBuilders.termQuery("studymodel","21001"));
        //搜索方法
        searchSourceBuilder.query(boolQueryBuilder);
        // 设置字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp","description"},new String[]{});
        //设置搜索源
        searchRequest.source(searchSourceBuilder);
        //向es发送请求
        SearchResponse search = restHighLevelClient.search(searchRequest);
        //搜索结果
        SearchHits hits = search.getHits();
        //匹配得到总记录数
        long totalHits = hits.getTotalHits();
        //得到匹配度高的
        SearchHit[] hits1 = hits.getHits();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (SearchHit hit : hits1) {
            //文档主键
            String id = hit.getId();
            //元文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            //学习模式
            String studymodel = (String) sourceAsMap.get("studymodel");
            Double price = (Double) sourceAsMap.get("price");
            //Date timestamp = simpleDateFormat.parse((String) sourceAsMap.get("timestamp"));
            String description =(String) sourceAsMap.get("description");
            System.out.println(name+"  "+studymodel+"  "+price+"  "+description);
        }
    }

    /**
     * 高亮显示
     * @throws IOException
     * @throws ParseException
     */
    @Test
    public void testSearchByHignlight() throws IOException, ParseException {
        //设置搜索请求对象
        SearchRequest searchRequest=new SearchRequest("xc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索构造源对象
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        //定义数组id集合
        String[] ids=new String[]{"1","2"};
        //定义mutipartqurey
        MultiMatchQueryBuilder multipart = QueryBuilders.multiMatchQuery("spring 课", "name", "description").minimumShouldMatch("50%").field("name", 10);
        //定义一个term查询
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("description", "spring");
        //定义一个boolean查询
        BoolQueryBuilder boolQueryBuilder=QueryBuilders.boolQuery();
        boolQueryBuilder.must(multipart);
        boolQueryBuilder.must(termQueryBuilder);
        //定义过滤条件
       // boolQueryBuilder.filter(QueryBuilders.termQuery("studymodel","21001"));
        //设置高亮对象
        HighlightBuilder highlightBuilder=new HighlightBuilder();
        //设置高亮标签
        highlightBuilder.preTags("<tag>");
        highlightBuilder.postTags("</tag>");
        //设置高亮字段
        highlightBuilder.fields().add(new HighlightBuilder.Field("studymodel"));
        searchSourceBuilder.highlighter(highlightBuilder);
        //搜索方法
        searchSourceBuilder.query(boolQueryBuilder);
        // 设置字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp","description"},new String[]{});
        //设置搜索源
        searchRequest.source(searchSourceBuilder);
        //向es发送请求
        SearchResponse search = restHighLevelClient.search(searchRequest);
        //搜索结果
        SearchHits hits = search.getHits();
        //匹配得到总记录数
        long totalHits = hits.getTotalHits();
        //得到匹配度高的
        SearchHit[] hits1 = hits.getHits();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (SearchHit hit : hits1) {
            //文档主键
            String id = hit.getId();
            //元文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            //学习模式
            String studymodel =null;
            //取出syudymodel高亮字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (highlightFields!=null){
                HighlightField studymodel1 = highlightFields.get("studymodel");
                if (studymodel1!=null){
                    Text[] fragments = studymodel1.getFragments();
                    StringBuffer stringBuffer=new StringBuffer();
                    for (Text fragment : fragments) {
                        stringBuffer.append(fragment);
                    }
                    studymodel=stringBuffer.toString();
                }
            }
            Double price = (Double) sourceAsMap.get("price");
            //Date timestamp = simpleDateFormat.parse((String) sourceAsMap.get("timestamp"));
            String description =(String) sourceAsMap.get("description");
            System.out.println(name+"  "+studymodel+"  "+price+"  "+description);
        }
    }
}
