package com.oyome.controller;

import com.oyome.pojo.Orders;
import com.oyome.service.center.MyOrdersService;
import com.oyome.utils.IMOOCJSONResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.io.File;

@Controller
public class BaseController {
	@Autowired
	public MyOrdersService myOrdersService;
	public static final Integer COMMENT_PAGE_SIZE = 10;
	public static final Integer PAGE_SIZE = 10;
	public static final String FOODIE_SHOPCART = "shopcart";
	// 支付中心的调用地址
	String paymentUrl = "http://payment.t.mukewang.com/foodie-payment/payment/createMerchantOrder";
	//微信支付成功 -> 支付中心 -> 天天吃货
    // 						 -> 回调通知的url
	String payReturnUrl = "http://localhost:8088/foodie-dev-api/orders/notifyMerchantOrderPaid";
//	String payReturnUrl = "http://www.oyome.cn:8088/foodie-dev-api/orders/notifyMerchantOrderPaid";
	// 用户头像上传位置
	public static final String IAMGE_URL_FACE_LOCATION = "E:" +File.separator +
			"images" +File.separator +
			"foodie" +File.separator +
			"face";

	/**
	 * 用于验证用户和订单是否有关联，避免非法用户调用
	 * @param userId
	 * @param orderId
	 * @return
	 */
	public IMOOCJSONResult checkUserOrder(String userId, String orderId){
		Orders order = myOrdersService.queryMyOrder(orderId,userId);
		if(order == null){
			return IMOOCJSONResult.errorMsg("订单不存在");
		}
		return IMOOCJSONResult.ok(order);

	}
}
