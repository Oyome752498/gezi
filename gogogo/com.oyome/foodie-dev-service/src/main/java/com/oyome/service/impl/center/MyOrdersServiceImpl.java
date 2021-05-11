package com.oyome.service.impl.center;

import com.fasterxml.jackson.databind.JsonSerializable;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.oyome.enums.OrderStatusEnum;
import com.oyome.enums.YesOrNo;
import com.oyome.mapper.OrderStatusMapper;
import com.oyome.mapper.OrdersMapper;
import com.oyome.mapper.OrdersMapperCustom;
import com.oyome.mapper.UsersMapper;
import com.oyome.pojo.OrderStatus;
import com.oyome.pojo.Orders;
import com.oyome.pojo.Users;
import com.oyome.pojo.bo.center.CenterUserBO;
import com.oyome.pojo.vo.MyOrdersVO;
import com.oyome.pojo.vo.OrderStatusCountsVO;
import com.oyome.service.center.BaseService;
import com.oyome.service.center.CenterUserService;
import com.oyome.service.center.MyOrdersService;
import com.oyome.utils.PagedGridResult;
import org.omg.CORBA.ObjectHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MyOrdersServiceImpl extends BaseService implements MyOrdersService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrdersMapperCustom ordersMapperCustom;

    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult queryMyOrders(String userId,
                                         Integer orderStatus,
                                         Integer page,
                                         Integer pageSize) {
        Map<String,Object> map = new HashMap<>();
        map.put("userId",userId);
        if(orderStatus != null){
            map.put("orderStatus",orderStatus);
        }
        PageHelper.startPage(page,pageSize);

        List<MyOrdersVO> list = ordersMapperCustom.queryMyOrders(map);
        setterPagedGrid(list,page);
        return setterPagedGrid(list,page);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateDeliverOrderStatus(String orderId) {
        OrderStatus updateOrder = new OrderStatus();
        updateOrder.setOrderStatus(OrderStatusEnum.WAIT_RECEIVE.type);
        updateOrder.setDeliverTime(new Date());
        Example example = new Example(OrderStatus.class);
        Example.Criteria updateOrderCriteria = example.createCriteria();
        updateOrderCriteria.andEqualTo("orderId",orderId);
        updateOrderCriteria.andEqualTo("orderStatus",OrderStatusEnum.WAIT_DELIVER.type);
        orderStatusMapper.updateByExampleSelective(updateOrder,example);
    }
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Orders queryMyOrder(String orderId, String userId) {
        Orders orders = new Orders();
        orders.setId(orderId);
        orders.setUserId(userId);
        orders.setIsDelete(YesOrNo.NO.type);

        return ordersMapper.selectOne(orders);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public boolean updateReceiveOrderStatus(String orderId) {
         OrderStatus updateOrder = new OrderStatus();
         updateOrder.setOrderStatus(OrderStatusEnum.SUCCESS.type);
         updateOrder.setSuccessTime(new Date());
         Example example = new Example(OrderStatus.class);
         Example.Criteria criteria = example.createCriteria();
         criteria.andEqualTo("orderId",orderId);
         criteria.andEqualTo("orderStatus",OrderStatusEnum.WAIT_RECEIVE.type);
         int result = orderStatusMapper.updateByExampleSelective(updateOrder,example);

        return result == 1  ? true : false;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public boolean deleteOrder(String orderId, String userId) {
        Orders updateOrder = new Orders();
        updateOrder.setIsDelete(YesOrNo.YES.type);
        updateOrder.setUpdatedTime(new Date());
        Example example = new Example(Orders.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",orderId);
        criteria.andEqualTo("userId",userId);
        int result = ordersMapper.updateByExampleSelective(updateOrder,example);

        return result == 1  ? true : false;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public OrderStatusCountsVO getOrderStatusCounts(String userId) {
        Map<String, Object>map = new HashMap<>();
        map.put("userId",userId);
        map.put("orderStatus",OrderStatusEnum.WAIT_PAY.type);
        int waitPayCounts =  ordersMapperCustom.getMyOrderStatusCounts(map);

        map.put("orderStatus",OrderStatusEnum.WAIT_DELIVER.type);
        int waitDeliverCounts =  ordersMapperCustom.getMyOrderStatusCounts(map);

        map.put("orderStatus",OrderStatusEnum.WAIT_RECEIVE.type);
        int waitReceiveCounts =  ordersMapperCustom.getMyOrderStatusCounts(map);

        map.put("orderStatus",OrderStatusEnum.SUCCESS.type);
        map.put("isComment",YesOrNo.NO.type);
        int waitCommentCounts =  ordersMapperCustom.getMyOrderStatusCounts(map);

        OrderStatusCountsVO vo = new OrderStatusCountsVO();
        vo.setWaitCommentCounts(waitCommentCounts);
        vo.setWaitDeliverCounts(waitDeliverCounts);
        vo.setWaitPayCounts(waitPayCounts);
        vo.setWaitReceiveCounts(waitReceiveCounts);

        return vo;
    }
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult getOrdersTrend(String userId, Integer page, Integer pageSize) {
        Map<String, Object>map = new HashMap<>();
        map.put("userId",userId);

        PageHelper.startPage(page,pageSize);
        List<OrderStatus> list = ordersMapperCustom.getMyOrderTrend(map);
        return setterPagedGrid(list,page);
    }


}
