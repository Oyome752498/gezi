package com.oyome.service.center;

import com.oyome.pojo.OrderItems;
import com.oyome.pojo.Users;
import com.oyome.pojo.bo.center.CenterUserBO;
import com.oyome.pojo.bo.center.OrderItemsCommentBO;
import com.oyome.utils.PagedGridResult;

import java.util.List;


public interface MyCommentsService {

  /**
   * 根据订单id 查询关联商品
   * @param orderId
   * @return
   */
  public List<OrderItems> queryPendingComment(String orderId);

  /**
   * 保存用户的评论
   * @param orderId
   * @param userId
   */

  public void saveComments(String orderId, String userId, List<OrderItemsCommentBO>list);

  /**
   * 我的评价查询 分页
   * @param userId
   * @param page
   * @param pageSize
   * @return
   */
  public PagedGridResult queryMyComments( String userId, Integer page, Integer pageSize);
}
