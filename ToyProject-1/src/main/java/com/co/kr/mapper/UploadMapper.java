package com.co.kr.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.co.kr.domain.BoardContentDomain;
import com.co.kr.domain.BoardFileDomain;
import com.co.kr.domain.BoardListDomain;

@Mapper
public interface UploadMapper {
	// list
	public List<BoardListDomain> boardList();

	// content insert
	public void bdContentUpload(BoardContentDomain boardContentDomain);

	// file insert
	public void bdFileUpload(BoardFileDomain boardFileDomain);

	// content update
	public void bdContentUpdate(BoardContentDomain boardContentDomain);

	// file update
	public void bdFileUpdate(BoardFileDomain boardFileDomain);

	// content delete
	public void bdContentDelete(HashMap<String, Object> map);

	// file delete
	public void bdFileDelete(BoardFileDomain boardFileDomain);

	// select one
	public BoardListDomain bdSelectOne(HashMap<String, Object> map);

	// select one file
	public List<BoardFileDomain> bdSelectOneFile(HashMap<String, Object> map);

}
