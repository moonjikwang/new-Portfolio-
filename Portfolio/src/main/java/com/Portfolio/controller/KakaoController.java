package com.Portfolio.controller;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Portfolio.dto.MemberDTO;
import com.Portfolio.service.MemberService;

import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
public class KakaoController {

	private final MemberService kakaoLoginService;
	// 1번 카카오톡에 사용자 코드 받기(jsp의 a태그 href에 경로 있음)
	@RequestMapping(value = "/oauth", method = RequestMethod.GET)
	public String kakaoLogin(@RequestParam(value = "code", required = false) String code,HttpServletRequest request,RedirectAttributes redirectAttributes) throws Throwable {

		// 코드확인
		System.out.println("code:" + code);
		// 사용자코드로 엑세스 토큰 발급받기
		String access_Token = kakaoLoginService.getAccessToken(code);
		//엑세스 토큰으로 사용자 정보확인
		HashMap<String, Object> userInfo = kakaoLoginService.getUserInfo(access_Token);
		//이메일 검증 하여 기존회원여부 확인
		MemberDTO member = kakaoLoginService.findByEmail("kakao_"+userInfo.get("nickname").toString());
		HttpSession session = request.getSession();
		if(member != null) {
		session.setAttribute("userInfo", member);
		}else {
			MemberDTO dto = MemberDTO.builder().email("kakao_"+userInfo.get("nickname").toString()).profileImg(userInfo.get("profileImg").toString()).name(userInfo.get("nickname").toString()).build();
			kakaoLoginService.register(dto);
			session.setAttribute("userInfo", dto);
		} 
		return "redirect:index";

	}
	@GetMapping("sessionOut")
	public String sessionOut(HttpServletRequest req) {
		HttpSession session = req.getSession();
		session.invalidate();
		return "redirect:index";
	}
	
	@GetMapping("myPage")
	public void myPage() {
		
	}
}