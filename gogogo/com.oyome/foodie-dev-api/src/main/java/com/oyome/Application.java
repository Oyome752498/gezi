package com.oyome;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
//扫描mybaits 通用 mapper 所在的包
@MapperScan(basePackages = "com.oyome.mapper")
//扫描所有包，以及相关所有包
@ComponentScan(basePackages = {"com.oyome","org.n3r.idworker"})
@EnableScheduling
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class,args);
	}
}
