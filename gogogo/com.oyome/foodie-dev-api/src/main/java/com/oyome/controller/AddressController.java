package com.oyome.controller;

import com.oyome.pojo.UserAddress;
import com.oyome.pojo.bo.AddressBO;
import com.oyome.pojo.vo.ShopcartVO;
import com.oyome.service.AddressService;
import com.oyome.utils.IMOOCJSONResult;
import com.oyome.utils.MobileEmailUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@Api(value = "地址相关",tags = "地址相关的api接口")
@RequestMapping("/address")
@RestController
public class AddressController {
	final static Logger logger = LoggerFactory.getLogger(AddressController.class);
	@Autowired
	private AddressService addressService;
	/**
	 * 用户在确认订单页面，可以针对收获地址做如下操作：
	 * 1.查询用户的所有的收货地址列表
	 * 2.新增收获地址
	 * 3.删除收货地址
	 * 4.修改收获地址
	 * 5.切换默认收货地址
	 *
	 */

	@PostMapping("/list")
	@ApiOperation(value = "根据用户id查询用户收货地址列表",notes = "根据用户id查询用户收货地址列表",httpMethod = "POST")
	public IMOOCJSONResult refresh(
			@ApiParam(name = "userId",value = "用户id", required = true) @RequestParam String userId
	){
		if(StringUtils.isBlank(userId)){
			return IMOOCJSONResult.errorMsg("");
		}
		List<UserAddress> result = addressService.queryAll(userId);
		return IMOOCJSONResult.ok(result);
	}

	@PostMapping("/add")
	@ApiOperation(value = "用户新增地址",notes = "用户新增地址",httpMethod = "POST")
	public IMOOCJSONResult refresh(
			@ApiParam(name = "addressBO",value = "用户新增地址BO", required = true) @RequestBody AddressBO addressBO
			){

		IMOOCJSONResult checkRes = checkAddress(addressBO);
		if (checkRes.getStatus() != 200) {
			return checkRes;
		}

		addressService.addNewUserAddress(addressBO);

		return IMOOCJSONResult.ok();
	}

	@PostMapping("/update")
	@ApiOperation(value = "用户修改地址",notes = "用户修改地址",httpMethod = "POST")
	public IMOOCJSONResult update(
			@ApiParam(name = "addressBO",value = "用户修改地址BO", required = true) @RequestBody AddressBO addressBO
	){
		if(StringUtils.isBlank(addressBO.getAddressId())){
			return IMOOCJSONResult.errorMsg("修改地址错误:AddressId不能为空");
		}
		IMOOCJSONResult checkRes = checkAddress(addressBO);
		if (checkRes.getStatus() != 200) {
			return checkRes;
		}
		addressService.updateUserAddress(addressBO);
		return IMOOCJSONResult.ok();
	}

	@PostMapping("/delete")
	@ApiOperation(value = "用户删除地址",notes = "用户删除地址",httpMethod = "POST")
	public IMOOCJSONResult delete(
			@ApiParam(name = "userId",value = "用户id", required = true) @RequestParam String userId,
			@ApiParam(name = "addressId",value = "地址信息id", required = true) @RequestParam String addressId
	){
		if(StringUtils.isBlank(userId)){
			return IMOOCJSONResult.errorMsg("删除地址信息失败,用户id不能为空");
		}
		if(StringUtils.isBlank(addressId)){
			return IMOOCJSONResult.errorMsg("删除地址信息失败,地址id不能为空");
		}

		addressService.deleteUserAddress(userId,addressId);
		return IMOOCJSONResult.ok();
	}


	@PostMapping("/setDefalut")
	@ApiOperation(value = "用户设置默认地址",notes = "用户设置默认地址",httpMethod = "POST")
	public IMOOCJSONResult setDefalut(
			@ApiParam(name = "userId",value = "用户id", required = true) @RequestParam String userId,
			@ApiParam(name = "addressId",value = "地址信息id", required = true) @RequestParam String addressId
	){
		if(StringUtils.isBlank(userId)){
			return IMOOCJSONResult.errorMsg("设置默认地址信息失败,用户id不能为空");
		}
		if(StringUtils.isBlank(addressId)){
			return IMOOCJSONResult.errorMsg("设置默认地址信息失败,地址id不能为空");
		}

		addressService.setDefaultAddress(userId,addressId);
		return IMOOCJSONResult.ok();
	}

	private IMOOCJSONResult checkAddress(AddressBO addressBO) {
		String receiver = addressBO.getReceiver();
		if (StringUtils.isBlank(receiver)) {
			return IMOOCJSONResult.errorMsg("收货人不能为空");
		}
		if (receiver.length() > 12) {
			return IMOOCJSONResult.errorMsg("收货人姓名不能太长");
		}

		String mobile = addressBO.getMobile();
		if (StringUtils.isBlank(mobile)) {
			return IMOOCJSONResult.errorMsg("收货人手机号不能为空");
		}
		if (mobile.length() != 11) {
			return IMOOCJSONResult.errorMsg("收货人手机号长度不正确");
		}
		boolean isMobileOk = MobileEmailUtils.checkMobileIsOk(mobile);
		if (!isMobileOk) {
			return IMOOCJSONResult.errorMsg("收货人手机号格式不正确");
		}

		String province = addressBO.getProvince();
		String city = addressBO.getCity();
		String district = addressBO.getDistrict();
		String detail = addressBO.getDetail();
		if (StringUtils.isBlank(province) ||
				StringUtils.isBlank(city) ||
				StringUtils.isBlank(district) ||
				StringUtils.isBlank(detail)) {
			return IMOOCJSONResult.errorMsg("收货地址信息不能为空");
		}

		return IMOOCJSONResult.ok();
	}

}
