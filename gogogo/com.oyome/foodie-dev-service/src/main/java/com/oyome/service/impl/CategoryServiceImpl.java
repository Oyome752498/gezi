package com.oyome.service.impl;

import com.oyome.mapper.CarouselMapper;
import com.oyome.mapper.CategoryMapper;
import com.oyome.mapper.CategoryMapperCustom;
import com.oyome.pojo.Carousel;
import com.oyome.pojo.Category;
import com.oyome.pojo.vo.CategoryVO;
import com.oyome.pojo.vo.NewItemsVO;
import com.oyome.service.CarouselService;
import com.oyome.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CategoryMapperCustom categoryMapperCustom;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Category> queryAllRootLevelCat() {
        Example example = new Example(Category.class);
        Example.Criteria categoryCriteria = example.createCriteria();
        categoryCriteria.andEqualTo("type",1);
        List<Category> result = categoryMapper.selectByExample(example);
        return result;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<CategoryVO> getSubCatList(Integer rootCatId) {
        return categoryMapperCustom.getSubCatList(rootCatId);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<NewItemsVO> getSixNewItemsLazy(Integer rootCatId) {
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("rootCatId",rootCatId);

        return categoryMapperCustom.getSixNewItemsLazy(map);
    }


}
