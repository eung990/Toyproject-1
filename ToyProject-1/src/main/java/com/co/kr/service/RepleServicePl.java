package com.co.kr.service;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.co.kr.domain.RepleDomain;
import com.co.kr.mapper.RepleMapper;
import com.co.kr.vo.RepleVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class RepleServicePl implements RepleService{
	
	@Autowired
	RepleMapper repleMapper;
	
	@Override
	public int repleProcess(HttpServletRequest request,RepleVO repleVO) {
		HttpSession session = request.getSession();
		int bdSeq = Integer.parseInt((String)session.getAttribute("bdSeq"));
		System.out.println("repleProcess에서 가져온 bdSeq값====>" + bdSeq);
		System.out.println("repleProcess에서 가져온 id값====>" +session.getAttribute("id").toString() );

		RepleDomain repleDomain = RepleDomain.builder().mbId(session.getAttribute("id").toString()).bdSeq(bdSeq).reContent(repleVO.getRe_content()).build();
		repleMapper.repleUpload(repleDomain);
		return bdSeq;
	}
	@Override
	public void repleUpload(RepleDomain repleDomain) {
		repleMapper.repleUpload(repleDomain);
	}

	@Override
	public List<RepleDomain> repleList(int bdSeq) {
		return repleMapper.repleList(bdSeq);
	}
	@Override
	public void repleDelete(HashMap<String, Object> map) {
		repleMapper.repleDelete(map);
	}
	
}
