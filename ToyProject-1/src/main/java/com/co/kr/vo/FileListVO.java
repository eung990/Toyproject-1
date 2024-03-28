package com.co.kr.vo;

import javax.swing.border.TitledBorder;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FileListVO {

	private String title; //게시글 제목
	private String content; //게시글 내용
	private String seq; //게시글 번호
	private String isEdit; // 편집
}
