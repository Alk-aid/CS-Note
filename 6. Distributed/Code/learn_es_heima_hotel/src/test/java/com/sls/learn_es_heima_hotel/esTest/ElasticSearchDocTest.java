package com.sls.learn_es_heima_hotel.esTest;

import com.alibaba.fastjson.JSON;
import com.sls.learn_es_heima_hotel.mapper.HotelMapper;
import com.sls.learn_es_heima_hotel.po.Hotel;
import com.sls.learn_es_heima_hotel.po.HotelDoc;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

@SpringBootTest
public class ElasticSearchDocTest {
    @Autowired
    private RestHighLevelClient client;
    @Autowired
    private HotelMapper hotelMapper;

    @Test
    void BulkAddDocument() throws IOException {
        List<Hotel> hotels = hotelMapper.findAllHotel();

        BulkRequest bulkRequest = new BulkRequest();

        for (Hotel hotel : hotels) {
            HotelDoc hotelDoc = new HotelDoc(hotel);
            String json = JSON.toJSONString(hotelDoc);
            bulkRequest.add(new IndexRequest("hotel")
                    .id(hotelDoc.getId().toString())
                    .source(json, XContentType.JSON));
        }
        client.bulk(bulkRequest, RequestOptions.DEFAULT);

    }
}
