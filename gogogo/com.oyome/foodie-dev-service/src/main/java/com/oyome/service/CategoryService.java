package com.oyome.service;

import com.oyome.pojo.Carousel;
import com.oyome.pojo.Category;
import com.oyome.pojo.vo.CategoryVO;
import com.oyome.pojo.vo.NewItemsVO;

import java.util.List;

public interface CategoryService {
    /**
     * 查询所有大分类
     *
     * * @return
     */
    public List<Category> queryAllRootLevelCat();

    /**
     * 根据一级分类id查询子分类信息
     * @param rootCatId
     * @return
     */
    public List<CategoryVO> getSubCatList(Integer rootCatId);

    /**
     * 查询首页每个一级分类下的最新的商品数据
     * @param rootCatId
     * @return
     */
    public List<NewItemsVO> getSixNewItemsLazy(Integer rootCatId);

}
