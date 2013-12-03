<%@taglib uri="http://java.sun.com/jsp/jstl/core"
          prefix="c" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<jsp:include page="template-top.jsp" />
<link rel="stylesheet" href="css/signup.css" type="text/css"/>
<!-- head -->
<div class="body clearfix">
	<div class="signup">
		<h2>Welcome to Yue's Weird!</h2>
		<form action="register.do" method="post" name="joinus">
			<input type="hidden" name="form" value="register" />
			<div class="item clearfix">
				<label for="email">Email: </label> <input type="text" id="email"
					name="email" value="" />
			</div>
			<div class="item clearfix">
				<label for="fname">First Name: </label> <input type="text"
					id="fname" name="fname" value="" />
			</div>
			<div class="item clearfix">
				<label for="lname">Last Name: </label> <input type="text" id="lname"
					name="lname" value="" />
			</div>
			<div class="item clearfix">
				<label for="tzone">Time Zone: </label> <select name="tzone"
					id="tzone">
					<option value="0">Please Select ...</option>
					<c:forEach var="tzone" items="${ tzones }">
						<option value="${ tzone }">${ tzone }</option>
					</c:forEach>
				</select>
			</div>
			<div class="item clearfix">
				<label for="password">Password: </label> <input type="password"
					name="password" id="password" value="" />
			</div>
			<div class="item1 clearfix">
				<input value="Sign Up" type="submit" class="g-box" name="submit" />
				<input value="Clear" type="reset" class="reset" />
			</div>
		</form>
	</div>
	<div class="signin">
		I Am One Of The Weird. <input type="button" value="Log In Now!"
			class="g-box2 gindex" />
	</div>
</div>
<!-- body -->
<div class="foot">
	<span id="icp">&copy; 2005－2012 yue xing, all rights reserved</span>
</div>
<!-- foot -->
</div>
<!-- wrapper -->
<script type="text/javascript" src="script/weird.js"></script>
<script>
	var form = document.forms[0];
	var email = form.email;
	var email_p = false;
	email.onblur = function() {
		weird
				.get(
						"chkuser.do?email=" + email.value,
						function(resp) {
							if (resp == 0) {
								alert("The email address has been registed\n use another one please!");
								email.focus();
							} else
								email_p = true;
						});
	};
	//more onsubmit?
</script>
</body>
</html>

