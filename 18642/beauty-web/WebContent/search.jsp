<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Seach</title>
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
		<form action="search.do" method="post">
			<jsp:include page="frag/frag_error.jsp"></jsp:include>
			<table class="list">
				<tr>
					<td><label for="rate">Query: </label></td>
					<td><input type="text"
						value="<c:if test="${ not empty form }">${ form.q }</c:if>"
						name="q" /></td>
				</tr>
				<tr>
					<td><label for="type">Category: </label><input type="radio"
						id="type" name="type" value="category" /></td>
					<td><c:forEach var="cate" items="${cates}"
							varStatus="rowCounter">
							<label for="cate-${cate.id }">${cate.name }: </label>
							<input type="radio" name="id" value="${cate.id }"
								id="cate-${cate.id }" />
						</c:forEach></td>
				</tr>
				<tr>
					<td><label for="type">Brand: </label><input type="radio"
						id="type" name="type" value="brand"></td>
					<td><c:forEach var="brand" items="${brands}"
							varStatus="rowCounter">
							<label for="brand-${brand.id }">${brand.name }: </label>
							<input type="radio" name="id" value="${brand.id }"
								id="brand-${brand.id }" />
						</c:forEach></td>
				</tr>
				<tr>
					<td><label for="type">Benefit: </label><input type="radio"
						id="type" name="type" value="benefit"></td>
					<td><c:forEach var="benefit" items="${benefits}"
							varStatus="rowCounter">
							<label for="benefit-${benefit.id }">${benefit.name }: </label>
							<input type="radio" name="id" value="${benefit.id }"
								id="benefit-${benefit.id }" />
						</c:forEach></td>
				</tr>
				<tr>
					<td><label for="type">Tag: </label><input type="radio"
						id="type" name="type" value="tag"></td>
					<td><c:forEach var="tag" items="${tags}"
							varStatus="rowCounter">
							<label for="tag-${tag.id }">${tag.name }: </label>
							<input type="radio" name="id" value="${tag.id }"
								id="tag-${tag.id }" />
						</c:forEach></td>
				</tr>

				<tr>
					<td><label for="type">None: </label><input type="radio"
						id="type" name="type" value="none"></td>
					<td></td>
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