<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Add and View Product</title>
<link rel="stylesheet" type="text/css" href="style/common.css" />
</head>
<body>
	<div class="container">
		<ul class="nav">
			<li>Hello, ${user.type } ${ user.email }</li>
			<li><a href="index.jsp">Index</a></li>
			<li><a href="logout.do">Log Out</a></li>
		</ul>
		<br/> If you cannot add the product because of duplication check,
		please click <a href="addproduct.do?force=true">here</a>
		<hr />
		
		<jsp:include page="frag/frag_products.jsp"></jsp:include>

		<form action="addproduct.do" method="post"
			enctype="multipart/form-data" id="product-form">
			<jsp:include page="frag/frag_error.jsp"></jsp:include>
			<jsp:include page="frag/frag_mode.jsp"></jsp:include>

			<input type="hidden" name="brandId" value="" /> <input type="hidden"
				name="categoryId" value="" /> <input type="hidden"
				name="benefitIds" value="" /> <input type="hidden" name="tagIds"
				value="" />

			<c:if test="${ not empty edit and edit == 1}">
				<input type="hidden" name="id" value="${ form.id }" />
			</c:if>
			<table>
				<tr>
					<td><label for="name">Name: </label></td>
					<td><input type="text" id="name" name="name"
						value="<c:if test="${ not empty form }">${ form.name }</c:if>" /></td>
				</tr>

				<c:if test="${ not form.edit }">
					<!-- brand -->

					<tr>
						<td><label for="brand">Brand: </label></td>
						<td><select id="brand" name="brand">
								<c:forEach var="brand" items="${brands}" varStatus="rowCounter">
									<option value="${brand.id }"
										<c:if test="${ not empty form and form.brandId eq brand.id}">selected</c:if>>${brand.name
										}</option>
								</c:forEach>
						</select></td>
					</tr>
				</c:if>


				<!-- cate  -->
				<tr>
					<td><label for="cate">Category: </label></td>
					<td><select id="cate" name="category">
							<c:forEach var="cate" items="${cates}" varStatus="rowCounter">
								<option value="${cate.id }"
									<c:if test="${ not empty form and form.categoryId eq cate.id}">selected</c:if>>${cate.name
									}</option>
							</c:forEach>
					</select></td>
				</tr>

				<!-- benefit multi-select -->
				<tr>
					<td><label for="benefit">Benefits: </label></td>
					<td><select id="benefit" name="benefits" multiple="multiple">
							<c:forEach var="benefit" items="${benefits}"
								varStatus="rowCounter">
								<option value="${benefit.id }">${benefit.name }</option>
							</c:forEach>
					</select></td>
				</tr>

				<!-- tag multi-select -->
				<tr>
					<td><label for="tag">Tags: </label></td>
					<td><select id="tag" name="tags" multiple="multiple">
							<c:forEach var="tag" items="${tags}" varStatus="rowCounter">
								<option value="${tag.id }">${tag.name }</option>
							</c:forEach>
					</select></td>
				</tr>

				<tr>
					<td><label for="price">Price: </label></td>
					<td><input type="text" name="price" id="price"
						value="<c:if test="${ not empty form }">${ form.price }</c:if>" /></td>
				</tr>

				<tr>
					<td><label for="introduction">Introduction: </label></td>
					<td><textarea name="introduction" id="introduction">
							<c:if test="${ not empty form }">${ form.introduction }</c:if>
						</textarea></td>
				</tr>

				<tr>
					<td><label for="image">Image: </label></td>
					<td><input type="file" name="file" id="image" /></td>
				</tr>

				<tr>
					<td><input type="reset" value="reset" /></td>
					<td><input type="submit" value="submit" name="submit" /></td>
				</tr>
			</table>
		</form>

	</div>
	<script type="text/javascript">
		document.getElementById("product-form").onsubmit = function() {

			// brand
			if (this.brand) {
				var bids = this.brand.options;
				for (i = 0; i < bids.length; i++) {
					if (bids[i].selected) {
						this.brandId.value = bids[i].value;
						break;
					}
				}
			}

			// cate
			var cids = this.category.options;
			for (i = 0; i < cids.length; i++) {
				if (cids[i].selected) {
					this.categoryId.value = cids[i].value;
					break;
				}
			}

			// benefits
			var bis = this.benefits.options;
			var bisStr = "";
			for (i = 0; i < bis.length; i++) {
				if (bis[i].selected)
					bisStr += bis[i].value + ',';
			}
			this.benefitIds.value = bisStr;

			// tags			
			tis = this.tags.options;
			var tisStr = "";
			for (i = 0; i < tis.length; i++) {
				if (tis[i].selected)
					tisStr += tis[i].value + ",";
			}
			this.tagIds.value = tisStr;
			return true;
		};
	</script>
</body>
</html>