package com.oyome.service.impl;

import com.oyome.enums.OrderStatusEnum;
import com.oyome.enums.YesOrNo;
import com.oyome.mapper.CarouselMapper;
import com.oyome.mapper.OrderItemsMapper;
import com.oyome.mapper.OrderStatusMapper;
import com.oyome.mapper.OrdersMapper;
import com.oyome.pojo.*;
import com.oyome.pojo.bo.ShopcartBO;
import com.oyome.pojo.bo.SubmitOrderBO;
import com.oyome.pojo.vo.MerchantOrdersVO;
import com.oyome.pojo.vo.OrderVO;
import com.oyome.service.AddressService;
import com.oyome.service.CarouselService;
import com.oyome.service.ItemService;
import com.oyome.service.OrderService;
import com.oyome.utils.DateUtil;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.annotation.Order;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private AddressService addressService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrderItemsMapper orderItemsMapper;

    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public OrderVO createOrder(SubmitOrderBO orderBO,List<ShopcartBO> list) {
        String userId = orderBO.getUserId();
        String addressId = orderBO.getAddressId();
        String itemSpecIds = orderBO.getItemSpecIds();
        Integer payMethod = orderBO.getPayMethod();
        String leftMsg = orderBO.getLeftMsg();
        //包邮费用设置为0
        Integer postAmount = 0;
        String orderId = sid.nextShort();
        UserAddress userAddress =  addressService.queryUserAddress(userId,addressId);
        //1.新订单数据保存
        Orders newOrders = new Orders();
        newOrders.setId(orderId);
        newOrders.setUserId(userId);
        newOrders.setReceiverName(userAddress.getReceiver());
        newOrders.setReceiverMobile(userAddress.getMobile());
        newOrders.setReceiverAddress(
                userAddress.getProvince()+" "
                +userAddress.getCity()+" "
                +userAddress.getDistrict()+" "
                +userAddress.getDetail());

        newOrders.setPostAmount(postAmount);
        newOrders.setLeftMsg(leftMsg);
        newOrders.setPayMethod(payMethod);
        newOrders.setIsComment(YesOrNo.NO.type);
        newOrders.setIsDelete(YesOrNo.NO.type);
        newOrders.setCreatedTime(new Date());
        newOrders.setUpdatedTime(new Date());
        //2.循环根据itemSpecIds保存订单信息表
        String itemSpecIdArr[] =  itemSpecIds.split(",");
        Integer totalAmount = 0; // 商品原价累计
        Integer realPayAmount = 0; //优惠后的实际价格累计
        List<ShopcartBO> toBeRemovedShopCartBo = new ArrayList<>();
        for(String itemSpecId: itemSpecIdArr){
            // 整合redis后，商品购买的数量重新从redis的购物车中获取

            ShopcartBO cartBo =  getBuyCountFromShopCart(list,itemSpecId);

            int buyCounts = cartBo.getBuyCounts();
            toBeRemovedShopCartBo.add(cartBo);

            //2.1 根据规格id，查询规格的具体信息，主要获取价格
            ItemsSpec itemsSpec = itemService.queryItemSpecById(itemSpecId);
            totalAmount += itemsSpec.getPriceNormal() * buyCounts;
            realPayAmount += itemsSpec.getPriceDiscount() * buyCounts;

            //2.2 根据商品id，获取商品信息以及商品图片
            String itemId =  itemsSpec.getItemId();
            Items item = itemService.queryItemsById(itemId);
            String imgUrl = itemService.queryItemMainImgById(itemId);

            //2.3循环保存子订单到数据库
            OrderItems subOrderItems = new OrderItems();
            String subOrderId = sid.nextShort();
            subOrderItems.setId(subOrderId);
            subOrderItems.setOrderId(orderId);
            subOrderItems.setItemId(itemId);
            subOrderItems.setItemName(item.getItemName());
            subOrderItems.setItemImg(imgUrl);
            subOrderItems.setBuyCounts(buyCounts);
            subOrderItems.setItemSpecId(itemSpecId);
            subOrderItems.setItemSpecName(itemsSpec.getName());
            subOrderItems.setPrice(itemsSpec.getPriceDiscount());
            orderItemsMapper.insert(subOrderItems);

            //2.4在用户提交订单后，规格表中需要扣除库存
            itemService.decreaseItemSpecStock(itemSpecId,buyCounts);
        }
      newOrders.setTotalAmount(totalAmount);
      newOrders.setRealPayAmount(realPayAmount);
      ordersMapper.insert(newOrders);
        //3.保存订单状态表
        OrderStatus waitPayOrderStatus = new OrderStatus();
        waitPayOrderStatus.setOrderId(orderId);
        waitPayOrderStatus.setOrderStatus(OrderStatusEnum.WAIT_PAY.type);
        waitPayOrderStatus.setCreatedTime(new Date());
        orderStatusMapper.insert(waitPayOrderStatus);
        //4.构建商户订单，用于传给支付中心
        MerchantOrdersVO merchantOrdersVO = new MerchantOrdersVO();
        merchantOrdersVO.setMerchantOrderId(orderId);
        merchantOrdersVO.setMerchantUserId(userId);
        merchantOrdersVO.setAmount(realPayAmount + postAmount);
        merchantOrdersVO.setPayMethod(payMethod);

        //5.构建自定义订单VO
        OrderVO orderVO = new OrderVO();
        orderVO.setOrderId(orderId);
        orderVO.setMerchantOrdersVO(merchantOrdersVO);
        orderVO.setToBeRemovedShopCartBo(toBeRemovedShopCartBo);
        return orderVO;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateOrderStatus(String orderId, Integer orderStatus) {
        OrderStatus paidStatus = new OrderStatus();
        paidStatus.setOrderId(orderId);
        paidStatus.setOrderStatus(orderStatus);
        paidStatus.setPayTime(new Date());
        orderStatusMapper.updateByPrimaryKeySelective(paidStatus);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public OrderStatus queryOrderStatusInfo(String orderId) {

        return  orderStatusMapper.selectByPrimaryKey(orderId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void closeOrder() {
        //查询所有未支付订单。判断时间是否超过一天，超时则关闭交易
        OrderStatus queryOrder = new OrderStatus();
        queryOrder.setOrderStatus(OrderStatusEnum.WAIT_PAY.type);
        List<OrderStatus> list = orderStatusMapper.select(queryOrder);
        for(OrderStatus orderStatus : list){
            //获得订单创建时间
            Date createDate = orderStatus.getCreatedTime();
            //和当前时间进行对比
            int day = DateUtil.daysBetween(createDate,new Date());
            if(day >= 1){
            //超过一天，关闭订单
                doCloseOrder(orderStatus.getOrderId());
            }
        }

    }
    @Transactional(propagation = Propagation.REQUIRED)
    void doCloseOrder(String orderId){
        OrderStatus closeOrder =new OrderStatus();
        closeOrder.setOrderId(orderId);
        closeOrder.setOrderStatus(OrderStatusEnum.CLOSE.type);
        closeOrder.setCloseTime(new Date());
        orderStatusMapper.updateByPrimaryKeySelective(closeOrder);

    }

    private ShopcartBO getBuyCountFromShopCart(List<ShopcartBO>list,String itemSpecId){
        for(ShopcartBO bo : list){
            if(itemSpecId.equals(bo.getSpecId())){
                return bo;
            }
        }
        return null;
    }
}
