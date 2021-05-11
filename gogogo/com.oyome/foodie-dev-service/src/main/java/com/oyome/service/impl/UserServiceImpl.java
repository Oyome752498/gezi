package com.oyome.service.impl;

import com.oyome.enums.Sex;
import com.oyome.mapper.UsersMapper;
import com.oyome.pojo.Users;
import com.oyome.pojo.bo.UserBO;
import com.oyome.service.UserService;
import com.oyome.utils.DateUtil;
import com.oyome.utils.MD5Utils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;

@Service
public class UserServiceImpl implements UserService {
    private static final String FACE_URL ="https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1601442376193&di=c82e4f7f74d12f0525a1b24bc1641ac6&imgtype=0&src=http%3A%2F%2Fdiy.qqjay.com%2Fu%2Ffiles%2F2012%2F0510%2F25c1770e108250f8a14cbc468c2030bf.jpg";
    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryUsernameIsExist(String username) {
        Example example = new Example(Users.class);
        Example.Criteria userCriteria =  example.createCriteria();
        userCriteria.andEqualTo("username",username);
        Users users =usersMapper.selectOneByExample(example);
        return users == null ? false :true;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users createUser(UserBO userBO) {
        String userid = sid.nextShort();
        Users u = new Users();
        u.setId(userid);
        u.setUsername(userBO.getUsername());
        try {
            u.setPassword(MD5Utils.getMD5Str(userBO.getPassword()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //默认用户昵称同用户名
        u.setNickname(userBO.getUsername());
        //设置头像
        u.setFace(FACE_URL);
        //设置默认生日
        u.setBirthday(DateUtil.stringToDate("1900-01-01"));
        //设置默认性别为保密
        u.setSex(Sex.secret.type);
        u.setCreatedTime(new Date());
        u.setUpdatedTime(new Date());
        usersMapper.insert(u);
        return u;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserForLogin(String username, String password) {
        Example example = new Example(Users.class);
        Example.Criteria userCriteria =  example.createCriteria();
        userCriteria.andEqualTo("username",username);
        userCriteria.andEqualTo("password",password);
        Users users =usersMapper.selectOneByExample(example);
        return users ;
    }
}
