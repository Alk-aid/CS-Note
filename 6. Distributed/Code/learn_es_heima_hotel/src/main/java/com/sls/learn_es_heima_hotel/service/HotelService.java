package com.sls.learn_es_heima_hotel.service;

import com.sls.learn_es_heima_hotel.vo.PageResult;
import com.sls.learn_es_heima_hotel.vo.RequestParams;

public interface HotelService {
    PageResult search(RequestParams params);
}
