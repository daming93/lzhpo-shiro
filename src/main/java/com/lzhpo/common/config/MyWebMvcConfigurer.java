package com.lzhpo.common.config;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import cn.hutool.system.SystemUtil;

/**
 * <p>
 * Author：lzhpo
 * </p>
 * <p>
 * Title：
 * </p>
 * <p>
 * Description：
 * </p>
 */
@SpringBootConfiguration
public class MyWebMvcConfigurer implements WebMvcConfigurer {

	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		String osName = SystemUtil.getOsInfo().getName();
		if (osName.contains("Window")) {
			registry.addResourceHandler("/**").addResourceLocations("file: d:");
		}else{
			registry.addResourceHandler("/**").addResourceLocations("/");
		}
	
	}

}
