package com.co.kr.config;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Bean
	@Description("Thymeleaf template resolver serving HTML")
	public ClassLoaderTemplateResolver templateResolver() {

		var templateResolver = new ClassLoaderTemplateResolver();
		templateResolver.setPrefix("templates/");
		templateResolver.setSuffix(".html");
		templateResolver.setCacheable(true);
		templateResolver.setTemplateMode("html");
		templateResolver.setCharacterEncoding("UTF-8");
		return templateResolver;
	}

	@Bean
	@Description("Thymeleaf template engine with Spring integration")
	public SpringTemplateEngine mytemplateEngine() {
		var templateEngine = new SpringTemplateEngine();
		templateEngine.setTemplateResolver(templateResolver());
		templateEngine.addDialect(new LayoutDialect()); // dependency 미리 설정해놓음
		return templateEngine;
	}

	@Bean
	@Description("Thymeleaf view resolver")
	public ViewResolver viewResolver() {
		var viewResolver = new ThymeleafViewResolver();
		viewResolver.setTemplateEngine(mytemplateEngine());
		viewResolver.setCharacterEncoding("UTF-8");
		return viewResolver;
	}

	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("index.html");
	}

	// upload 자체 플젝내 설정
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// /images/** 은 /resources/images/ 으로 시작하는 uri호출은 /resources/images/ 경로 하위에 있는
		// 리소스 파일이다 라는 의미입니다.
		registry.addResourceHandler("/resources/upload/**").addResourceLocations("file:///C:/upload/");
		 Path rootPath = Paths.get(new File("C://").toString(),"upload",File.separator).toAbsolutePath().normalize();	
		 
	}
}