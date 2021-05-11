package com.oyome.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.oyome.enums.CommentLevel;
import com.oyome.enums.YesOrNo;
import com.oyome.mapper.*;
import com.oyome.pojo.*;
import com.oyome.pojo.vo.CommentLevelCountsVO;
import com.oyome.pojo.vo.ItemCommentVO;
import com.oyome.pojo.vo.SearchItemsVO;
import com.oyome.pojo.vo.ShopcartVO;
import com.oyome.service.ItemService;
import com.oyome.utils.DesensitizationUtil;
import com.oyome.utils.PagedGridResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemsMapper itemsMapper;
    @Autowired
    private ItemsImgMapper itemsImgMapper;
    @Autowired
    private ItemsSpecMapper itemsSpecMapper;
    @Autowired
    private ItemsParamMapper itemsParamMapper;
    @Autowired
    private ItemsCommentsMapper itemsCommentsMapper;
    @Autowired
    private ItemsMapperCustom itemsMapperCustom;


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Items queryItemsById(String itemId) {
        return itemsMapper.selectByPrimaryKey(itemId);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ItemsImg> queryItemImgList(String itemId) {
        Example example = new Example(ItemsImg.class);
        Example.Criteria itemsImgCriteria = example.createCriteria();
        itemsImgCriteria.andEqualTo("itemId",itemId);
        return itemsImgMapper.selectByExample(example);
    }
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ItemsSpec> queryItemSpecList(String itemId) {
        Example example = new Example(ItemsSpec.class);
        Example.Criteria itemsSpecCriteria = example.createCriteria();
        itemsSpecCriteria.andEqualTo("itemId",itemId);
        return itemsSpecMapper.selectByExample(example);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public ItemsParam queryItemParam(String itemId) {
        Example example = new Example(ItemsParam.class);
        Example.Criteria itemsParamCriteria = example.createCriteria();
        itemsParamCriteria.andEqualTo("itemId",itemId);
        return itemsParamMapper.selectOneByExample(example);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public CommentLevelCountsVO queryCommentCounts(String itemId) {
          Integer goodCounts = getCommentCounts(itemId, CommentLevel.GOOD.type);
          Integer normalCounts = getCommentCounts(itemId, CommentLevel.NORMAL.type);
          Integer badCounts = getCommentCounts(itemId, CommentLevel.BAD.type);
          Integer totalCounts = goodCounts+normalCounts+badCounts;

          CommentLevelCountsVO countsVO = new CommentLevelCountsVO();
          countsVO.setBadCounts(badCounts);
          countsVO.setNormalCounts(normalCounts);
          countsVO.setGoodCounts(goodCounts);
          countsVO.setTotalCounts(totalCounts);
        return countsVO;
    }



    @Transactional(propagation = Propagation.SUPPORTS)
    Integer getCommentCounts(String itemId,Integer level){
        ItemsComments condition = new ItemsComments();
        condition.setItemId(itemId);
        if(level != null){
            condition.setCommentLevel(level);
        }
        return itemsCommentsMapper.selectCount(condition);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult queryPagedComments(String itemId,
                                                  Integer level,
                                                  Integer page,
                                                  Integer pageSize) {
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("itemId",itemId);
        map.put("level",level);

        PageHelper.startPage(page,pageSize);
        List<ItemCommentVO> list = itemsMapperCustom.queryItemComments(map);
        for(ItemCommentVO commentVO : list){
            commentVO.setNickname(DesensitizationUtil.commonDisplay(commentVO.getNickname()));

        }

        return  setterPagedGrid(list,page);

    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult searchItems(String keywords, String sort, Integer page, Integer pageSize) {
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("keywords",keywords);
        map.put("sort",sort);

        PageHelper.startPage(page,pageSize);
        List<SearchItemsVO> list = itemsMapperCustom.searchItems(map);
//        for(ItemCommentVO commentVO : list){
//            commentVO.setNickname(DesensitizationUtil.commonDisplay(commentVO.getNickname()));
//
//        }

        return  setterPagedGrid(list,page);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult searchItemsByThirdCat(Integer catId, String sort, Integer page, Integer pageSize) { Map<String,Object> map = new HashMap<String,Object>();
        map.put("catId",catId);
        map.put("sort",sort);

        PageHelper.startPage(page,pageSize);
        List<SearchItemsVO> list = itemsMapperCustom.searchItemsByThirdCat(map);

        return setterPagedGrid(list,page);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ShopcartVO> queryItemsBySpecIds(String specIds) {
        String ids[] = specIds.split(",");
        List<String> specIdsList = new ArrayList<>();
        Collections.addAll(specIdsList,ids);
       return  itemsMapperCustom.queryItemsBySpecIds(specIdsList);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public ItemsSpec queryItemSpecById(String specId) {
        return itemsSpecMapper.selectByPrimaryKey(specId);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public String queryItemMainImgById(String itemId) {
        ItemsImg itemsImg = new ItemsImg();
        itemsImg.setItemId(itemId);
        itemsImg.setIsMain(YesOrNo.YES.type);
        ItemsImg result = itemsImgMapper.selectOne(itemsImg);
        return result == null ? "" : result.getUrl();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public  void decreaseItemSpecStock(String specId, int buyCounts) {

        //synchronized 不推荐使用 集群下无用，性能低下
        //锁数据库 不推荐 导致数据库性能低下
        //分布式锁 zookeeper redis
//        ItemsSpec itemsSpec = itemsSpecMapper.selectByPrimaryKey(specId);
//        itemsSpec.setStock(itemsSpec.getStock() - buyCounts);
//        itemsSpecMapper.updateByPrimaryKeySelective(itemsSpec);
        int result = itemsMapperCustom.decreaseItemSpecStock(specId,buyCounts);
        if(result != 1){
            throw new RuntimeException("订单创建失败原因：库存不足");
        }
    }

    private PagedGridResult setterPagedGrid(List<?> list,Integer page){
        PageInfo<?> pageList = new PageInfo<>(list);
        PagedGridResult grid = new PagedGridResult();
        grid.setPage(page);
        grid.setRows(list);
        grid.setTotal(pageList.getPages());
        grid.setRecords(pageList.getTotal());
        return grid;
    }


}
