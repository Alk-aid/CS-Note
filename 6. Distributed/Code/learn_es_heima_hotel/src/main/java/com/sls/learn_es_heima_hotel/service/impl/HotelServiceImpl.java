package com.sls.learn_es_heima_hotel.service.impl;

import com.alibaba.fastjson.JSON;
import com.sls.learn_es_heima_hotel.po.HotelDoc;
import com.sls.learn_es_heima_hotel.service.HotelService;
import com.sls.learn_es_heima_hotel.vo.PageResult;
import com.sls.learn_es_heima_hotel.vo.RequestParams;
import org.apache.ibatis.annotations.Mapper;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class HotelServiceImpl implements HotelService {

    @Autowired
    private RestHighLevelClient client;

    @Override
    public PageResult search(RequestParams params) {
        //1. 准备request
        SearchRequest request = new SearchRequest("hotel");
        //2. 准备DSL
        buildBasicQuery(params, request);
        //2.2 分页
        int page = params.getPage();
        int size = params.getSize();
        request.source().from((page - 1) * size).size(size);
        //2.3 排序
        String location = params.getLocation();
        if (location != null && !location.equals("")) {
            request.source().sort(SortBuilders
                    .geoDistanceSort("location", new GeoPoint(location))
                    .order(SortOrder.ASC)
                    .unit(DistanceUnit.KILOMETERS)
            );
        }
        //3. 发送
        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            return handleResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //query封装
    private void buildBasicQuery(RequestParams params, SearchRequest request) {
        // 1.构建BooleanQuery
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        // 关键字搜索
        String key = params.getKey();
        if (key == null || "".equals(key)) {
            boolQuery.must(QueryBuilders.matchAllQuery());
        } else {
            boolQuery.must(QueryBuilders.matchQuery("all", key));
        }
        // 城市条件
        if (params.getCity() != null && !params.getCity().equals("")) {
            boolQuery.filter(QueryBuilders.termQuery("city", params.getCity()));
        }
        // 品牌条件
        if (params.getBrand() != null && !params.getBrand().equals("")) {
            boolQuery.filter(QueryBuilders.termQuery("brand", params.getBrand()));
        }
        // 星级条件
        if (params.getStarName() != null && !params.getStarName().equals("")) {
            boolQuery.filter(QueryBuilders.termQuery("starName", params.getStarName()));
        }
        // 价格
        if (params.getMinPrice() != null && params.getMaxPrice() != null) {
            boolQuery.filter(QueryBuilders
                    .rangeQuery("price")
                    .gte(params.getMinPrice())
                    .lte(params.getMaxPrice())
            );
        }

        // 2.算分控制
        FunctionScoreQueryBuilder functionScoreQuery =
                QueryBuilders.functionScoreQuery(
                        // 原始查询，相关性算分的查询
                        boolQuery,
                        // function score的数组
                        new FunctionScoreQueryBuilder.FilterFunctionBuilder[]{
                                // 其中的一个function score 元素
                                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                                        // 过滤条件
                                        QueryBuilders.termQuery("isAD", true),
                                        // 算分函数
                                        ScoreFunctionBuilders.weightFactorFunction(10)
                                )
                        });
        request.source().query(functionScoreQuery);
    }

    // 结果解析
    private PageResult handleResponse(SearchResponse response) {
        // 4.解析响应
        SearchHits searchHits = response.getHits();
        // 4.1.获取总条数
        long total = searchHits.getTotalHits().value;
        // 4.2.文档数组
        SearchHit[] hits = searchHits.getHits();
        // 4.3.遍历
        List<HotelDoc> hotels = new ArrayList<>();
        for (SearchHit hit : hits) {
            // 获取文档source
            String json = hit.getSourceAsString();
            // 反序列化
            HotelDoc hotelDoc = JSON.parseObject(json, HotelDoc.class);
            // 获取排序值
            Object[] sortValues = hit.getSortValues();
            if (sortValues.length > 0){
                hotelDoc.setDistance(sortValues[0]);
            }
            // 放入集合
            hotels.add(hotelDoc);
        }
        // 4.4.封装返回
        return new PageResult(total, hotels);
    }
}
