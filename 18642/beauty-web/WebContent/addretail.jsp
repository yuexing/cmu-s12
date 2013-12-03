<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Add A Retail And View All Retails</title>
<link rel="stylesheet" type="text/css" href="style/common.css" />
</head>
<body>

	<div class="container">
		<ul class="nav">
			<li>Hello, ${user.type } ${ user.email }</li>
			<li><a href="index.jsp">Index</a></li>
			<li><a href="logout.do">Log Out</a></li>
		</ul>
		<br> If you don't like the recommended address, please delete it
		and click <a href="addretail.do?force=true">here</a> to add your
		original address
		<hr />
		<jsp:include page="frag/frag_retails.jsp"></jsp:include>

		<c:if test="${ not user.added}">
			<form action="addretail.do" method="post">
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
						<td><label for="url">URL: </label></td>
						<td><input type="text"
							value="<c:if test="${ not empty form }">${ form.url }</c:if>"
							id="url" name="url" /></td>
					</tr>
					<tr>
						<td><label for="streetAddress">Street Address: </label></td>
						<td><input type="text"
							value="<c:if test="${ not empty form }">${ form.streetAddress }</c:if>"
							id="streetAddress" name="streetAddress" /></td>
					</tr>
					<tr>
						<td><label for="city">City: </label></td>
						<td><input type="text"
							value="<c:if test="${ not empty form }">${ form.city }</c:if>"
							id="city" name="city" /></td>
					</tr>
					<tr>
						<td><label for="state">State: </label></td>
						<td><input type="text"
							value="<c:if test="${ not empty form }">${ form.state }</c:if>"
							id="state" name="state" /></td>
					</tr>
					<tr>
						<td><label for="country">Country: </label></td>
						<td><input type="text"
							value="<c:if test="${ not empty form }">${ form.country }</c:if>"
							id="country" name="country" /></td>
					</tr>
					<tr>
						<td><label for="postcode">Post Code: </label></td>
						<td><input type="text"
							value="<c:if test="${ not empty form }">${ form.postcode }</c:if>"
							id="postcode" name="postcode" /></td>
					</tr>
					<tr>
						<td><label for="phoneNumber">Phone Number: </label></td>
						<td><input type="text"
							value="<c:if test="${ not empty form }">${ form.phoneNumber }</c:if>"
							id="phoneNumber" name="phoneNumber" /></td>
					</tr>
					<tr>
						<td><input type="reset" value="reset" /></td>
						<td><input type="submit" value="submit" name="submit" /></td>
					</tr>
				</table>
			</form>
		</c:if>
	</div>


</body>
</html>