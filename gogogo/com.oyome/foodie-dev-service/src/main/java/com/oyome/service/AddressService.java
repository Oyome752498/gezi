package com.oyome.service;

import com.oyome.pojo.Carousel;
import com.oyome.pojo.UserAddress;
import com.oyome.pojo.bo.AddressBO;

import java.util.List;

public interface AddressService {
    /**
     * 根据用户id查询用户的收货列表
     * @param userId
     * @return
     */
    public List<UserAddress> queryAll(String userId);

    /**
     * 新增用户收获地址
     * @param addressBO
     * @return
     */
    public void addNewUserAddress(AddressBO addressBO);

    /**
     * 修改用户收获地址
     * @param addressBO
     * @return
     */
    public void updateUserAddress(AddressBO addressBO);

    /**
     * 根据用户id，地址id 删除用户收获地址
     * @param userId
     * @param addressId
     */
    public void deleteUserAddress(String userId,String addressId);

    /**
     *  根据用户id，地址id 设置默认地址
     * @param userId
     * @param addressId
     */

    public void setDefaultAddress(String userId,String addressId);

    /**
     * 根据用户id，地址id查询相应的地址信息
     * @param userId
     * @param addressId
     * @return
     */
    public UserAddress queryUserAddress(String userId , String addressId);

}
