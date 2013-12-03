<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>List Database</title>
<link rel="stylesheet" type="text/css" href="style/common.css" />
</head>
<body>
	<div class="container">
		<ul class="nav">
			<li>Hello, ${user.type } ${ user.email }</li>
			<li><a href="index.jsp">Index</a></li>
			<li><a href="logout.do">Log Out</a></li>
		</ul><br/>
		<hr />
		<jsp:include page="frag/frag_error.jsp"></jsp:include>

		<jsp:include page="frag/frag_products.jsp"></jsp:include>

		<jsp:include page="frag/frag_brands.jsp"></jsp:include>

		<jsp:include page="frag/frag_cates.jsp"></jsp:include>

		<jsp:include page="frag/frag_tags.jsp"></jsp:include>

		<jsp:include page="frag/frag_benefits.jsp"></jsp:include>

		<jsp:include page="frag/frag_comments.jsp"></jsp:include>

		<jsp:include page="frag/frag_retails.jsp"></jsp:include>

		<jsp:include page="frag/frag_deals.jsp"></jsp:include>
	</div>
</body>
</html>