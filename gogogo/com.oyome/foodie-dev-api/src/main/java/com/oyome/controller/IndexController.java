package com.oyome.controller;

import com.oyome.enums.YesOrNo;
import com.oyome.pojo.Carousel;
import com.oyome.pojo.Category;
import com.oyome.pojo.vo.CategoryVO;
import com.oyome.pojo.vo.NewItemsVO;
import com.oyome.service.CarouselService;
import com.oyome.service.CategoryService;
import com.oyome.utils.IMOOCJSONResult;
import com.oyome.utils.JsonUtils;
import com.oyome.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.List;

@Api(value = "首页", tags = {"首页展示的相关接口"})
@RequestMapping("index")
@RestController
public class IndexController {
	@Autowired
	private CarouselService carouselService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private RedisOperator redisOperator;

	@GetMapping("/carousel")
	@ApiOperation(value = "获取首页轮播图列表",notes = "获取首页轮播图列表",httpMethod = "GET")
	public IMOOCJSONResult carousel(){
		String carousel = redisOperator.get("carousel");
		List<Carousel> list = null;
		if(StringUtils.isBlank(carousel)){
			 list = carouselService.queryAll(YesOrNo.YES.type);
			redisOperator.set("carousel", JsonUtils.objectToJson(list));
		}else{
			 list = JsonUtils.jsonToList(carousel,Carousel.class);
		}


		return  IMOOCJSONResult.ok(list);
	}

	/**
	 * 首页分类展示需求
	 * 1.第一次刷新主页查询大分类，渲染展示到首页
	 * 2.如果鼠标移到大分类，则加载其子分类的内容，如果已经存在子分类，则不需要加载（懒加载）
	 */
	@GetMapping("/cats")
	@ApiOperation(value = "获取商品分类（一级分类）",notes = "获取商品分类（一级分类）",httpMethod = "GET")
	public IMOOCJSONResult cats(){
		String category = redisOperator.get("category");
		List<Category> list = new ArrayList<>();
		if(StringUtils.isBlank(category)){
			 list =  categoryService.queryAllRootLevelCat();
			 redisOperator.set("category",JsonUtils.objectToJson(list));
		}else{
			 list = JsonUtils.jsonToList(category,Category.class);
		}

		return  IMOOCJSONResult.ok(list);
	}


	@GetMapping("/subCat/{rootCatId}")
	@ApiOperation(value = "获取商品子分类",notes = "获取商品子分类",httpMethod = "GET")
	public IMOOCJSONResult subcats(
			@ApiParam(name = "rootCatId",value = "一级分类id", required = true) @PathVariable Integer rootCatId){
		if(rootCatId == null){
			return IMOOCJSONResult.errorMsg("分类不存在");
		}
		List<CategoryVO> result = new ArrayList<>();
		String root_id = redisOperator.get("subCat:"+String.valueOf(rootCatId));
	   	if(StringUtils.isBlank(root_id)){
			result = categoryService.getSubCatList(rootCatId);
			redisOperator.set("subCat:"+String.valueOf(rootCatId),JsonUtils.objectToJson(result));
		}else{
			result = JsonUtils.jsonToList(root_id,CategoryVO.class);
		}
		return IMOOCJSONResult.ok(result);
	}

	@GetMapping("/sixNewItems/{rootCatId}")
	@ApiOperation(value = "查询每个一级分类下的6个最新的商品数据",notes = "查询每个一级分类下的6个最新的商品数据",httpMethod = "GET")
	public IMOOCJSONResult sixNewItems(
			@ApiParam(name = "rootCatId",value = "一级分类id", required = true) @PathVariable Integer rootCatId){
		if(rootCatId == null){
			return IMOOCJSONResult.errorMsg("分类不存在");
		}
		List<NewItemsVO> result = categoryService.getSixNewItemsLazy(rootCatId);
		return IMOOCJSONResult.ok(result);
	}
}
