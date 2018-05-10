# Spring Boot Rest API
#### Rest API

###### Tomcat 9.0 
###### DB : MySQL
###### Spring Boot : 2.0.1
###### Mybatis : 3.2.8

### ver 1.0
- 게시글이 저장된 DB로부터 데이터를 읽어옴
- POST 방식으로 데이터를 읽어올 시작점과 끝을 요청 받음
- JSON 포맷으로 데이터 반환

### ver 2.0
- Client로부터 CSV파일로 만들 DB의 범위를 요청받음(Post 방식)
- 요청이 제대로된 요청인지 아닌지 text형태로 응답함
- 지정된 folder에 CSV 파일을 생성해줌
