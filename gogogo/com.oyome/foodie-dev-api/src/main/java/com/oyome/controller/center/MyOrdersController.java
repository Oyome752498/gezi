package com.oyome.controller.center;

import com.oyome.controller.BaseController;
import com.oyome.pojo.Orders;
import com.oyome.pojo.Users;
import com.oyome.pojo.bo.center.CenterUserBO;
import com.oyome.pojo.vo.OrderStatusCountsVO;
import com.oyome.resource.FileUpload;
import com.oyome.service.center.CenterUserService;
import com.oyome.service.center.MyOrdersService;
import com.oyome.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "用户订单信息接口",tags = {"用户相关订单接口"})
@RestController
@RequestMapping("/myorders")
public class MyOrdersController extends BaseController {
    @Autowired
    private MyOrdersService myOrdersService;
    @PostMapping("/query")
    @ApiOperation(value = "查询订单列表",notes = "查询订单列表",httpMethod = "POST")
    public IMOOCJSONResult query(
            @ApiParam(name = "userId",value = "用户id", required = true) @RequestParam String userId,
            @ApiParam(name = "orderStatus",value = "订单状态", required = false) @RequestParam Integer orderStatus,
            @ApiParam(name = "page",value = "查询下一页的第几页", required = false) @RequestParam Integer page,
            @ApiParam(name = "pageSize",value = "分页的每一页显示的记录数", required = false) @RequestParam Integer pageSize){
        if(StringUtils.isBlank(userId)){
            return IMOOCJSONResult.errorMsg("null");
        }
        if(page == null){
            page = 1;
        }
        if(pageSize == null){
            pageSize = COMMENT_PAGE_SIZE;
        }
        PagedGridResult result = myOrdersService.queryMyOrders(userId,orderStatus,page,pageSize);
        return IMOOCJSONResult.ok(result);
    }

    @GetMapping("/deliver")
    @ApiOperation(value = "商家发货",notes = "商家发货",httpMethod = "GET")
    public IMOOCJSONResult query(
            @ApiParam(name = "orderId",value = "订单Id", required = true) @RequestParam String orderId
            ){
        if(StringUtils.isBlank(orderId)){
            return IMOOCJSONResult.errorMsg("订单id不能为空");
        }

         myOrdersService.updateDeliverOrderStatus(orderId);
        return IMOOCJSONResult.ok();
    }
    @PostMapping("/confirmReceive")
    @ApiOperation(value = "确认收货",notes = "确认收货",httpMethod = "POST")
    public IMOOCJSONResult confirmReceive(
            @ApiParam(name = "orderId",value = "订单Id", required = true) @RequestParam String orderId,
            @ApiParam(name = "userId",value = "用户Id", required = true) @RequestParam String userId
    ){

        IMOOCJSONResult result = checkUserOrder(userId,orderId);
        if(result.getStatus() != HttpStatus.OK.value()){
            return result;
        }

       boolean res =  myOrdersService.updateReceiveOrderStatus(orderId);
        if(!res){
            return IMOOCJSONResult.errorMsg("订单确认收货失败");
        }
        return IMOOCJSONResult.ok();
    }
    @PostMapping("/delete")
    @ApiOperation(value = "用户删除订单",notes = "用户删除订单",httpMethod = "POST")
    public IMOOCJSONResult delete(
            @ApiParam(name = "orderId",value = "订单Id", required = true) @RequestParam String orderId,
            @ApiParam(name = "userId",value = "用户Id", required = true) @RequestParam String userId
    ){

        IMOOCJSONResult result = checkUserOrder(userId,orderId);
        if(result.getStatus() != HttpStatus.OK.value()){
            return result;
        }

        boolean res =  myOrdersService.deleteOrder(orderId,userId);
        if(!res){
            return IMOOCJSONResult.errorMsg("订单删除失败");
        }
        return IMOOCJSONResult.ok();
    }
    @PostMapping("/statusCounts")
    @ApiOperation(value = "获得订单状态数概况",notes = "获得订单状态数概况",httpMethod = "POST")
    public IMOOCJSONResult statusCounts(
            @ApiParam(name = "userId",value = "用户Id", required = true) @RequestParam String userId
    ){

        if(StringUtils.isBlank(userId)){
            return IMOOCJSONResult.errorMsg("null");
        }
        OrderStatusCountsVO countsVO = myOrdersService.getOrderStatusCounts(userId);
        return IMOOCJSONResult.ok(countsVO);
    }
    @PostMapping("/trend")
    @ApiOperation(value = "查询订单动向",notes = "查询订单动向",httpMethod = "POST")
    public IMOOCJSONResult trend(
            @ApiParam(name = "userId",value = "用户Id", required = true) @RequestParam String userId,
            @ApiParam(name = "page",value = "查询下一页的第几页", required = false) @RequestParam Integer page,
            @ApiParam(name = "pageSize",value = "分页的每一页显示的记录数", required = false) @RequestParam Integer pageSize
    ){

        if(StringUtils.isBlank(userId)){
            return IMOOCJSONResult.errorMsg("null");
        }
        if(page == null){
            page = 1;
        }
        if(pageSize == null){
            pageSize = COMMENT_PAGE_SIZE;
        }
        PagedGridResult result = myOrdersService.getOrdersTrend(userId,page,pageSize);
        return IMOOCJSONResult.ok(result);
    }
}
