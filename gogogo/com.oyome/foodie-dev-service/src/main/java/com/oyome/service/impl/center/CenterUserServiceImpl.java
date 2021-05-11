package com.oyome.service.impl.center;

import com.oyome.enums.Sex;
import com.oyome.mapper.UsersMapper;
import com.oyome.pojo.Users;
import com.oyome.pojo.bo.UserBO;
import com.oyome.pojo.bo.center.CenterUserBO;
import com.oyome.service.UserService;
import com.oyome.service.center.CenterUserService;
import com.oyome.utils.DateUtil;
import com.oyome.utils.MD5Utils;
import com.oyome.utils.PagedGridResult;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;

@Service
public class CenterUserServiceImpl implements CenterUserService {

    @Autowired
    private UsersMapper usersMapper;
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserInfo(String userId) {
        Users  u = usersMapper.selectByPrimaryKey(userId);
        u.setPassword(null);
        return u;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users updateUserInfo(String userId, CenterUserBO centerUserBO) {
        Users updateUser = new Users();
        BeanUtils.copyProperties(centerUserBO,updateUser);
        updateUser.setId(userId);
        updateUser.setUpdatedTime(new Date());
        usersMapper.updateByPrimaryKeySelective(updateUser);
        Users user = queryUserInfo(userId);
        return user;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users updateUserFace(String userId, String faceUrl) {
        Users updateUser = new Users();
        updateUser.setId(userId);
        updateUser.setUpdatedTime(new Date());
        updateUser.setFace(faceUrl);
        usersMapper.updateByPrimaryKeySelective(updateUser);
        return queryUserInfo(userId);
    }
}
