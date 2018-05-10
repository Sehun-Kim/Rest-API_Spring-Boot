package com.example.restApi.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.restApi.service.BoardService;

@RestController
@RequestMapping("/csv")
public class RestAPIController {
	
	// @Autowired는 타입으로 bean 객체를 찾아 맵핑하고 @Resource는 이름으로 먼저 매핑한다.
	@Autowired
	BoardService service;
	
	// 요청을 Post방식으로 request의 body를 MultiValueMap 형태로 받는다.
	@RequestMapping(value="/record", method = RequestMethod.POST)
	public String recard(@RequestBody MultiValueMap<String, String> request) throws Exception{
		
		// 요청 받은 내용을 변수에 저장
		String fileName = request.getFirst("fileName");
		int startNum = Integer.parseInt(request.getFirst("startNum"))-1;
		int lastNum = Integer.parseInt(request.getFirst("lastNum"));
		
		// 기본 응답방식은 JSON이기 때문에 브라우저에서 확인할때는 JSON 포맷으로 확인할 수 있음
		return service.csvCheckService(fileName, startNum, lastNum);
	}

}
