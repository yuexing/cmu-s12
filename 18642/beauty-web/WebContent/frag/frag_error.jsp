<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:forEach var="error" items="${errors}">
	<div style="font-size: medium; color: red;">${error}</div>
</c:forEach>