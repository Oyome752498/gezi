package com.oyome.controller;

import com.oyome.enums.OrderStatusEnum;
import com.oyome.enums.PayMethod;
import com.oyome.pojo.OrderStatus;
import com.oyome.pojo.UserAddress;
import com.oyome.pojo.bo.AddressBO;
import com.oyome.pojo.bo.ShopcartBO;
import com.oyome.pojo.bo.SubmitOrderBO;
import com.oyome.pojo.vo.MerchantOrdersVO;
import com.oyome.pojo.vo.OrderVO;
import com.oyome.service.AddressService;
import com.oyome.service.OrderService;
import com.oyome.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Api(value = "订单相关",tags = "订单相关的api接口")
@RequestMapping("/orders")
@RestController
public class OrdersController extends BaseController{
	final static Logger logger = LoggerFactory.getLogger(OrdersController.class);
    @Autowired
    private OrderService orderService;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private RedisOperator redisOperator;

	@PostMapping("/create")
	@ApiOperation(value = "用户下单",notes = "用户下单",httpMethod = "POST")
	public IMOOCJSONResult refresh(
			@ApiParam(name = "orderBO",value = "用于创建订单的VO对象", required = true)
			@RequestBody SubmitOrderBO orderBO,
			HttpServletRequest request,
			HttpServletResponse response
			){
	    if(orderBO.getPayMethod() != PayMethod.WEIXIN.type
        && orderBO.getPayMethod() != PayMethod.ALIPAY.type){
            return IMOOCJSONResult.errorMsg("支付方式不支持");
        }
//		System.out.println(orderBO);


		String shopCartJson = redisOperator.get(FOODIE_SHOPCART+":"+orderBO.getUserId());
		List<ShopcartBO> list = new ArrayList<>();
		if(StringUtils.isBlank(shopCartJson)){
			return IMOOCJSONResult.errorMsg("购物车数据不争取");
		}
		list = JsonUtils.jsonToList(shopCartJson,ShopcartBO.class);

		//1.创建订单
       OrderVO orderVO =  orderService.createOrder(orderBO,list);

       String orderId = orderVO.getOrderId();

		//2.创建订单成功后，移除购物车中已结算的商品
		/**
		 * 1001
		 * 2002 ->用户购买
		 * 3003 ->用户购买
		 * 4004
		 */
		Cookie[] cookies = request.getCookies();
		//清理覆盖现有的redis中的缓存
		list.removeAll(orderVO.getToBeRemovedShopCartBo());
		redisOperator.set(FOODIE_SHOPCART+":"+orderBO.getUserId(),JsonUtils.objectToJson(list));
		// 整合redis后 完善购物车的已结算商品清除，并且同步到前端cookies
		CookieUtils.setCookie(request,response,FOODIE_SHOPCART,JsonUtils.objectToJson(list),true);



		//3.向支付中心发送当前订单，用于保存支付中心的订单数据
		MerchantOrdersVO merchantOrdersVO = orderVO.getMerchantOrdersVO();
		merchantOrdersVO.setReturnUrl(payReturnUrl);
		//为了方便测试购买，所以所有支付金额都设置为0.01元
		merchantOrdersVO.setAmount(1);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("imoocUserId","6196344-496174574");
		headers.add("password","489y-65i9-0yi5-3yh5");
		HttpEntity<MerchantOrdersVO>entity = new HttpEntity<>(merchantOrdersVO,headers);
		ResponseEntity<IMOOCJSONResult> resultResponseEntity =
				restTemplate.postForEntity(
						paymentUrl,
						entity,
						IMOOCJSONResult.class);
		IMOOCJSONResult paymentResult = resultResponseEntity.getBody();
		if(paymentResult.getStatus() != 200){
			return IMOOCJSONResult.errorMsg("支付中心订单创建失败，请联系管理员");
		}
		return IMOOCJSONResult.ok(orderId);
	}
	@PostMapping("/notifyMerchantOrderPaid")
	public int notifyMerchantOrderPaid(String merchantOrderId){
		orderService.updateOrderStatus(merchantOrderId, OrderStatusEnum.WAIT_DELIVER.type);
		return HttpStatus.OK.value();
	}
	@PostMapping("/getPaidOrderInfo")
	public IMOOCJSONResult getPaidOrderInfo(String orderId){
		OrderStatus status = orderService.queryOrderStatusInfo(orderId);
		return IMOOCJSONResult.ok(status);
	}
}
