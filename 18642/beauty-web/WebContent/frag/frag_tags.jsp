<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:if test="${ not empty tags }">
	<h3>The Tags</h3>
	<table class="list">
		<tr>
			<th>ID</th>
			<th>Name</th>
			<th>STD FORM</th>
			<th>Count</th>
			<th>Action</th>

		</tr>
		<c:forEach var="tag" items="${tags}" varStatus="loop">
			<c:choose>
				<c:when test="${loop.index % 2 == 0}">
					<tr class="even">
				</c:when>
				<c:otherwise>
					<tr>
				</c:otherwise>
			</c:choose>
			<td>${tag.id }</td>
			<td>${tag.name }</td>
			<td>${tag.stdForm }</td>
			<td>${tag.productCount }</td>
			<td><c:if test="${ tag.owner == user.id || user.admin}">
					<a href="delete.do?type=tag&id=${tag.id }">Delete</a>
					<a href="addtag.do?edit=true&id=${tag.id }">Edit</a>
				</c:if></td>
			</tr>
		</c:forEach>
	</table>
</c:if>