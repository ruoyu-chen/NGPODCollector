<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>国家地理每日一图检索系统</title>
</head>
<body>
	<form action="search" method="post">
		检索关键字：<input type="text" name=query />
		起始时间：<input type="text" name="start" />
		结束时间：<input type="text" name="end" />
		<input type="submit" value="submit" title="提交" />
	</form>
</body>
</html>