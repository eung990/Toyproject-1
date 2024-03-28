package com.co.kr.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.co.kr.domain.LoginDomain;
import com.co.kr.mapper.UserMapper;

@Service
public class UserServiceImpl implements UserSerivce {

	@Autowired
	private UserMapper userMapper;

	@Override
	public LoginDomain mbSelectList(Map<String, String> map) {
		return userMapper.mbSelectList(map);
	}

	@Override
	public List<LoginDomain> mbAllList(Map<String, String> map) {
		// TODO Auto-generated method stub
		return userMapper.mbAllList(map);
	}

	@Override
	public int mbGetAll() {
		// TODO Auto-generated method stub
		return userMapper.mbGetAll();
	}

	@Override
	public void mbCreate(LoginDomain loginDomain) {
		// TODO Auto-generated method stub
		userMapper.mbCreate(loginDomain);

	}

	@Override
	public LoginDomain mbGetId(Map<String, String> map) {
		// TODO Auto-generated method stub
		return userMapper.mbGetId(map);
	}

	@Override
	public int mbDuplicationCheck(Map<String, String> map) {
		// TODO Auto-generated method stub
		return userMapper.mbDuplicationCheck(map);
	}
	
	@Override
	public int KakaoMbDuplicationCheck(Map<String, String> map) {
		// TODO Auto-generated method stub
		return userMapper.KakaoMbDuplicationCheck(map);
	}

	@Override
	public void mbUpdate(LoginDomain loginDomain) {
		// TODO Auto-generated method stub
		userMapper.mbUpdate(loginDomain);
		
	}

	@Override
	public void mbRemove(Map<String, String> map) {
		// TODO Auto-generated method stub
		userMapper.mbDelete(map);
	}

}
