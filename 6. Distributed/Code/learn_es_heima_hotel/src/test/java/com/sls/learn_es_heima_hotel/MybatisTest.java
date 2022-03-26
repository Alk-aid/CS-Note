package com.sls.learn_es_heima_hotel;

import com.sls.learn_es_heima_hotel.mapper.HotelMapper;
import com.sls.learn_es_heima_hotel.po.Hotel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class MybatisTest {
    @Autowired
    private HotelMapper hotelMapper;

    /**
     * 检查mybatis是否正确配置成功，通过调用findAllHotel来检查
     */
    @Test
    public void TestBasic(){
        List<Hotel> allHotel = hotelMapper.findAllHotel();
        for (Hotel hotel : allHotel) {
            System.out.println(hotel);
        }
    }
}
