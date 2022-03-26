package cn.itcast.hotel;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

@SpringBootTest
class HotelAggregationTest {

    private RestHighLevelClient client;

    @Test
    void testAgg() throws IOException {
        // 1.准备请求
        SearchRequest request = new SearchRequest("hotel");
        // 2.请求参数
        // 2.1.size
        request.source().size(0);
        // 2.2.聚合
        request.source().aggregation(
                AggregationBuilders.terms("brandAgg").field("brand").size(20));
        // 3.发出请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 4.解析结果
        Aggregations aggregations = response.getAggregations();
        // 4.1.根据聚合名称，获取聚合结果
        Terms brandAgg = aggregations.get("brandAgg");
        // 4.2.获取buckets
        List<? extends Terms.Bucket> buckets = brandAgg.getBuckets();
        // 4.3.遍历
        for (Terms.Bucket bucket : buckets) {
            String brandName = bucket.getKeyAsString();
            System.out.println("brandName = " + brandName);
            long docCount = bucket.getDocCount();
            System.out.println("docCount = " + docCount);
        }
    }

    @Test
    void testSuggest() throws IOException {
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
                                .prefix("s")
                ));
        // 3.发出请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 4.解析
        Suggest suggest = response.getSuggest();
        // 4.1.根据名称获取结果
        CompletionSuggestion suggestion = suggest.getSuggestion("hotelSuggest");
        // 4.2.获取options
        for (CompletionSuggestion.Entry.Option option : suggestion.getOptions()) {
            // 4.3.获取补全的结果
            String str = option.getText().toString();
            System.out.println(str);
        }
    }

    @BeforeEach
    void setUp() {
        client = new RestHighLevelClient(RestClient.builder(
                HttpHost.create("http://192.168.150.101:9200")
        ));
    }

    @AfterEach
    void tearDown() throws IOException {
        client.close();
    }



}
