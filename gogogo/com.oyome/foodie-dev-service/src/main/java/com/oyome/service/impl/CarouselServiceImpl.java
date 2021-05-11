package com.oyome.service.impl;

import com.oyome.mapper.CarouselMapper;
import com.oyome.pojo.Carousel;
import com.oyome.service.CarouselService;
import com.oyome.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class CarouselServiceImpl implements CarouselService {

    @Autowired
    private CarouselMapper carouselMapper;


    @Override
    public List<Carousel> queryAll(Integer isShow) {
        Example example = new Example(Carousel.class);
        example.orderBy("sort").desc();
        Example.Criteria carouselCriteria = example.createCriteria();
        carouselCriteria.andEqualTo("isShow",isShow);

        List<Carousel> carouselList =carouselMapper.selectByExample(example);
        return carouselList;
    }
}
