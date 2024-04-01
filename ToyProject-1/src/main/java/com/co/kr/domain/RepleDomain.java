package com.co.kr.domain;

import lombok.Builder;
import lombok.Data;


@Data
@Builder(builderMethodName = "builder")
public class RepleDomain {
	private Integer bdSeq;
	private String mbId;
	private String reContent;
	private String reCreateAt;
}
