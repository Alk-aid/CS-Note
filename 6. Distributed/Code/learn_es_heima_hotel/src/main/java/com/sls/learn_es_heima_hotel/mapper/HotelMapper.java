package com.sls.learn_es_heima_hotel.mapper;

import com.sls.learn_es_heima_hotel.po.Hotel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HotelMapper {
    List<Hotel> findAllHotel();
}
