<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:if test="${ not empty comments }">
	<h3>The Comments</h3>
	<table class="list">
		<tr>
			<th>ID</th>
			<th>content</th>
			<th>date</th>
			<th>type</th>
			<th>product</th>
			<th>replyId</th>
			<th>Picture</th>
			<th>Action</th>
		</tr>
		<c:forEach var="comment" items="${comments}" varStatus="rowCounter">
			<c:choose>
				<c:when test="${rowCounter.count % 2 == 0}">
					<tr class="even">
				</c:when>
				<c:otherwise>
					<tr>
				</c:otherwise>
			</c:choose>
			<td>${comment.id }</td>
			<td>${comment.content }</td>
			<td><script>document.write(new Date(${comment.timestamp }).toLocaleString());</script></td>
			<td><c:choose>
					<c:when test="${ comment.origin }">
					original
					</c:when>
					<c:otherwise>
					reply
					</c:otherwise>
				</c:choose></td>
			<td><a href="detail.do?type=product&id=${comment.productId }">${comment.productId
					}</a></td>
			<td><c:choose>
					<c:when test="${ comment.replyId != -1 }">
						<a href="detail.do?type=comment&id=${comment.replyId }">${comment.replyId
							}</a>
					</c:when>
					<c:otherwise>
					</c:otherwise>
				</c:choose></td>
			<td><c:choose>
					<c:when test="${ comment.attachmentId != -1 }">
						<img src='view.do?id=${comment.attachmentId}' height='50'
							width='50' />
					</c:when>
					<c:otherwise>
					</c:otherwise>
				</c:choose></td>
			<td><c:set value="original" var="orig" scope="request" /> <c:if
					test="${comment.origin }">
					<a
						href="addcomment.do?userId=${ user.id }&replyId=${comment.id }&pid=${comment.productId}">Reply</a>
				</c:if> <c:if test="${ comment.userId == user.id || user.admin}">
					<a href="delete.do?type=comment&id=${comment.id }">Delete</a>
					<a href="addcomment.do?edit=true&id=${comment.id }">Edit</a>
				</c:if></td>
			</tr>
		</c:forEach>
	</table>
</c:if>