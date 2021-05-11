package com.oyome.service;

import com.oyome.pojo.Stu;
import com.oyome.pojo.Users;
import com.oyome.pojo.bo.UserBO;


public interface UserService {
    /**
     * 判断用户名是否存在
     */
  public boolean queryUsernameIsExist(String username);

    /**
     * 创建用户
     * @param userBO
     * @return
     */
  public Users createUser(UserBO userBO) throws Exception;

    /**
     * 检索用户名和密码是否匹配，用于登录
     * @param username
     * @param password
     * @return
     */
  public Users queryUserForLogin(String username,String password);
}
