package com.example.restApi.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.example.restApi.dto.BoardDto;



//@Repository : DB에 접근하는 클래스임을 명시 
@Repository("com.example.restApi.dao.BoardDao")
@Mapper // mapper를 선언해주어야 Bean 객체로 사용할 수 있다
public interface BoardDao {

	//게시글 개수  
	public int boardCount() throws Exception;

	//게시글을 분할해서 가져오기
	public List<BoardDto> boardDivision(@Param("startNum") int startNum, @Param("limit") int limit) throws Exception;

}
