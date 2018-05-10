package com.example.restApi.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.restApi.dao.BoardDao;
import com.example.restApi.dto.BoardDto;


//비즈니스 로직을 실행하는 역할을 함
@Service
public class BoardService {

	// csvDirPath에 properties에 만든 property 주입
	@Value("${csv.download.directory}")
	String csvDirPath;

	@Autowired
	BoardDao mBoardDao;

	// Board Table에 레코드가 몇개 있는지 반환해주는 메소드
	public int boardCount() throws Exception {
		return mBoardDao.boardCount();
	}

	// Board Table의 레코드를 분할해서 가져오는 메소드
	public List<BoardDto> boardDivisionService(int startNum, int getLimit) throws Exception{
		return mBoardDao.boardDivision(startNum, getLimit);
	}

	// 요청이 정확한지 아닌지 확인하는 메소드
	public String csvCheckService(String fileName, int startNum, int lastNum) throws Exception {

		StringBuffer sb = new StringBuffer();

		if(startNum < 0 || lastNum > boardCount()) {
			if(startNum < 0) {
				sb.append(startNum + " must not be less than 0");
			}else {
				sb.append(lastNum + " larger than DB size");
			}
			sb.append(", Http Status :202 Error");
		}else {
			File file = new File(csvDirPath + "/" + fileName + ".csv");

			if(!file.exists()) {
				// 파일 생성
				String column = "bNo,subject,content,writer,reg_date\n"; // table 컬럼
				createCsv(fileName, column, csvDirPath); // csvDirPath는 application.properties에 입력하여 주입받은 경로를 가지고 있다.
				writeFile(fileName, startNum, lastNum);
				
				sb.append(fileName + ": not exist!," + "Http Status :200 OK");
			}else {
				writeFile(fileName, startNum, lastNum);
				
				sb.append(fileName + ": exist!," + "Http Status :200 OK");
			}
		}
		return sb.toString();
	}

	// 저장된 경로에 csv파일을 생성하는 메소드
	public void createCsv(String title, String column, String filepath) {
		try {
			// BufferedWriter 객체를 생성 
			FileWriter fileWriter = new FileWriter(filepath + "/" + title + ".csv", true);
			fileWriter.write(column); // 컬럼 설정

			fileWriter.flush();
			fileWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 서버에 저장된 csv파일에 request로 받은 startNum부터 lastNum까지의 데이터를 limit 만큼씩 읽어와 csv파일에 쓰는 메소드
	private void writeFile(String fileName, int startNum, int lastNum) throws Exception {

		// DB에 접근할때마다 가져올 레코드를 제한함
		int limit = 100;
		if(lastNum - startNum < limit) limit = lastNum - startNum; // 처음부터 100보다 작은 값이 들어올 수 있기때문에

		// 저장된 파일에 이어쓰기
		while(true) {
			List<BoardDto> tmpList = writeCsv(fileName, startNum, limit);

			if(tmpList.size() == 0 || lastNum == startNum + tmpList.size()) {
				break;
			}else {
				startNum += tmpList.size();
				if(lastNum - startNum < limit) limit = lastNum - startNum;
			}
		}
	}

	// 내 서버에 있는 csv 파일에 이어서 레코드를 입력하는 메소드
	public List<BoardDto> writeCsv(String title, int startNum, int limit) throws Exception {
		List<BoardDto> tmpList = boardDivisionService(startNum, limit); // Service 객체로 limit의 크기만큼 레코드를 가져옴
		try {
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(csvDirPath+"/"+title+".csv", true));
			bufferedWriter.write(setContent(tmpList));
			bufferedWriter.flush();
			bufferedWriter.close();

		}catch (Exception e) {
			e.printStackTrace();
		}
		return tmpList;
	}

	// board의 데이터를 csv파일에 옮겨주는 메소드
	private String setContent(List<BoardDto> dtoList) {
		StringBuffer sb = new StringBuffer();

		for(int i=0; i<dtoList.size(); i++) {
			int bId = dtoList.get(i).getbNo();
			String subject = dtoList.get(i).getSubject();
			String content = dtoList.get(i).getContent();
			String writer = dtoList.get(i).getWriter();
			String reg_date = dtoList.get(i).getReg_date().toString();

			sb.append(bId + "," + subject + "," + content + "," + writer + "," + reg_date + ",\n");
		}

		return sb.toString();
	}

	// 저장된 파일을 다운로드 받게 해주는 메소드
	public void download(HttpServletRequest request, HttpServletResponse response, String fileName) {
		try{
			InputStream in = null; // inputStream 
			OutputStream os = null; // outputStream
			File file = null;
			boolean skip = false;
			String client = "";

			//파일을 읽어 스트림에 담기  
			try{
				file = new File(csvDirPath, fileName);
				in = new FileInputStream(file);
			} catch (FileNotFoundException fe) {
				skip = true;
			}

			client = request.getHeader("User-Agent");

			//파일 다운로드 헤더 지정 
			response.reset();
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Description", "JSP Generated Data");

			if (!skip) { // skip이 false일 때
				// IE
				if (client.indexOf("MSIE") != -1) {
					response.setHeader("Content-Disposition", "attachment; filename=\""
							+ java.net.URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "\\ ") + "\"");
					// IE 11 이상.
				} else if (client.indexOf("Trident") != -1) {
					response.setHeader("Content-Disposition", "attachment; filename=\"" + java.net.URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "\\ ") + "\"");
				} else {
					// 한글 파일명 처리
					response.setHeader("Content-Disposition", "attachment; filename=\"" + new String(fileName.getBytes("UTF-8"), "ISO8859_1") + "\"");
					response.setHeader("Content-Type", "application/octet-stream; charset=utf-8");
				}
				response.setHeader("Content-Length", "" + file.length());
				os = response.getOutputStream();
				byte b[] = new byte[(int) file.length()];
				int leng = 0;
				while ((leng = in.read(b)) > 0) {
					os.write(b, 0, leng);
				}
			} else {
				response.setContentType("text/html;charset=UTF-8");
				System.out.println("<script language='javascript'>alert('파일을 찾을 수 없습니다');history.back();</script>");
			}
			in.close();
			os.close();
		} catch (Exception e) {
			System.out.println("ERROR : " + e.getMessage());
		}
	}

}
