package com.sls.learn_es_heima_hotel.vo;

import com.sls.learn_es_heima_hotel.po.HotelDoc;
import lombok.Data;

import java.util.List;

@Data
public class PageResult {
    private Long total;
    private List<HotelDoc> hotels;

    public PageResult() {
    }

    public PageResult(Long total, List<HotelDoc> hotels) {
        this.total = total;
        this.hotels = hotels;
    }
}
