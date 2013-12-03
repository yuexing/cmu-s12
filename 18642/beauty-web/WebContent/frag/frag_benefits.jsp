<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:if test="${ not empty benefits }">
	<h3>Benefits</h3>
	<table class="list">
		<tr>
			<th>ID</th>
			<th>Name</th>
			<th>STD FORM</th>
			<th>Count</th>
			<th>Action</th>
		</tr>
		<c:forEach var="benefit" items="${benefits}" varStatus="loop">
			<c:choose>
				<c:when test="${loop.index % 2 == 0}">
					<tr class="even">
				</c:when>
				<c:otherwise>
					<tr>
				</c:otherwise>
			</c:choose>
			<td>${benefit.id }</td>
			<td>${benefit.name }</td>
			<td>${benefit.stdForm }</td>
			<td>${benefit.productCount }</td>
			<td><c:if test="${ benefit.owner == user.id || user.admin }">
					<a href="delete.do?type=benefit&id=${benefit.id }">Delete</a>
					<a href="addbenefit.do?edit=true&id=${benefit.id }">Edit</a>
				</c:if></td>
			</tr>
		</c:forEach>
	</table>
</c:if>