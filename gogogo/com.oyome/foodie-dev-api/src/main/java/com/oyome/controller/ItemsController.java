package com.oyome.controller;

import com.oyome.enums.YesOrNo;
import com.oyome.pojo.*;
import com.oyome.pojo.vo.*;
import com.oyome.service.CarouselService;
import com.oyome.service.CategoryService;
import com.oyome.service.ItemService;
import com.oyome.utils.IMOOCJSONResult;
import com.oyome.utils.PagedGridResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "商品接口", tags = {"商品信息展示的相关接口"})
@RequestMapping("items")
@RestController
public class ItemsController extends BaseController{
	@Autowired
	private ItemService itemService;



	@GetMapping("/info/{itemId}")
	@ApiOperation(value = "查询商品详情",notes = "查询商品详情",httpMethod = "GET")
	public IMOOCJSONResult sixNewItems(
			@ApiParam(name = "itemId",value = "商品id", required = true) @PathVariable String itemId){
		if(StringUtils.isBlank(itemId)){
			return IMOOCJSONResult.errorMsg("null");
		}
		Items item = itemService.queryItemsById(itemId);
		List<ItemsImg>itemsImgList =  itemService.queryItemImgList(itemId);
		List<ItemsSpec>itemsSpecList = itemService.queryItemSpecList(itemId);
		ItemsParam itemsParam = itemService.queryItemParam(itemId);

		ItemInfoVO itemInfoVO = new ItemInfoVO();
		itemInfoVO.setItem(item);
		itemInfoVO.setItemImgList(itemsImgList);
		itemInfoVO.setItemSpecList(itemsSpecList);
		itemInfoVO.setItemParams(itemsParam);
		return IMOOCJSONResult.ok(itemInfoVO);
	}
	@GetMapping("/commentLevel")
	@ApiOperation(value = "查询商品评价等级",notes = "查询商品评价等级",httpMethod = "GET")
	public IMOOCJSONResult commentLevel(
			@ApiParam(name = "itemId",value = "商品id", required = true) @RequestParam String itemId){
		if(StringUtils.isBlank(itemId)){
			return IMOOCJSONResult.errorMsg("null");
		}
		CommentLevelCountsVO commentLevelCountsVO = itemService.queryCommentCounts(itemId);
		return IMOOCJSONResult.ok(commentLevelCountsVO);
	}
	@GetMapping("/comments")
	@ApiOperation(value = "查询商品评论",notes = "查询商品评论",httpMethod = "GET")
	public IMOOCJSONResult comments(
			@ApiParam(name = "itemId",value = "商品id", required = true) @RequestParam String itemId,
			@ApiParam(name = "level",value = "评价等级", required = false) @RequestParam Integer level,
			@ApiParam(name = "page",value = "查询下一页的第几页", required = false) @RequestParam Integer page,
			@ApiParam(name = "pageSize",value = "分页的每一页显示的记录数", required = false) @RequestParam Integer pageSize){
		if(StringUtils.isBlank(itemId)){
			return IMOOCJSONResult.errorMsg("null");
		}
		if(page == null){
			page = 1;
		}
		if(pageSize == null){
			pageSize = COMMENT_PAGE_SIZE;
		}
		PagedGridResult result = itemService.queryPagedComments(itemId,level,page,pageSize);
		return IMOOCJSONResult.ok(result);
	}
	@GetMapping("/search")
	@ApiOperation(value = "搜索商品列表",notes = "搜索商品列表",httpMethod = "GET")
	public IMOOCJSONResult search(
			@ApiParam(name = "keywords",value = "关键字", required = true) @RequestParam String keywords,
			@ApiParam(name = "sort",value = "排序", required = false) @RequestParam String sort,
			@ApiParam(name = "page",value = "查询下一页的第几页", required = false) @RequestParam Integer page,
			@ApiParam(name = "pageSize",value = "分页的每一页显示的记录数", required = false) @RequestParam Integer pageSize){
		if(StringUtils.isBlank(keywords)){
			return IMOOCJSONResult.errorMsg("null");
		}
		if(page == null){
			page = 1;
		}
		if(pageSize == null){
			pageSize = PAGE_SIZE;
		}
		PagedGridResult result = itemService.searchItems(keywords,sort,page,pageSize);
		return IMOOCJSONResult.ok(result);
	}
	@GetMapping("/catItems")
	@ApiOperation(value = "通过分类id搜索商品列表",notes = "通过分类id搜索商品列表",httpMethod = "GET")
	public IMOOCJSONResult catItems(
			@ApiParam(name = "catId",value = "分类id", required = true) @RequestParam Integer catId,
			@ApiParam(name = "sort",value = "排序", required = false) @RequestParam String sort,
			@ApiParam(name = "page",value = "查询下一页的第几页", required = false) @RequestParam Integer page,
			@ApiParam(name = "pageSize",value = "分页的每一页显示的记录数", required = false) @RequestParam Integer pageSize){
		if(catId == null){
			return IMOOCJSONResult.errorMsg("null");
		}
		if(page == null){
			page = 1;
		}
		if(pageSize == null){
			pageSize = PAGE_SIZE;
		}
		PagedGridResult result = itemService.searchItemsByThirdCat(catId,sort,page,pageSize);
		return IMOOCJSONResult.ok(result);
	}
	//用于用户长时间未登录，刷新购物车中的数据（主要是商品价格）类似JD taobao
	@GetMapping("/refresh")
	@ApiOperation(value = "根据商品规格id查找最新的商品数据",notes = "根据商品规格id查找最新的商品数据",httpMethod = "GET")
	public IMOOCJSONResult refresh(
			@ApiParam(name = "itemSpecIds",value = "拼接的商品规格ids", required = true, example = "1001,1003,1005") @RequestParam String itemSpecIds
			){
		if(StringUtils.isBlank(itemSpecIds)){
			return IMOOCJSONResult.errorMsg("null");
		}

		List<ShopcartVO> result = itemService.queryItemsBySpecIds(itemSpecIds);
		return IMOOCJSONResult.ok(result);
	}
}
