package com.sls.learn_es_heima_hotel.esTest;

import com.alibaba.fastjson.JSON;
import com.sls.learn_es_heima_hotel.constants.HotelIndexConstant;
import com.sls.learn_es_heima_hotel.po.HotelDoc;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
public class ElasticSerachIndexTest {

    @Autowired
    private RestHighLevelClient client;

    @Test
    public void testEs() throws IOException {
        GetRequest request = new GetRequest("hotel", "38812");
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        String json = response.getSourceAsString();
        HotelDoc hotelDoc = JSON.parseObject(json, HotelDoc.class);
        System.out.println(hotelDoc);
    }

    //创建索引库
    @Test
    public void createHotelIndex() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest("hotel");
        request.source(HotelIndexConstant.MAPPING_TEMPLATE, XContentType.JSON);
        client.indices().create(request, RequestOptions.DEFAULT);
    }
    //判断对应索引库是否存字
    @Test
    public void ExistHotelIndexTest() throws IOException {
        // 1.准备Request
        org.elasticsearch.client.indices.GetIndexRequest request = new GetIndexRequest("hotel");
        // 3.发送请求
        boolean isExists = client.indices().exists(request, RequestOptions.DEFAULT);

        System.out.println(isExists ? "存在" : "不存在");
    }
    //删除对应索引库
    @Test
    void testDeleteIndex() throws IOException {
        // 1.准备Request
        DeleteIndexRequest request = new DeleteIndexRequest("hotel");
        // 3.发送请求
        client.indices().delete(request, RequestOptions.DEFAULT);
    }


}
