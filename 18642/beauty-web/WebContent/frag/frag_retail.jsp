<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:if test="${ not empty retail }">
	<h3>The Retail</h3>
	<table class="list">
		<tr>
			<th>ID</th>
			<th>Name</th>
			<th>URL</th>
			<th>Street Add</th>
			<th>City</th>
			<th>State</th>
			<th>Country</th>
			<th>formatted_address</th>
			<th>Post Code</th>
			<th>Phone Number</th>
			<th>Action</th>
		</tr>

		<tr>
			<td>${retail.id }</td>
			<td>${retail.name }</td>
			<td>${retail.url }</td>
			<td>${retail.streetAddress }</td>
			<td>${retail.city }</td>
			<td>${retail.state }</td>
			<td>${retail.country }</td>
			<td>${retail.formatted_address }</td>
			<td>${retail.postcode }</td>
			<td>${retail.phoneNumber }</td>
			<td><c:if test="${ retail.owner == user.id || user.admin}">
			<a href="delete.do?type=retail&id=${retail.id }">Delete</a>
			<a href="addretail.do?edit=true&id=${retail.id }">Edit</a>
			</c:if></td>
		</tr>
	</table>
</c:if>