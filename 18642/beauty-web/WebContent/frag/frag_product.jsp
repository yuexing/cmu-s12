<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:if test="${ not empty product }">
	<h3>The Product</h3>
	<table class="list">
		<tr>
			<th>ID</th>
			<th>Name</th>
			<th>STD FORM</th>
			<th>Brand</th>
			<th>Category</th>
			<!-- <th>Benefit</th> -->
			<th>Comment Num</th>
			<th>Rate Num</th>
			<th>Rate</th>
			<th>Price</th>
			<th>Picture</th>
			<th>Action</th>
		</tr>
		<tr>
			<td>${product.id }</td>
			<td>${product.name }</td>
			<td>${product.stdForm }</td>
			<td>${product.brandId }</td>
			<td>${product.categoryId }</td>
			<td>${product.commentNum }</td>
			<td>${product.rateNum }</td>
			<td>${product.rate }</td>
			<td>${product.price }</td>
			<td><img src='view.do?id=${product.attachmentId}' height='50'
				width='50' /></td>
			<td><c:if test="${ product.owner == user.id || user.admin}">
					<a href="delete.do?type=product&id=${product.id }">Delete</a>
					<a href="addproduct.do?edit=true&id=${product.id }">Edit</a>
				</c:if> <c:if test="user.retail">
					<c:choose>
						<c:when test="${ not added[loop.index] }">
							<a href="addpr.do?pid=${ product.id }">Bind</a>
						</c:when>
						<c:otherwise>
							<a href="addpr.do?del=1&pid=${ product.id }">Release</a>
						</c:otherwise>
					</c:choose>
				</c:if> </td>
		</tr>
	</table>
</c:if>