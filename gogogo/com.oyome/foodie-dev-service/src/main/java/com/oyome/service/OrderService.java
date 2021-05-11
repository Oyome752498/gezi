package com.oyome.service;

import com.oyome.pojo.Carousel;
import com.oyome.pojo.OrderStatus;
import com.oyome.pojo.bo.ShopcartBO;
import com.oyome.pojo.bo.SubmitOrderBO;
import com.oyome.pojo.vo.OrderVO;

import java.util.List;

public interface OrderService {

    /**
     * 用于创建订单相关信息
     * @param orderBO
     * @return
     */
    public OrderVO createOrder(SubmitOrderBO orderBO,List<ShopcartBO> list);

    /**
     * 修改订单状态
     * @param orderId
     * @param orderStatus
     */
    public void updateOrderStatus(String orderId, Integer orderStatus);

    /**
     * 查询订单状态
     * @param orderId
     * @return
     */
    public OrderStatus queryOrderStatusInfo(String orderId);


    /**
     * 关闭超市未支付订单
     */
    public void closeOrder();
}
