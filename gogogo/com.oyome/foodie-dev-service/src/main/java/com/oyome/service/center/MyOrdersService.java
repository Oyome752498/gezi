package com.oyome.service.center;

import com.oyome.pojo.Orders;
import com.oyome.pojo.Users;
import com.oyome.pojo.bo.center.CenterUserBO;
import com.oyome.pojo.vo.OrderStatusCountsVO;
import com.oyome.utils.PagedGridResult;


public interface MyOrdersService {

  /**
   * 查询我的订单列表
   * @param userId
   * @param orderStatus
   * @param page
   * @param pageSize
   * @return
   */
  public PagedGridResult queryMyOrders(String userId,
                                       Integer orderStatus,
                                       Integer page,
                                       Integer pageSize);

  /**
   * 订单状态 -> 商家发货
   * @param orderId
   */
  public void updateDeliverOrderStatus(String orderId);

  /**
   * 查询我的订单
   * @param orderId
   * @param userId
   * @return
   */
  public Orders queryMyOrder(String orderId,String userId);

  /**
   * 更新订单状态 -> 确认收货
   * @param orderId
   * @return
   */
  public boolean updateReceiveOrderStatus(String orderId);
  /**
   * 删除订单（逻辑删除）
   * @param orderId
   * @return
   */
  public boolean deleteOrder(String orderId,String userId);

  /**
   * 查询用户订单数
   * @param userId
   */

  public OrderStatusCountsVO getOrderStatusCounts(String userId);


  /**
   * 获得分页的订单动向
   * @param userId
   * @param page
   * @param pageSize
   * @return
   */
  public PagedGridResult getOrdersTrend(String userId,
                                       Integer page,
                                       Integer pageSize);
}
