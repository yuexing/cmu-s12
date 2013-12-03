<%@taglib uri="http://java.sun.com/jsp/jstl/core"
          prefix="c" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<jsp:include page="template-top.jsp" />
<link rel="stylesheet" href="css/signup.css" type="text/css"/>
<!-- head -->
<div class="body clearfix">
	<h2>${ user.fname } ${ user.lname }</h2>
	<div class="signup">
		<form action="profile.do" method="post" enctype="multipart/form-data" name="edit">
		<input type="hidden" name="form" value="edit"/>
			<div class="item clearfix">
				<label for="email">Email: </label> <input type="text" name="email"
					value="${ user.email }" id="email" />
			</div>
			<div class="item clearfix">
				<label for="fname">First Name: </label> <input type="text"
					name="fname" value="${ user.fname }" id="fname" />
			</div>
			<div class="item clearfix">
				<label for="lname">Last Name: </label> <input type="text"
					name="lname" value="${ user.lname }" id="lname" />
			</div>
			<div class="item clearfix">
				<label for="tzone">Time Zone: </label> <select name="tzone"
					id="tzone">
					<c:forEach var="tzone" items="${ tzones }">
					<option value="${ tzone }"
					<c:if test = "${ tzone eq user.tzone }">
					selected="selected"
					</c:if>
					>${ tzone }</option>
				</c:forEach>
				</select>
			</div>
			<fieldset>
				<legend>Profile Image </legend>
				<label for="image">Picture: </label> <input type="file" name="file"
					id="image" />
				<div class="description">Your virtual face or picture.
					Pictures larger than 1024x1024 pixels will be scaled down. Please
					include a recognizable head and shoulder shot for your profile.</div>
			</fieldset>
			<div class="item clearfix">
				<label for="signature">Signature: </label>
				<textarea name="signature" rows="8" cols="40" id="signature">${ user.signature }</textarea>
			</div>
			<div class="item1 clearfix">
				<input value="Sign Up" type="submit" class="g-box" /> <input
					value="Clear" type="reset" class="reset" />
			</div>
		</form>
	</div>
	<div class="signin">
		Nothing To Edit Here. <input type="button" value="Go Back Home!"
			class="g-box2 ghome" />
	</div>
</div>
<!-- body -->
<jsp:include page="template-bottom.jsp" />