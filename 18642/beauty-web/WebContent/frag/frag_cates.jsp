<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:if test="${ not empty categories }">
	<h3>The Categories</h3>
	<table class="list">
		<tr>
			<th>ID</th>
			<th>Name</th>
			<th>STD FORM</th>
			<th>Count</th>
			<th>Action</th>
		</tr>
		<c:forEach var="category" items="${categories}" varStatus="loop">
			<c:choose>
				<c:when test="${loop.index % 2 == 0}">
					<tr class="even">
				</c:when>
				<c:otherwise>
					<tr>
				</c:otherwise>
			</c:choose>
			<td>${category.id }</td>
			<td>${category.name }</td>
			<td>${category.stdForm }</td>
			<td>${category.productCount }</td>
			<td><c:if test="${ category.owner == user.id || user.admin}">
					<a href="delete.do?type=category&id=${category.id }">Delete</a>
					<a href="addcate.do?edit=true&id=${category.id }">Edit</a>
				</c:if></td>
			</tr>
		</c:forEach>
	</table>
</c:if>