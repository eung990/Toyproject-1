package com.co.kr.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.co.kr.domain.BoardListDomain;
import com.co.kr.domain.LoginDomain;
import com.co.kr.service.UploadService;
import com.co.kr.service.UserSerivce;
import com.co.kr.util.CommonUtils;
import com.co.kr.util.Pagination;
import com.co.kr.vo.KakaoUserVO;
import com.co.kr.vo.LoginVO;
import com.co.kr.vo.OAuthToken;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping(value = "/")
public class UserController {

	@Autowired // 자동 의존성 주입, 해당타입의 빈을 알아서 찾아줌
	private UserSerivce userService;

	@Autowired
	private UploadService uploadService;

	@GetMapping("/auth/kakao/callback")
	// 여기서 받는 code를 가지고 토큰을 받아 사용자 정보를 받아옴
	public ModelAndView kakaoCalback(String code, HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		final String CLIENT_ID = "a0bd036a1541fbc673a69cb52fe84a28";
		final String GRANT_TYPE = "authorization_code";
		final String REDIRECT_URL = "http://localhost:8083/auth/kakao/callback";
		final String CONTENT_TYPE = "application/x-www-form-urlencoded;charset=utf-8";

		// post방식으로 key - value 데이터 요청 (카카오로)
		RestTemplate rt = new RestTemplate();

		// HttpHeader object 생성
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", CONTENT_TYPE);

		// HttpBody object 생성
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", GRANT_TYPE);
		params.add("client_id", CLIENT_ID);
		params.add("redirect_uri", REDIRECT_URL);
		params.add("code", code);

		// HttpHeader와 HttpBody 하나의 오브젝트에 담기
		HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

		ResponseEntity<String> responseEntity = rt.exchange("https://kauth.kakao.com/oauth/token", HttpMethod.POST,
				kakaoTokenRequest, String.class);
		// json 데이터를 object에 담는다
		ObjectMapper objectMapper = new ObjectMapper();
		OAuthToken oauthToken = null;
		try {
			// 여기서 인자 안에 "OAuthToken.class"는 "responseEntity.getBody()" 를 "OAuthToken.class"
			// 맵핑한다는 뜻이다
			oauthToken = objectMapper.readValue(responseEntity.getBody(), OAuthToken.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		System.out.println("kakao Access Token ==> " + oauthToken.getAccess_token());

		// ========================================================

		// post방식으로 key - value 데이터 요청 (카카오로)
		RestTemplate rt2 = new RestTemplate();

		// HttpHeader object 생성
		HttpHeaders headers2 = new HttpHeaders();
		headers2.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		headers2.add("Authorization", "Bearer " + oauthToken.getAccess_token());
		System.out.println("토큰 값 =====>" + oauthToken.getAccess_token());

		// HttpHeader와 HttpBody 하나의 오브젝트에 담기
		HttpEntity<MultiValueMap<String, String>> kakaoProfile = new HttpEntity<>(headers2);

		ResponseEntity<String> responseEntity2 = rt2.exchange("https://kapi.kakao.com/v2/user/me", HttpMethod.POST,
				kakaoProfile, String.class);
		System.out.println("사용자 정보 >>>>>>>>>>" + responseEntity2.getBody());
		// json 데이터를 object에 담는다
		ObjectMapper objectMapper2 = new ObjectMapper();
		KakaoUserVO kakaoUserVO = null;
		try {
			// 여기서 인자 안에 "OAuthToken.class"는 "responseEntity.getBody()" 를 "OAuthToken.class"
			// 맵핑한다는 뜻이다
			kakaoUserVO = objectMapper2.readValue(responseEntity2.getBody(), KakaoUserVO.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		System.out.println("카카오 아이디(번호) ==> " + kakaoUserVO.getId());
		System.out.println("카카오 이메일 ==> " + kakaoUserVO.getKakao_account().getEmail());

		// Session 처리
		HttpSession session = request.getSession();
		ModelAndView mav = new ModelAndView();

		// 중복체크
		Map<String, String> map = new HashMap();
		map.put("mbId", kakaoUserVO.getKakao_account().getEmail() + "_" + kakaoUserVO.getId());
		// map.put("mbPw", kakaoUserVO.getPw());

		// 중복체크
		int dupleCheck = userService.KakaoMbDuplicationCheck(map);
		System.out.println("dupleCheck ==> " + dupleCheck);
		LoginDomain loginDomain = userService.mbGetId(map);
		System.out.println("mbGetId ==> " + loginDomain);

		// 현재아이피 추출
		String IP = CommonUtils.getClientIP(request);

		int totalcount = userService.mbGetAll();
		LoginDomain loginDomain2 = LoginDomain.builder()
				.mbId(kakaoUserVO.getKakao_account().getEmail() + "_" + kakaoUserVO.getId()).mbPw("1234")
				.mbLevel((totalcount == 0) ? "3" : "2") // 최초가입자를 level 3 admin 부여
				.mbIp(IP).mbUse("Y").build();

		// 사용자 없으면 회원저장
		if (dupleCheck == 0) {
			userService.mbCreate(loginDomain2);

			// session저장
			session.setAttribute("ip", IP);
			session.setAttribute("id", loginDomain2.getMbId());
			session.setAttribute("mbLevel", loginDomain2.getMbLevel());

			List<BoardListDomain> items = uploadService.boardList();
			System.out.println("items=====>" + items);
			mav.addObject("items", items);

			mav.setViewName("board/boardList.html");
		} else {
			// 로그인
			// session저장
			session.setAttribute("ip", IP);
			session.setAttribute("id", loginDomain2.getMbId());
			session.setAttribute("mbLevel", loginDomain2.getMbLevel());

			List<BoardListDomain> items = uploadService.boardList();
			mav.addObject("items", items);
			mav.setViewName("board/boardList.html");

		}

		return mav;
	}

	// 어드민의 멤버추가 & 회원가입
	@PostMapping("create")
	public ModelAndView mbcreate(LoginVO loginVO, HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		ModelAndView mav = new ModelAndView();

		// session 처리
		HttpSession session = request.getSession();

		// 페이지 초기화
		String page = (String) session.getAttribute("page");
		if (page == null)
			page = "1";

		// 중복체크
		Map<String, String> map = new HashMap();
		map.put("mbId", loginVO.getId());
		map.put("mbPw", loginVO.getPw());

		// 중복체크
		int dupleCheck = userService.mbDuplicationCheck(map);
		System.out.println(dupleCheck);

		if (dupleCheck > 0) { // 가입되있으면
			String alertText = "중복이거나 유효하지 않은 접근입니다";
			String redirectPath = "/main";
			System.out.println(loginVO.getAdmin());
			if (loginVO.getAdmin() != null) {
				redirectPath = "/main/mbList?page=" + page;
			}
			CommonUtils.redirect(alertText, redirectPath, response);
		} else {

			// 현재아이피 추출
			String IP = CommonUtils.getClientIP(request);

			// 전체 갯수
			int totalcount = userService.mbGetAll();

			// db insert 준비
			LoginDomain loginDomain = LoginDomain.builder().mbId(loginVO.getId()).mbPw(loginVO.getPw())
					.mbLevel((totalcount == 0) ? "3" : "2") // 최초가입자를 level 3 admin 부여
					.mbIp(IP).mbUse("Y").build();

			// 저장
			userService.mbCreate(loginDomain);

			if (loginVO.getAdmin() == null) { // 'admin'들어있을때는 alert 스킵한다
				// session 저장
				session.setAttribute("ip", IP);
				session.setAttribute("id", loginDomain.getMbId());
				session.setAttribute("mbLevel", (totalcount == 0) ? "3" : "2"); // 최초가입자를 level 3 admin 부여
				mav.setViewName("redirect:/bdList");
			} else { // admin일때
				mav.setViewName("redirect:/mbList?page=1");
			}
		}

		return mav;

	};

	@RequestMapping(value = "board")
	public ModelAndView login(LoginVO loginDTO, HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		// Session 처리
		HttpSession session = request.getSession();
		ModelAndView mav = new ModelAndView();

		// 중복체크
		Map<String, String> map = new HashMap();
		map.put("mbId", loginDTO.getId());
		map.put("mbPw", loginDTO.getPw());

		// 중복체크
		int dupleCheck = userService.mbDuplicationCheck(map);
		System.out.println("dupleCheck ==> " + dupleCheck);
		LoginDomain loginDomain = userService.mbGetId(map);
		System.out.println("mbGetId ==> " + loginDomain);

		if (dupleCheck == 0) {
			String alertText = "없는 아이디이거나 패스워드가 잘못되었습니다. 기입해주세요";
			String redirectPath = "/main/signin";
			CommonUtils.redirect(alertText, redirectPath, response);
			return mav;
		}

		// 현재 아이피 추출
		String IP = CommonUtils.getClientIP(request);

		// session저장
		session.setAttribute("ip", IP);
		session.setAttribute("id", loginDomain.getMbId());
		session.setAttribute("mbLevel", loginDomain.getMbLevel());

		List<BoardListDomain> items = uploadService.boardList();
		System.out.println("items=====>" + items);
		mav.addObject("items", items);

		mav.setViewName("board/boardList.html");

		return mav;
	}

	// 좌측 메뉴 클릭시 보드화면 이동 (로그인된 상태)
	@RequestMapping(value = "bdList")
	public ModelAndView bdList() {
		ModelAndView mav = new ModelAndView();
		List<BoardListDomain> items = uploadService.boardList();
		System.out.println("items ==>" + items);
		mav.addObject("items", items);
		mav.setViewName("board/boardList.html");
		return mav;
	}

	// 대시보드 리스트 보여주기
	@GetMapping("mbList")
	public ModelAndView mbList(HttpServletRequest request) {

		ModelAndView mav = new ModelAndView();

		HttpSession session = request.getSession();
		String page = (String) session.getAttribute("page"); // session에 담고 있는 page 꺼냄
		if (page == null)
			page = "1"; // 없으면 1

		// 클릭페이지 세션에 담아줌
		session.setAttribute("page", page);

		// 페이지네이션
		mav = mbListCall(request); // 리스트만 가져오기

		mav.setViewName("admin/adminList.html");
		return mav;
	};

	// 페이징으로 리스트 가져오기
	public ModelAndView mbListCall(HttpServletRequest request) { // 클릭페이지 널이면
		ModelAndView mav = new ModelAndView();
		// 페이지네이션 쿼리 참고
		// SELECT * FROM login_test.member order by mb_update_at limit 1, 5;
		// {offset}{limit}

		// 전체 갯수
		int totalcount = userService.mbGetAll();
		int contentnum = 10; // 데이터 가져올 갯수

		// 데이터 유무 분기때 사용
		boolean itemsNotEmpty;

		if (totalcount > 0) { // 데이터 있을때

			// itemsNotEmpty true일때만, 리스트 & 페이징 보여주기
			itemsNotEmpty = true;
			// 페이지 표현 데이터 가져오기
			Map<String, Object> pagination = Pagination.pagination(totalcount, request);

			Map map = new HashMap<String, Integer>();
			map.put("offset", pagination.get("offset"));
			map.put("contentnum", contentnum);

			// 페이지별 데이터 가져오기
			List<LoginDomain> loginDomain = userService.mbAllList(map);

			// 모델객체 넣어주기
			mav.addObject("itemsNotEmpty", itemsNotEmpty);
			mav.addObject("items", loginDomain);
			mav.addObject("rowNUM", pagination.get("rowNUM"));
			mav.addObject("pageNum", pagination.get("pageNum"));
			mav.addObject("startpage", pagination.get("startpage"));
			mav.addObject("endpage", pagination.get("endpage"));

		} else {
			itemsNotEmpty = false;
		}

		return mav;
	};

	// 회원가입 화면
	@GetMapping("signin")
	public ModelAndView signIn() throws IOException {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("signin/signin.html");
		return mav;
	}

	// 로그아웃
	@RequestMapping("logout")
	public ModelAndView logout(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		HttpSession session = request.getSession();
		session.invalidate(); // 전체삭제
		mav.setViewName("index.html");
		return mav;
	}

	@RequestMapping("chat")
	public ModelAndView chat() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("chat/chat.html");
		return mav;
	}
}
