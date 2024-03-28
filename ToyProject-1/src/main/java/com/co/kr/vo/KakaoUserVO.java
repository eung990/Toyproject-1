package com.co.kr.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class KakaoUserVO {

	//json 데이터 클래스 각각의 항목들과 동일이름으로 맵핑
	public Long id;
	public String connected_at;
	private String admin;
	public Properties properties;
	public KakaoAccount kakao_account;

	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	public class Properties {

		public String nickname;
		public String profile_image;
		public String thumbnail_image;

	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	public class KakaoAccount {

		public String email;

		@JsonIgnoreProperties(ignoreUnknown = true)
		@Data
		public class Profile {

			public String nickname;
			public String thumbnail_image_url;

		}
	}

}
