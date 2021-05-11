package com.oyome.controller;

import com.oyome.utils.RedisOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@RestController
@RequestMapping("redis")
public class RedisController {
	final static Logger logger = LoggerFactory.getLogger(RedisController.class);

	@Autowired
	private RedisOperator redisOperator;

	@RequestMapping("/set")
	public Object set(String key,String value){
		redisOperator.set(key,value);
		return "ok";
	}
	@RequestMapping("/get")
	public String get(String key){

		return redisOperator.get(key);
	}
	@RequestMapping("/delete")
	public Object delete(String key){
		redisOperator.del(key);
		return "delete ok";
	}
}
