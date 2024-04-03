package com.co.kr.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.co.kr.domain.LoginDomain;

@Mapper
public interface UserMapper {

	// 전체리스트 조회
	public LoginDomain mbSelectList(Map<String, String> map);

	// 신규 저장
	public void mbCreate(LoginDomain loginDomain);

	// 전체데이터
	public List<LoginDomain> mbAllList(Map<String, String> map);

	// 전체갯수
	public int mbGetAll();

	// id정보 가져오기
	public LoginDomain mbGetId(Map<String, String> map);

	// 중복체크
	public int mbDuplicationCheck(Map<String, String> map);

	// 카카오 중복체크
	public int KakaoMbDuplicationCheck(Map<String, String> map);

	// 업데이트
	public void mbUpdate(LoginDomain loginDomain);

	// 삭제
	public void mbDelete(Map<String, String> map);
}
