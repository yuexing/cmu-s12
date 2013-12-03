<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" import="beauty.web.model.*"
	import="beauty.web.controller.*"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Links available</title>
<link rel="stylesheet" type="text/css" href="style/common.css" />
</head>
<body>
	<div class="container">
		<jsp:include page="frag/frag_error.jsp"></jsp:include>
		<ul class="nav">
			<li>Hello, ${user.type } ${ user.email }</li>
			<li><a href="logout.do">Log Out</a></li>
		</ul>
		<br />
		<ul>
			<%
				User u = (User) request.getSession().getAttribute("user");
				if (u.isAdmin()) {
			%>
			<li><a href="addbrand.do">Add Brand</a></li>
			<li><a href="addcate.do">Add Category</a></li>
			<li><a href="addtag.do">Add Tag</a></li>
			<li><a href="addbenefit.do">Add Benefit</a></li>
			<li><a href="addproduct.do">Add Product</a></li>
			<li><a href="loadzip.do">Load A product</a></li>
			<%
				if (Action.enableInvite) {
			%>
			<li><a href="gencode.do">Generate Invite Code</a></li>
			<%
				}
			%>
			<li><a href="list.do">List</a></li>
			<%
				} else if (u.isRetail()) {
					// only view allowed
			%>
			<li><a href="adddeal.do?rid=1">Add Deal</a></li>
			<li><a href="addretail.do">Add Retail </a></li>
			<li><a href="list.do">List</a></li>
			<%
				} else if (u.isManu()) {
			%>
			<li><a href="addbrand.do">Add Brand</a></li>
			<li><a href="addcate.do">Add Category</a></li>
			<li><a href="addtag.do">Add Tag</a></li>
			<li><a href="addbenefit.do">Add Benefit</a></li>
			<li><a href="addproduct.do">Add Product</a></li>
			<li><a href="loadzip.do">Load A product</a></li>
			<li><a href="list.do">List</a></li>
			<%
				}
			%>

		</ul>
	</div>
</body>
</html>