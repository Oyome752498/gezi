package com.oyome.controller.center;

import com.oyome.pojo.Users;
import com.oyome.service.center.CenterUserService;
import com.oyome.utils.IMOOCJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "center - 用户中心",tags = {"用户中心展示相关的接口"})
@RestController
@RequestMapping("/center")
public class CenterController {
    @Autowired
    private CenterUserService centerUserService;

    @ApiOperation( value = "获取用户信息",notes = "获取用户信息", httpMethod = "GET")
    @GetMapping("/userInfo")
    public IMOOCJSONResult userInfo(@ApiParam(name = "userId", value = "用户id",required = true) @RequestParam String userId){
        Users u = centerUserService.queryUserInfo(userId);
        return IMOOCJSONResult.ok(u);
    }


}
