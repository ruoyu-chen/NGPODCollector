<%@page import="cn.edu.bistu.ngpod.utils.ConfigReader"%>
<%@ page import="org.apache.lucene.document.Document"%>
<%@ page import="cn.edu.bistu.ngpod.index.LuceneSearch"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>国家地理每日一图检索系统</title>
<link rel='stylesheet' id='css' href='style.css' type='text/css' media='all'/>
</head>
<body>
	<% 
	LuceneSearch search = LuceneSearch.getInstance();
	Document doc = search.getRandomPod();
	if(doc != null){
		String photo = doc.get("photo");
		String title = doc.get("title");
		String pageId = doc.get("pageId");
		String url = "http://photography.nationalgeographic.com/photography/photo-of-the-day/"+pageId;
		%>
		<a href="<%=url%>"><img class="aligncenter" width="600" align="middle" alt="<%=title%>" src="photos/<%=photo%>" ></a>
		<%
	}
	%>
<h2 align="center">国家地理每日一图采集检索系统</h2>
<p>
<div align="center">
<form action="search" method="post">
		检索关键字：<input type="text" name=query />
		起始时间：<input type="text" name="start" />
		结束时间：<input type="text" name="end" />
		<!-- 默认选择第一页 -->
		<input type="hidden" name="page" value="1">
		<input type="submit" value="submit" title="提交" />
	</form>
</div>
</body>
</html>