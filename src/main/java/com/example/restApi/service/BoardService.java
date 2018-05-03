package com.example.restApi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.restApi.dao.BoardDao;
import com.example.restApi.dto.BoardDto;




//이전에 MVC 패턴에서 사용했던 Command와 비슷하게 비즈니스 로직을 실행하는 역할을 함
@Service("com.example.restApi.service.BoardService")
public class BoardService {

	@Autowired
	BoardDao mBoardDao;

	public List<BoardDto> boardDivisionService(int startNum, int getLimit) throws Exception{
		return mBoardDao.boardDivision(startNum, getLimit);
	}
}
