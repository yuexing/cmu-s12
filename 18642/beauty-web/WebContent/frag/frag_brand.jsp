<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:if test="${ not empty brand }">
	<h3>The Brand</h3>
	<table class="list">
		<tr>
			<th>ID</th>
			<th>Name</th>
			<th>STD FORM</th>
			<th>Count</th>
			<th>Picture</th>
			<th>Action</th>
		</tr>
		<tr>
			<td>${brand.id }</td>
			<td>${brand.name }</td>
			<td>${brand.stdForm }</td>
			<td>${brand.productCount }</td>
			<td><img src='view.do?id=${brand.attachmentId}' height='50'
				width='50' /></td>
			<td><c:if test="${ brand.owner == user.id || user.admin}">
					<a href="delete.do?type=brand&id=${brand.id }">Delete</a>
					<a href="addbrand.do?edit=true&id=${brand.id }">Edit</a>
				</c:if></td>
		</tr>
	</table>
</c:if>