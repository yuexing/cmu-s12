<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:if test="${ not empty deals }">
	<h3>The Deals</h3>
	<table class="list">
		<tr>
			<th>ID</th>
			<th>Content</th>
			<th>Retail</th>
			<th>Retail Name</th>
			<th>Action</th>
		</tr>
		<c:forEach var="deal" items="${deals}" varStatus="loop">
			<c:choose>
				<c:when test="${loop.index % 2 == 0}">
					<tr class="even">
				</c:when>
				<c:otherwise>
					<tr>
				</c:otherwise>
			</c:choose>
			<td>${deal.id }</td>
			<td>${deal.content }</td>
			<td><a href="detail.do?type=retail&id=${deal.retailId }">${
					deal.retailId }</a></td>
			<td>${deal.retailName }</td>
			<td><c:if test="${ deal.owner == user.id || user.admin}">
					<a href="delete.do?type=deal&id=${deal.id }">Delete</a>
					<a href="adddeal.do?edit=true&id=${deal.id }">Edit</a>
				</c:if></td>
			</tr>
		</c:forEach>
	</table>
</c:if>