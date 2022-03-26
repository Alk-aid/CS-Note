package cn.itcast.hotel.service.impl;

import cn.itcast.hotel.mapper.HotelMapper;
import cn.itcast.hotel.pojo.Hotel;
import cn.itcast.hotel.pojo.HotelDoc;
import cn.itcast.hotel.pojo.PageResult;
import cn.itcast.hotel.pojo.RequestParams;
import cn.itcast.hotel.service.IHotelService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class HotelService extends ServiceImpl<HotelMapper, Hotel> implements IHotelService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public PageResult search(RequestParams params) {
        try {
            // 1.准备Request
            SearchRequest request = new SearchRequest("hotel");
            // 2.准备请求参数
            // 2.1.query
            buildBasicQuery(params, request);
            // 2.2.分页
            int page = params.getPage();
            int size = params.getSize();
            request.source().from((page - 1) * size).size(size);
            // 2.3.距离排序
            String location = params.getLocation();
            if (StringUtils.isNotBlank(location)) {
                request.source().sort(SortBuilders
                        .geoDistanceSort("location", new GeoPoint(location))
                        .order(SortOrder.ASC)
                        .unit(DistanceUnit.KILOMETERS)
                );
            }
            // 3.发送请求
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            // 4.解析响应
            return handleResponse(response);
        } catch (IOException e) {
            throw new RuntimeException("搜索数据失败", e);
        }
    }

    @Override
    public Map<String, List<String>> getFilters(RequestParams params) {
        try {
            // 1.准备请求
            SearchRequest request = new SearchRequest("hotel");
            // 2.请求参数
            // 2.1.query
            buildBasicQuery(params, request);
            // 2.2.size
            request.source().size(0);
            // 2.3.聚合
            buildAggregations(request);
            // 3.发出请求
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            // 4.解析结果
            Aggregations aggregations = response.getAggregations();
            Map<String, List<String>> filters = new HashMap<>(3);
            // 4.1.解析品牌
            List<String> brandList = getAggregationByName(aggregations, "brandAgg");
            filters.put("brand", brandList);
            // 4.1.解析品牌
            List<String> cityList = getAggregationByName(aggregations, "cityAgg");
            filters.put("city", cityList);
            // 4.1.解析品牌
            List<String> starList = getAggregationByName(aggregations, "starAgg");
            filters.put("starName", starList);

            return filters;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> getSuggestion(String key) {
        try {
            // 1.准备请求
            SearchRequest request = new SearchRequest("hotel");
            // 2.请求参数
            request.source().suggest(new SuggestBuilder()
                    .addSuggestion(
                            "hotelSuggest",
                            SuggestBuilders
                                    .completionSuggestion("suggestion")
                                    .size(10)
                                    .skipDuplicates(true)
                                    .prefix(key)
                    ));
            // 3.发出请求
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            // 4.解析
            Suggest suggest = response.getSuggest();
            // 4.1.根据名称获取结果
            CompletionSuggestion suggestion = suggest.getSuggestion("hotelSuggest");
            // 4.2.获取options
            List<String> list = new ArrayList<>();
            for (CompletionSuggestion.Entry.Option option : suggestion.getOptions()) {
                // 4.3.获取补全的结果
                String str = option.getText().toString();
                // 4.4.放入集合
                list.add(str);
            }
            return list;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(Long hotelId) {
        try {
            // 1.创建request
            DeleteRequest request = new DeleteRequest("hotel", hotelId.toString());
            // 2.发送请求
            restHighLevelClient.delete(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException("删除酒店数据失败", e);
        }
    }

    @Override
    public void saveById(Long hotelId) {
        try {
            // 查询酒店数据，应该基于Feign远程调用hotel-admin，根据id查询酒店数据（现在直接去数据库查）
            Hotel hotel = getById(hotelId);
            // 转换
            HotelDoc hotelDoc = new HotelDoc(hotel);

            // 1.创建Request
            IndexRequest request = new IndexRequest("hotel").id(hotelId.toString());
            // 2.准备参数
            request.source(JSON.toJSONString(hotelDoc), XContentType.JSON);
            // 3.发送请求
            restHighLevelClient.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException("新增酒店数据失败", e);
        }
    }

    private List<String> getAggregationByName(Aggregations aggregations, String aggName) {
        // 4.1.根据聚合名称，获取聚合结果
        Terms terms = aggregations.get(aggName);
        // 4.2.获取buckets
        List<? extends Terms.Bucket> buckets = terms.getBuckets();
        // 4.3.遍历
        List<String> list = new ArrayList<>(buckets.size());
        for (Terms.Bucket bucket : buckets) {
            String brandName = bucket.getKeyAsString();
            list.add(brandName);
        }
        return list;
    }

    private void buildAggregations(SearchRequest request) {
        request.source().aggregation(
                AggregationBuilders.terms("brandAgg").field("brand").size(100));
        request.source().aggregation(
                AggregationBuilders.terms("cityAgg").field("city").size(100));
        request.source().aggregation(
                AggregationBuilders.terms("starAgg").field("starName").size(100));
    }

    private void buildBasicQuery(RequestParams params, SearchRequest request) {
        // 1.准备Boolean查询
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        // 1.1.关键字搜索，match查询，放到must中
        String key = params.getKey();
        if (StringUtils.isNotBlank(key)) {
            // 不为空，根据关键字查询
            boolQuery.must(QueryBuilders.matchQuery("all", key));
        } else {
            // 为空，查询所有
            boolQuery.must(QueryBuilders.matchAllQuery());
        }

        // 1.2.品牌
        String brand = params.getBrand();
        if (StringUtils.isNotBlank(brand)) {
            boolQuery.filter(QueryBuilders.termQuery("brand", brand));
        }
        // 1.3.城市
        String city = params.getCity();
        if (StringUtils.isNotBlank(city)) {
            boolQuery.filter(QueryBuilders.termQuery("city", city));
        }
        // 1.4.星级
        String starName = params.getStarName();
        if (StringUtils.isNotBlank(starName)) {
            boolQuery.filter(QueryBuilders.termQuery("starName", starName));
        }
        // 1.5.价格范围
        Integer minPrice = params.getMinPrice();
        Integer maxPrice = params.getMaxPrice();
        if (minPrice != null && maxPrice != null) {
            maxPrice = maxPrice == 0 ? Integer.MAX_VALUE : maxPrice;
            boolQuery.filter(QueryBuilders.rangeQuery("price").gte(minPrice).lte(maxPrice));
        }

        // 2.算分函数查询
        FunctionScoreQueryBuilder functionScoreQuery = QueryBuilders.functionScoreQuery(
                boolQuery, // 原始查询，boolQuery
                new FunctionScoreQueryBuilder.FilterFunctionBuilder[]{ // function数组
                        new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                                QueryBuilders.termQuery("isAD", true), // 过滤条件
                                ScoreFunctionBuilders.weightFactorFunction(10) // 算分函数
                        )
                }
        );

        // 3.设置查询条件
        request.source().query(functionScoreQuery);
    }

    private PageResult handleResponse(SearchResponse response) {
        SearchHits searchHits = response.getHits();
        // 4.1.总条数
        long total = searchHits.getTotalHits().value;
        // 4.2.获取文档数组
        SearchHit[] hits = searchHits.getHits();
        // 4.3.遍历
        List<HotelDoc> hotels = new ArrayList<>(hits.length);
        for (SearchHit hit : hits) {
            // 4.4.获取source
            String json = hit.getSourceAsString();
            // 4.5.反序列化，非高亮的
            HotelDoc hotelDoc = JSON.parseObject(json, HotelDoc.class);
            // 4.6.处理高亮结果
            // 1)获取高亮map
            Map<String, HighlightField> map = hit.getHighlightFields();
            if (map != null && !map.isEmpty()) {
                // 2）根据字段名，获取高亮结果
                HighlightField highlightField = map.get("name");
                if (highlightField != null) {
                    // 3）获取高亮结果字符串数组中的第1个元素
                    String hName = highlightField.getFragments()[0].toString();
                    // 4）把高亮结果放到HotelDoc中
                    hotelDoc.setName(hName);
                }
            }
            // 4.8.排序信息
            Object[] sortValues = hit.getSortValues();
            if (sortValues.length > 0) {
                hotelDoc.setDistance(sortValues[0]);
            }

            // 4.9.放入集合
            hotels.add(hotelDoc);
        }
        return new PageResult(total, hotels);
    }
}
