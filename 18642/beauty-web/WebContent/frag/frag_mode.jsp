<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div style="font-size: medium; color: red;">
	<c:if test="${form.force }">Force mode</c:if>
	<br />
	<c:if test="${form.edit }">Edit mode</c:if>
</div>
<input type="hidden" name="edit" value="${ form.edit }" />
<input type="hidden" name="force" value="${form.force }" />
<input type="hidden" name="id" value="${ form.id }" />