<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Add a Rate</title>
<link rel="stylesheet" type="text/css" href="style/common.css" />
</head>
<body>
	<div class="container">
		<ul class="nav">
			<li>Hello, ${user.type } ${ user.email }</li>
			<li><a href="index.jsp">Index</a></li>
			<li><a href="logout.do">Log Out</a></li>
		</ul>
		<br /> You ' re Rating Product #${ product.id } ${ product.name }
		<hr />
		<form action="addrate.do" method="post" id="product-form">
			<jsp:include page="frag/frag_error.jsp"></jsp:include>
			<input type="hidden" name="productId" value="${ product.id }" />
			<table>
				<tr>
					<td><label for="rate">Rate: </label></td>
					<td><label for="rate1">1: </label><input type="radio"
						id="rate1" name="thisRate" value="1"> <label for="rate2">2:
					</label><input type="radio" id="rate2" name="thisRate" value="2"> <label
						for="rate3">3: </label><input type="radio" id="rate3"
						name="thisRate" value="3"> <label for="rate4">4: </label><input
						type="radio" id="rate4" name="thisRate" value="4"> <label
						for="rate5">5: </label><input type="radio" id="rate5"
						name="thisRate" value="5"></td>
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