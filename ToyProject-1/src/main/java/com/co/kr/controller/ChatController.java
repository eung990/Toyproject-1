package com.co.kr.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
public class ChatController {
	
	@RequestMapping("chat")
	public ModelAndView chat(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		String userId = request.getSession().getAttribute("id").toString();
		mav.addObject("userId", userId);
		mav.setViewName("chat/chat.html");
		return mav;
	}


}
