package com.co.kr.service;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.co.kr.domain.RepleDomain;
import com.co.kr.vo.RepleVO;

public interface RepleService {
	
	public void repleUpload(RepleDomain repleDomain);
		
	public List<RepleDomain> repleList(int bdSeq);

	public int repleProcess(HttpServletRequest request, RepleVO repleVO);

	public void repleDelete(HashMap<String, Object> map);

}
