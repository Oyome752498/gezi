package com.oyome.service.center;

import com.oyome.pojo.Users;
import com.oyome.pojo.bo.UserBO;
import com.oyome.pojo.bo.center.CenterUserBO;


public interface CenterUserService {

  /**
   * 根据用户id查询用户信息
   * @param userId
   * @return
   */
  public Users queryUserInfo(String userId);

  /**
   * 修改用户信息
   * @param userId
   * @param centerUserBO
   */
  public Users updateUserInfo(String userId, CenterUserBO centerUserBO);

  /**
   * 根据用户id 更新用户头像
   * @param userId
   * @param faceUrl
   * @return
   */
  public Users updateUserFace(String userId, String faceUrl);

}
