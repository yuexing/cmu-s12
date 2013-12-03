<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Add A Tag and View All Tags</title>
<link rel="stylesheet" type="text/css" href="style/common.css" />
</head>
<body>

	<div class="container">
		<ul class="nav">
			<li>Hello, ${user.type } ${ user.email }</li>
			<li><a href="index.jsp">Index</a></li>
			<li><a href="logout.do">Log Out</a></li>
		</ul>
		<br /> If you cannot add the tag because of duplication check, please
		click <a href="addtag.do?force=true">here</a>
		<hr />
		<jsp:include page="frag/frag_tags.jsp"></jsp:include>
		<jsp:include page="frag/frag_mode.jsp"></jsp:include>

		<form action="addtag.do" method="post" enctype="multipart/form-data">
			<jsp:include page="frag/frag_error.jsp"></jsp:include>
			<jsp:include page="frag/frag_mode.jsp"></jsp:include>
			<table>
				<tr>
					<td><label for="name">Name: </label></td>
					<td><input type="text"
						value="<c:if test="${ not empty form }">${ form.name }</c:if>"
						id="name" name="name" /></td>
				</tr>
				<tr>
					<td><input type="reset" value="reset" /></td>
					<td><input type="submit" value="submit" name="submit" /></td>
				</tr>
			</table>
		</form>

	</div>

</body>
</html>