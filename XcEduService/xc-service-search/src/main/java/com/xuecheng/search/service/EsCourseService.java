package com.xuecheng.search.service;
import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class EsCourseService {
    @Value("${xuecheng.course.index}")
    private String index;
    @Value("${xuecheng.course.type}")
    private String type;
    @Value("${xuecheng.course.source_field}")
    private String source_filed;
    @Value("${xuecheng.media.source_field}")
    private String source_media_field;
    @Value("${xuecheng.media.type}")
    private String media_type;
    @Value("${xuecheng.media.index}")
    private String media_index;
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    private HighlightBuilder highlightBuilder;

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
        //创建布尔查询对象
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
        //设置分页参数
        if (page<=0){
            page=1;
        }
        if (size<0){
            size=12;
        }
        int from=(page-1)*size;
        searchSourceBuilder.from(from);
        searchSourceBuilder.size(size);
        //设置高亮
        HighlightBuilder highlightBuilder=new HighlightBuilder();
        highlightBuilder.preTags("<font class='eslight'>");
        highlightBuilder.postTags("</font>");
        //设置高亮字段
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
        searchSourceBuilder.highlighter(highlightBuilder);

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
                //去高亮字段name
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                if (highlightFields!=null){
                    HighlightField name1 = highlightFields.get("name");
                    if (name1!=null){
                        Text[] fragments = name1.fragments();
                        StringBuffer stringBuffer=null;
                        for (Text fragment : fragments) {
                            stringBuffer.append(fragment);
                        }
                        name=stringBuffer.toString();
                    }
                }
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

    /**
     * 根据课程id火的信息
     * @param id
     * @return
     */
    public Map<String, CoursePub> getAll(String id) {
        //使用ES的客户端发送ES请求获得课程信息
        //定义一个搜索请求对象
        SearchRequest searchRequest=new SearchRequest(index);
        //指定type
        searchRequest.types(type);
        //指定searchbuilder
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();

        //设置使用termquery
        searchSourceBuilder.query(QueryBuilders.termQuery("id",id));
        //过滤原字段 这儿不要果露原字段
        //searchSourceBuilder.fetchSource();
        searchRequest.source(searchSourceBuilder);
        Map<String,CoursePub> map=new HashMap<>();
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            SearchHits hits = searchResponse.getHits();
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit searchHit : searchHits) {
                CoursePub coursePub=new CoursePub();
                Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
                String id1 =(String) sourceAsMap.get("id");
                coursePub.setId(id1);
                String name = (String) sourceAsMap.get("name");
                String grade = (String) sourceAsMap.get("grade");
                String charge = (String) sourceAsMap.get("charge");
                String pic = (String) sourceAsMap.get("pic");
                String description = (String) sourceAsMap.get("description");
                String teachplan = (String) sourceAsMap.get("teachplan");
                coursePub.setName(name);
                coursePub.setPic(pic);
                coursePub.setGrade(grade);
                coursePub.setTeachplan(teachplan);
                coursePub.setDescription(description);
                coursePub.setCharge(charge);
                map.put(id1,coursePub);
            }
        }catch (IOException ex){
            ex.printStackTrace();
        }
        return map;
    }

    /**
     * 课程媒资信息(根据多个id)
     * @param id
     * @return
     */
    public QueryResponseResult<TeachplanMediaPub> getMedia(String[] id) {
        //定义一个搜索对象
        SearchRequest searchRequest=new SearchRequest(media_index);
        //指定type
        searchRequest.types(media_type);
        //
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        //使用termsquery 传入数组 根据多个id查询
        searchSourceBuilder.query(QueryBuilders.termsQuery("teachplan_id",id));
        //设置过滤字段
        String[] split = source_media_field.split(",");
        searchSourceBuilder.fetchSource(split,new String[]{});
        searchRequest.source(searchSourceBuilder);
        //使用es的客户端
        List<TeachplanMediaPub> teachplanMediaPubList=new ArrayList<>();
        long total=0l;
        try {
            SearchResponse search = restHighLevelClient.search(searchRequest);
            SearchHits hits = search.getHits();
            total=hits.totalHits;
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                TeachplanMediaPub teachplanMediaPub=new TeachplanMediaPub();
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                //取出课程计划媒资信息
                String courseid = (String) sourceAsMap.get("courseid");
                String media_id = (String) sourceAsMap.get("media_id");
                String media_url = (String) sourceAsMap.get("media_url");
                String teachplan_id = (String) sourceAsMap.get("teachplan_id");
                String media_fileoriginalname = (String) sourceAsMap.get("media_fileoriginalname");
                teachplanMediaPub.setCourseId(courseid);
                teachplanMediaPub.setMediaUrl(media_url);
                teachplanMediaPub.setMediaFileOriginalName(media_fileoriginalname);
                teachplanMediaPub.setMediaId(media_id);
                teachplanMediaPub.setTeachplanId(teachplan_id);
                //将数据加入列表
                teachplanMediaPubList.add(teachplanMediaPub);
            }
        }catch (IOException ex){
            ex.printStackTrace();
        }
        QueryResult<TeachplanMediaPub> queryResult=new QueryResult<>();
        queryResult.setList(teachplanMediaPubList);
        queryResult.setTotal(total);
        QueryResponseResult<TeachplanMediaPub> queryResponseResult=new QueryResponseResult(CommonCode.SUCCESS,queryResult);
        return queryResponseResult;
    }
}
