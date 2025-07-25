package com.spark.dto;

import java.util.Arrays;

//BoardSearchRequest.java
public class BoardSearchRequest {
 private String boardnum;     // 게시판 번호 ("1", "2", "3")
 private int page = 1;        // 페이지 번호
 private int size = 10;       // 페이지 크기
 private String search = "";  // 검색어
 private String sort = "latest"; // 정렬 조건
 
 // 생성자
 public BoardSearchRequest() {}
 
 public BoardSearchRequest(String boardnum) {
     this.boardnum = boardnum;
 }
 
 // getter, setter
 public String getBoardnum() { return boardnum; }
 public void setBoardnum(String boardnum) { this.boardnum = boardnum; }
 
 public int getPage() { return page; }
 public void setPage(int page) { this.page = Math.max(1, page); }
 
 public int getSize() { return size; }
 public void setSize(int size) { this.size = Math.min(Math.max(1, size), 50); }
 
 public String getSearch() { return search; }
 public void setSearch(String search) { this.search = search != null ? search.trim() : ""; }
 
 public String getSort() { return sort; }
 public void setSort(String sort) { 
     this.sort = Arrays.asList("latest", "oldest", "views").contains(sort) ? sort : "latest";
 }
}