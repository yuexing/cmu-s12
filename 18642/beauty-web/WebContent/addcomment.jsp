<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Add and View Comment</title>
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
		<jsp:include page="frag/frag_comments.jsp"></jsp:include>

		<form action="addcomment.do" method="post"
			enctype="multipart/form-data" id="add-comment">
			<jsp:include page="frag/frag_error.jsp"></jsp:include>
			<jsp:include page="frag/frag_mode.jsp"></jsp:include>
			<input type="hidden" name="pid" value="${ form.pid }" /> <input
				type="hidden" name="userId" value="${ form.userId }" /> <input
				type="hidden" name="replyId" value="${ form.replyId }" /> <input
				type="hidden" name="type" value="${ form.type }" /> <input
				type="hidden" name="timestamp" value="" />

			<table>
				<tr>
					<td><label for="content">Content: </label></td>
					<td><textarea id="content" name="content">${form.content }</textarea></td>
				</tr>

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
	<script type="text/javascript">
		document.getElementById("add-comment").onsubmit = function() {
			this.timestamp.value = new Date().getTime();
			return true;
		};
	</script>
</body>
</html>