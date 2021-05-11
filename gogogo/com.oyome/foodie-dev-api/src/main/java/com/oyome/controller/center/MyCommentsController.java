package com.oyome.controller.center;

import com.oyome.controller.BaseController;
import com.oyome.enums.YesOrNo;
import com.oyome.pojo.OrderItems;
import com.oyome.pojo.Orders;
import com.oyome.pojo.bo.center.OrderItemsCommentBO;
import com.oyome.service.center.MyCommentsService;
import com.oyome.service.center.MyOrdersService;
import com.oyome.utils.IMOOCJSONResult;
import com.oyome.utils.PagedGridResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "用户中心评价模块",tags = {"用户相关用户中心评价模块接口"})
@RestController
@RequestMapping("/mycomments")
public class MyCommentsController extends BaseController {
    @Autowired
    private MyCommentsService myCommentsService;
    @PostMapping("/pending")
    @ApiOperation(value = "查询订单列表",notes = "查询订单列表",httpMethod = "POST")
    public IMOOCJSONResult pending(
            @ApiParam(name = "userId",value = "用户id", required = true) @RequestParam String userId,
            @ApiParam(name = "orderId",value = "订单id", required = true) @RequestParam String orderId
           ){

        //判断用户和订单是否关联
        IMOOCJSONResult result = checkUserOrder(userId,orderId);
        if(result.getStatus() != HttpStatus.OK.value()){
            return result;
        }
        //判断该笔订单是否已经评价，评价过return
        Orders myOrder = (Orders) result.getData();
        if(myOrder.getIsComment() == YesOrNo.YES.type){
            return IMOOCJSONResult.errorMsg("该笔订单已经评价");
        }
        List<OrderItems> list = myCommentsService.queryPendingComment(orderId);
        return IMOOCJSONResult.ok(list);
    }
    @PostMapping("/saveList")
    @ApiOperation(value = "保存评论列表",notes = "保存评论列表",httpMethod = "POST")
    public IMOOCJSONResult saveList(
            @ApiParam(name = "userId",value = "用户id", required = true) @RequestParam String userId,
            @ApiParam(name = "orderId",value = "订单id", required = true) @RequestParam String orderId,
            @RequestBody List<OrderItemsCommentBO> commentList
    ){

        //判断用户和订单是否关联
        IMOOCJSONResult result = checkUserOrder(userId,orderId);
        if(result.getStatus() != HttpStatus.OK.value()){
            return result;
        }
        //判断评论内容list不能为空
        if(commentList == null || commentList.size() == 0|| commentList.isEmpty()){
            return IMOOCJSONResult.errorMsg("评论内容不能为空");
        }

       myCommentsService.saveComments(orderId,userId,commentList);
        return IMOOCJSONResult.ok();
    }

    @PostMapping("/query")
    @ApiOperation(value = "查询我的评价",notes = "查询我的评价",httpMethod = "POST")
    public IMOOCJSONResult query(
            @ApiParam(name = "userId",value = "用户id", required = true) @RequestParam String userId,
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
        PagedGridResult result = myCommentsService.queryMyComments(userId,page,pageSize);
        return IMOOCJSONResult.ok(result);
    }
}
