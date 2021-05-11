package com.oyome.controller;

import com.oyome.pojo.Stu;
import com.oyome.pojo.Users;
import com.oyome.pojo.bo.UserBO;
import com.oyome.service.StuService;
import com.oyome.service.UserService;
import com.oyome.utils.CookieUtils;
import com.oyome.utils.IMOOCJSONResult;
import com.oyome.utils.JsonUtils;
import com.oyome.utils.MD5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(value = "注册登录",tags = {"用于登录注册的接口"})
@RestController
@RequestMapping("/passport")
public class PassportController {
    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户名是否存在",notes = "用户名是否存在",httpMethod = "GET")
    @GetMapping("/usernameIsExist")
    public IMOOCJSONResult usernameIsExist(@RequestParam String username){
        //1.入参不能为空
        if(StringUtils.isBlank(username)){
            return IMOOCJSONResult.errorMsg("用户名不能为空");
        }
        //2。查找注册的用户名是否存在
       boolean isExist =  userService.queryUsernameIsExist(username);
        if(isExist){
            return IMOOCJSONResult.errorMsg("用户名已经存在");
        }
        //3.请求成功 用户名没有重复
        return IMOOCJSONResult.ok();
    }
    @ApiOperation(value = "用户注册",notes = "用户注册",httpMethod = "POST")
    @PostMapping("/regist")
    public IMOOCJSONResult regist(@RequestBody UserBO userBO,
                                  HttpServletRequest request,
                                  HttpServletResponse response){
        String username = userBO.getUsername();
        String password = userBO.getPassword();
        String confirmPwd = userBO.getConfirmPassword();

        // 0. 判断用户名和密码必须不为空
        if (StringUtils.isBlank(username) ||
                StringUtils.isBlank(password) ||
                StringUtils.isBlank(confirmPwd)) {
            return IMOOCJSONResult.errorMsg("用户名或密码不能为空");
        }

        // 1. 查询用户名是否存在
        boolean isExist = userService.queryUsernameIsExist(username);
        if (isExist) {
            return IMOOCJSONResult.errorMsg("用户名已经存在");
        }

        // 2. 密码长度不能少于6位
        if (password.length() < 6) {
            return IMOOCJSONResult.errorMsg("密码长度不能少于6");
        }

        // 3. 判断两次密码是否一致
        if (!password.equals(confirmPwd)) {
            return IMOOCJSONResult.errorMsg("两次密码输入不一致");
        }
        try {
            Users user = userService.createUser(userBO);
            CookieUtils.setCookie(request,response,"user",
                    JsonUtils.objectToJson(user),true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //TODO 生成用户token，存入redis会话
        //TODO 同步购物车数据
        return IMOOCJSONResult.ok();
    }
    @ApiOperation(value = "用户登录",notes = "用户登录",httpMethod = "POST")
    @PostMapping("/login")
    public IMOOCJSONResult login(@RequestBody UserBO userBO,
                                 HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        String username = userBO.getUsername();
        String password = userBO.getPassword();


        // 0. 判断用户名和密码必须不为空
        if (StringUtils.isBlank(username) ||
                StringUtils.isBlank(password) ) {
            return IMOOCJSONResult.errorMsg("用户名或密码不能为空");
        }





           Users user = userService.queryUserForLogin(username, MD5Utils.getMD5Str(password));
            if(user == null ){
                return IMOOCJSONResult.errorMsg("用户名或密码不正确");

            }
        user = setUsersNullProperty(user);
        CookieUtils.setCookie(request,response,"user",
                JsonUtils.objectToJson(user),true);

        //TODO 生成用户token，存入redis会话
        //TODO 同步购物车数据

        return IMOOCJSONResult.ok(user);
    }
    private Users setUsersNullProperty(Users users){
        users.setEmail(null);
        users.setMobile(null);
        users.setBirthday(null);
        users.setPassword(null);
        users.setCreatedTime(null);
        users.setUpdatedTime(null);
        return  users;
    }
    @ApiOperation(value = "用户退出登录",notes = "用户退出登录",httpMethod = "POST")
    @PostMapping("/logout")
    public IMOOCJSONResult logout(@RequestParam String userId,
                                  HttpServletRequest request,
                                  HttpServletResponse response) throws Exception {
        //清除用户相关的cookie
        CookieUtils.deleteCookie(request,response,"user");
        //TODO 用户退出登录，需要清空购物车
        //TODO 分布式会话中需要清除用户数据
        return IMOOCJSONResult.ok();
    }
}
