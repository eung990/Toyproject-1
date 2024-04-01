package com.co.kr.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.co.kr.domain.RepleDomain;

@Mapper
public interface RepleMapper {
	
	public void repleUpload(RepleDomain repleDomain);
	
	public List<RepleDomain> repleList(int bdSeq);
	
	public void repleDelete(HashMap<String, Object> map);
}
