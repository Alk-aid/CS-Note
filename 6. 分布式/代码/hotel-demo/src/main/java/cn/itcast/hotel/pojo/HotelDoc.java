package cn.itcast.hotel.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
public class HotelDoc {
    private Long id;
    private String name;
    private String address;
    private Integer price;
    private Integer score;
    private String brand;
    private String city;
    private String starName;
    private String business;
    private String location;
    private String pic;
    private Object distance;
    private Boolean isAD;
    private List<String> suggestion;

    public HotelDoc(Hotel hotel) {
        this.id = hotel.getId();
        this.name = hotel.getName();
        this.address = hotel.getAddress();
        this.price = hotel.getPrice();
        this.score = hotel.getScore();
        this.brand = hotel.getBrand();
        this.city = hotel.getCity();
        this.starName = hotel.getStarName();
        this.business = hotel.getBusiness();
        this.location = hotel.getLatitude() + ", " + hotel.getLongitude();
        this.pic = hotel.getPic();
        // 自动补全字段的处理
        this.suggestion = new ArrayList<>();
        // 添加品牌、城市
        this.suggestion.add(this.brand);
        this.suggestion.add(this.city);
        // 判断商圈是否包含/
        if (this.business.contains("/")) {
            // 需要切割
            String[] arr = this.business.split("/");
            Collections.addAll(this.suggestion, arr);
        }else{
            this.suggestion.add(this.business);
        }

    }
}
