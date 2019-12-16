package com.xuecheng.search.service;

import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class EsCourseService {
    @Value("${xuecheng.course.index}")
    private String index;
    @Value("${xuecheng.course.type}")
    private String type;
    @Value("${xuecheng.course.source_field}")
    private  String source_filed;
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    //配置过滤原字段
    //课程搜索
    public QueryResponseResult<CoursePub> list(int page, int size, CourseSearchParam courseSearchParam) {
        //创建搜索请求对象
        SearchRequest searchRequest=new SearchRequest(index);
        //设置搜索类型
        searchRequest.types(type);
        //制定过滤原字段
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        String[] split = source_filed.split(",");
        searchSourceBuilder.fetchSource(split,new String[]{});
        //将sourcebuilder设置
        //搜索条件
        //创建不二查询对象
        BoolQueryBuilder boolQueryBuilder= QueryBuilders.boolQuery();
        //根据关键字搜索
        if (StringUtils.isNotEmpty(courseSearchParam.getKeyword())){
            MultiMatchQueryBuilder field = QueryBuilders.multiMatchQuery(courseSearchParam.getKeyword(), "name", "description", "teachplan")
                    .minimumShouldMatch("70%").field("name", 10);
            boolQueryBuilder.must(field);
        }
        //根据分类过滤器
        if (StringUtils.isNotEmpty(courseSearchParam.getMt())){
            //分类
            boolQueryBuilder.filter(QueryBuilders.termQuery("mt",courseSearchParam.getMt()));
        }
        if (StringUtils.isNotEmpty(courseSearchParam.getSt())){
            boolQueryBuilder.filter(QueryBuilders.termQuery("st",courseSearchParam.getSt()));
        }
        //难度登记分类
        if (StringUtils.isNotEmpty(courseSearchParam.getGrade())){
            boolQueryBuilder.filter(QueryBuilders.termQuery("grade",courseSearchParam.getGrade()));
        }
        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        //定义封装结果集
        QueryResult<CoursePub> queryResult=new QueryResult<>();
        List<CoursePub> list=new ArrayList<>();
        //
        try {
            //执行搜索
            SearchResponse search = restHighLevelClient.search(searchRequest);
            //获取相应结果
            SearchHits hits = search.getHits();
            //匹配总记录数
            long totalHits = hits.getTotalHits();
            queryResult.setTotal(totalHits);
            SearchHit[] hits1 = hits.getHits();
            for (SearchHit hit : hits1) {
                CoursePub coursePub=new CoursePub();
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                String name = (String)sourceAsMap.get("name");
                coursePub.setName(name);
                //图片
                String pic = (String) sourceAsMap.get("pic");
                coursePub.setPic(pic);
                //价格
                Double price = null;
                try {
                    if(sourceAsMap.get("price")!=null ){
                        price = ((Double) sourceAsMap.get("price"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                coursePub.setPrice(price);
                Double price_old = null;
                try {
                    if(sourceAsMap.get("price_old")!=null ){
                        price_old = (Double) sourceAsMap.get("price_old");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                coursePub.setPrice_old(price_old);
                list.add(coursePub);
                //拼装对象
            }
            queryResult.setList(list);
        }catch (IOException ex){
            ex.printStackTrace();
        }
        QueryResponseResult<CoursePub> queryResponseResult=new QueryResponseResult<CoursePub>(CommonCode.SUCCESS,queryResult);
        return queryResponseResult;
    }
}
