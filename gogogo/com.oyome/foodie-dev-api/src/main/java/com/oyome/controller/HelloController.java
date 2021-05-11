package com.oyome.controller;

import com.oyome.mapper.CarouselMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@RestController
@Lazy
public class HelloController {
	final static Logger logger = LoggerFactory.getLogger(HelloController.class);
	@RequestMapping("/hello")
	public Object hello(){
		String name = "oyome";
		logger.info("log.info :{}",name);
		return "Hello Oyome";
	}
}
