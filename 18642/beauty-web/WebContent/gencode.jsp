<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@page import="java.util.List"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Generate Invite Code</title>
<link rel="stylesheet" type="text/css" href="style/common.css" />
</head>
<body>
	<div class="container">
		<ul class="nav">
			<li>Hello, ${user.type } ${ user.email }</li>
			<li><a href="index.jsp">Index</a></li>
			<li><a href="logout.do">Log Out</a></li>
		</ul>
		<br>
		<hr />
		<form action="gencode.do" method="POST">
			<jsp:include page="frag/frag_error.jsp"></jsp:include>
			<table>
				<tr>
					<td><label for="type">Gen Invite Code For:</label></td>
					<td><label for="admin">Administrator</label><input
						type="radio" id="admin" name="type" value="admin"> <label
						for="retail">Retail </label><input type="radio" id="retail"
						name="type" value="retail"> <label for="manu">Manufacturer</label><input
						type="radio" id="manu" name="type" value="manufacturer"></td>
				</tr>
				<tr>
					<td colspan="1" align="center"><input type="submit"
						name="action" value="Generate Code" /></td>
				</tr>
			</table>
		</form>
	</div>
</body>
</html>