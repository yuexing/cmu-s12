<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:if test="${ not empty category }">
	<h3>The Category</h3>
	<table class="list">
		<tr>
			<th>ID</th>
			<th>Name</th>
			<th>STD FORM</th>
			<th>Count</th>
			<th>Action</th>
		</tr>
		<tr>
			<td>${category.id }</td>
			<td>${category.name }</td>
			<td>${category.stdForm }</td>
			<td>${category.productCount }</td>
			<td><c:if test="${ category.owner == user.id || user.admin}">
					<a href="delete.do?type=category&id=${category.id }">Delete</a>
					<a href="addcate.do?edit=true&id=${category.id }">Edit</a>
				</c:if></td>
		</tr>
	</table>
</c:if>