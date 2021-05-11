package com.oyome.service.impl;

import com.oyome.enums.YesOrNo;
import com.oyome.mapper.CarouselMapper;
import com.oyome.mapper.UserAddressMapper;
import com.oyome.pojo.Carousel;
import com.oyome.pojo.UserAddress;
import com.oyome.pojo.bo.AddressBO;
import com.oyome.service.AddressService;
import com.oyome.service.CarouselService;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private UserAddressMapper userAddressMapper;
    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<UserAddress> queryAll(String userId) {
        UserAddress ua = new UserAddress();
        ua.setUserId(userId);
        return  userAddressMapper.select(ua);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void addNewUserAddress(AddressBO addressBO) {
        //1.判断当前用户是否存在地址，如果没有，则新增为默认地址
        List<UserAddress> list = this.queryAll(addressBO.getUserId());
        Integer isDefault = 0;
        if(list == null || list.isEmpty() ||list.size() == 0){
            isDefault = 1;
        }
        String addressId = sid.nextShort();
        //2.保存数据库
        UserAddress userAddress = new UserAddress();
        BeanUtils.copyProperties(addressBO,userAddress);
        userAddress.setId(addressId);
        userAddress.setIsDefault(isDefault);
        userAddress.setCreatedTime(new Date());
        userAddress.setUpdatedTime(new Date());
        userAddressMapper.insert(userAddress);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateUserAddress(AddressBO addressBO) {
        String addressId = addressBO.getAddressId();
        UserAddress updateAddress = new UserAddress();
        BeanUtils.copyProperties(addressBO,updateAddress);
        updateAddress.setId(addressId);
        updateAddress.setUpdatedTime(new Date());
        userAddressMapper.updateByPrimaryKeySelective(updateAddress);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void deleteUserAddress(String userId, String addressId) {
        UserAddress updateAddress = new UserAddress();
        updateAddress.setUserId(userId);
        updateAddress.setId(addressId);
        userAddressMapper.delete(updateAddress);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void setDefaultAddress(String userId, String addressId) {
        //1.查找默认地址，设置为不默认
            UserAddress queryAddress = new UserAddress();
            queryAddress.setUserId(userId);
            queryAddress.setIsDefault(YesOrNo.YES.type);
            List<UserAddress> list = userAddressMapper.select(queryAddress);
            for(UserAddress address: list){
                address.setIsDefault(YesOrNo.NO.type);
                userAddressMapper.updateByPrimaryKeySelective(address);
            }
        //2.根据地址id修改为默认地址
        UserAddress defaultAddress = new UserAddress();
        defaultAddress.setIsDefault(YesOrNo.YES.type);
        defaultAddress.setUserId(userId);
        defaultAddress.setId(addressId);
        userAddressMapper.updateByPrimaryKeySelective(defaultAddress);

    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public UserAddress queryUserAddress(String userId, String addressId) {
        UserAddress userAddress = new UserAddress();
        userAddress.setId(addressId);
        userAddress.setUserId(userId);
        userAddressMapper.selectOne(userAddress);
        return  userAddressMapper.selectOne(userAddress);

    }


}
