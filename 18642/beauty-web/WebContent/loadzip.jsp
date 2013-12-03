<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Load a zip file</title>
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
		<form action="loadzip.do" method="post" enctype="multipart/form-data">
			<jsp:include page="frag/frag_error.jsp"></jsp:include>
			<table>
				<tr>
					<td><label for="image">Image: </label></td>
					<td><input type="file" name="file" id="image" /></td>
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