package com.oyome.service;

import com.oyome.pojo.*;
import com.oyome.pojo.vo.CommentLevelCountsVO;
import com.oyome.pojo.vo.ItemCommentVO;
import com.oyome.pojo.vo.ShopcartVO;
import com.oyome.utils.PagedGridResult;

import java.util.List;

public interface ItemService {
    /**
     * 根据商品id查询详情
     * @param itemId
     * @return
     */
    public Items queryItemsById(String itemId);

    /**
     * 根据商品id查询相应的图片列表
     * @param itemId
     * @return
     */
    public List<ItemsImg> queryItemImgList(String itemId);
    /**
     * 根据商品id查询相应的商品规格列表
     * @param itemId
     * @return
     */
    public List<ItemsSpec> queryItemSpecList(String itemId);

    /**
     * 根据商品id查询商品属性
     * @param itemId
     * @return
     */
    public ItemsParam queryItemParam(String itemId);
    /**
     * 根据商品id查询商品评价等级数量
     * @param itemId
     * @return
     */
    public CommentLevelCountsVO queryCommentCounts(String itemId);
    /**
     * 根据商品id查询商品评价(分页)
     * @param itemId
     * @return
     */
    public PagedGridResult queryPagedComments(String itemId, Integer level, Integer page, Integer pageSize);

    /**
     * 搜索商品列表
     * @param keywords
     * @return
     */
    public PagedGridResult searchItems(String keywords, String sort, Integer page, Integer pageSize);

    /**
     * 根据分类id搜索商品列表
     * @param catId
     * @return
     */
    public PagedGridResult searchItemsByThirdCat(Integer catId, String sort, Integer page, Integer pageSize);

    /**
     * 根据分根据拼接的ids查询最新的购物车中的商品数据（用于刷新渲染购物车的数据）
     * @param specIds
     * @return
     */
    public List<ShopcartVO> queryItemsBySpecIds(String specIds);

    /**
     * 根据商品规格id获取规格对象的具体信息
     * @param specId
     * @return
     */
    public ItemsSpec queryItemSpecById(String specId);

    /**
     * 根据商品id，查询商品的主图
     * @param itemId
     * @return
     */
    public String queryItemMainImgById(String itemId);

    /**
     * 减去库存
     * @param specId
     * @param buyCounts
     */
    public void decreaseItemSpecStock(String specId,int buyCounts);
}
