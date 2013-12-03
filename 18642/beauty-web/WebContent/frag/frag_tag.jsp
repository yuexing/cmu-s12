<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:if test="${ not empty tag }">
	<h3>The Tag</h3>
	<table class="list">
		<tr>
			<th>ID</th>
			<th>Name</th>
			<th>STD FORM</th>
			<th>Count</th>
			<th>Action</th>
		</tr>
		<tr>
			<td>${tag.id }</td>
			<td>${tag.name }</td>
			<td>${tag.stdForm }</td>
			<td>${tag.productCount }</td>
			<td><c:if test="${ tag.owner == user.id || user.admin}">
					<a href="delete.do?type=tag&id=${tag.id }">Delete</a>
					<a href="addtag.do?edit=true&id=${tag.id }">Edit</a>
				</c:if></td>
		</tr>
	</table>
</c:if>