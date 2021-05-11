package com.oyome.service.impl.center;

import com.github.pagehelper.PageInfo;
import com.oyome.enums.YesOrNo;
import com.oyome.mapper.*;
import com.oyome.pojo.OrderItems;
import com.oyome.pojo.OrderStatus;
import com.oyome.pojo.Orders;
import com.oyome.pojo.Users;
import com.oyome.pojo.bo.center.CenterUserBO;
import com.oyome.pojo.bo.center.OrderItemsCommentBO;
import com.oyome.pojo.vo.MyCommentVO;
import com.oyome.service.center.BaseService;
import com.oyome.service.center.CenterUserService;
import com.oyome.service.center.MyCommentsService;
import com.oyome.utils.PagedGridResult;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MyCommentsServiceImpl extends BaseService implements MyCommentsService {

    @Autowired
    private OrderItemsMapper orderItemsMapper;

    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private ItemsCommentsMapperCustom itemsCommentsMapperCustom;

    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<OrderItems> queryPendingComment(String orderId) {
        OrderItems orders = new OrderItems();
        orders.setOrderId(orderId);

        return orderItemsMapper.select(orders);
    }
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveComments(String orderId, String userId, List<OrderItemsCommentBO> list) {
        //1.保存评价 items_comments
            for(OrderItemsCommentBO bo:list){
                bo.setCommentId(sid.nextShort());
//                bo.
            }
            Map<String,Object> map =  new HashMap<>();
            map.put("userId",userId);
            map.put("commentList",list);
        itemsCommentsMapperCustom.saveComments(map);
        //2.修改订单表 改为已评价 orders
            Orders orders = new Orders();
           orders.setId(orderId);
           orders.setIsComment(YesOrNo.YES.type);
           ordersMapper.updateByPrimaryKeySelective(orders);
        //3.修改订单状态表的留言是键 order_status
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setCommentTime(new Date());
        orderStatusMapper.updateByPrimaryKeySelective(orderStatus);
    }


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult queryMyComments(String userId,
                                           Integer page,
                                           Integer pageSize) {
        Map<String,Object>map =new HashMap<>();
        map.put("userId",userId);
         List<MyCommentVO> list = itemsCommentsMapperCustom.queryMyComments(map);
        return   setterPagedGrid(list,page);
    }


}
