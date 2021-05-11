package com.oyome.controller;

import com.oyome.pojo.bo.ShopcartBO;
import com.oyome.utils.CookieUtils;
import com.oyome.utils.IMOOCJSONResult;
import com.oyome.utils.JsonUtils;
import com.oyome.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Api(value = "购物车接口controller",tags = {"购物车接口相关api"} )
@RequestMapping("/shopcart")
@RestController
public class ShopCatController extends BaseController{
	final static Logger logger = LoggerFactory.getLogger(ShopCatController.class);

	@Autowired
	private RedisOperator redisOperator;

	@ApiOperation(value = "同步购物车到后端",notes = "同步购物车到后端",httpMethod = "POST")
	@RequestMapping("/add")
	public IMOOCJSONResult add(
			@RequestParam String userId,
			@RequestBody ShopcartBO shopcartBO,
			HttpServletRequest request,
			HttpServletResponse response
	){
		if(StringUtils.isBlank(userId)){
			return IMOOCJSONResult.errorMsg("");
		}
		System.out.println(shopcartBO);
		// 前端用户在登陆的情况下，添加商品到购物车 会同时在后端同步购物车到redis缓存
		// 需要判断当前购物车中包含已经存在的商品，如果存在则累加购买数量
		String shopCartJson = redisOperator.get(FOODIE_SHOPCART+":"+userId);
		List<ShopcartBO> list = new ArrayList<>();
		if(StringUtils.isNotBlank(shopCartJson)){
			//如果redis中已有购物车
			list = JsonUtils.jsonToList(shopCartJson,ShopcartBO.class);
		    //判断购物车中是否已经有商品，有的话count累加
			boolean ishaving = false;
			for(ShopcartBO bo : list){
				String tmpSpecId = bo.getSpecId();
				if(tmpSpecId.equals(shopcartBO.getSpecId())){
					bo.setBuyCounts(bo.getBuyCounts()+shopcartBO.getBuyCounts());
					ishaving = true;
				}

			}
			if(!ishaving){
				list.add(shopcartBO);
			}
		}else{
			//redis中没有购物车
			list = new ArrayList<>();
			//直接添加到购物车中
			list.add(shopcartBO);
		}
		redisOperator.set(FOODIE_SHOPCART+":"+userId,JsonUtils.objectToJson(list));

		return IMOOCJSONResult.ok();
	}

	@ApiOperation(value = "从购物车中删除商品",notes = "从购物车中删除商品",httpMethod = "POST")
	@RequestMapping("/del")
	public IMOOCJSONResult del(
			@RequestParam String userId,
			@RequestParam String itemSpecId,
			HttpServletRequest request,
			HttpServletResponse response
	){
		if(StringUtils.isBlank(userId)||StringUtils.isBlank(itemSpecId)){
			return IMOOCJSONResult.errorMsg("参数不能为空");
		}
		// 用户在页面删除购物车中的商品数据，如果此时用户已经登录，则需要同步删除redis购物车中的商品
		String shopCartJson = redisOperator.get(FOODIE_SHOPCART+":"+userId);
		List<ShopcartBO> list = new ArrayList<>();
		if(StringUtils.isNotBlank(shopCartJson)){
			//如果redis中已有购物车
			list = JsonUtils.jsonToList(shopCartJson,ShopcartBO.class);
			//判断购物车中是否已经有商品，有的话remove
			for(ShopcartBO bo : list){
				String tmpSpecId = bo.getSpecId();
				if(tmpSpecId.equals(itemSpecId)){
					list.remove(bo);
					break;
				}
			}

		}
		redisOperator.set(FOODIE_SHOPCART+":"+userId,JsonUtils.objectToJson(list));

		return IMOOCJSONResult.ok();
	}
}
