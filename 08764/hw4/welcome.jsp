<%@taglib uri="http://java.sun.com/jsp/jstl/core"
          prefix="c" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<jsp:include page="template-top.jsp" />
<link rel="stylesheet" href="css/index.css" type="text/css" />
<!-- head -->
<div class="body">
	<div class="upper clearfix">
		<div class="grey rr-box p20 signup">
			<h2>Yue's Weird</h2>
			<p>
				<b>654 Cities </b> <b>${ count } Chicks </b> <b>2,600 Groups </b> <br />
				Create Weird, Share Weird, Enjoy Weird <br /> 
				<c:if test="${ empty user }">
				<input type="button"
					value="Join US!" class="g-box2 joinus" />
				</c:if>
			</p>
		</div>
		<form action="login.do" method="post" class="grey lr-box p20 signin">
			<input type="hidden" name="form" value="login" />
			<c:forEach var="error" items="${errors}">
				<div style="font-size:medium; color:red; "> ${error} </div>
			</c:forEach>
			<c:choose>
				<c:when test="${ not empty user }">
					<div class="item clearfix">
						<label for="email">Email: </label> <input type="text" name="email"
							value="${ user.email }" id="email" disabled="disabled"/>
					</div>
					<div class="item clearfix">
						<label for="password">Password: </label> <input type="password"
							name="password" value="${ user.hashedPassword }" id="password" disabled="disabled"/>
					</div>
					<div class="item1 clearfix">
				
				</div>
			<input type="button" value="Log Out!"
			class="g-box2 glogout" />
			<input type="button" value="Home!"
			class="g-box2 ghome" />
				</c:when>
				<c:otherwise>
					<div class="item clearfix">
						<label for="email">Email: </label> <input type="text" name="email"
							value="" id="email" />
					</div>
					<div class="item clearfix">
						<label for="password">Password: </label> <input type="password"
							name="password" value="" id="password" />
					</div>
					<div class="item1 clearfix">
				<label for="remember" class="remember ">Remember Me! </label>
							<input type="checkbox" id="remember" name="remember" value="1" checked/>
			</div>
			<input type="submit" name="submit" value="Log In" class="g-box" />
				</c:otherwise>
			</c:choose>
			
		</form>
	</div>
	<div class="lower clearfix">
		<div class="user-pop">
			<h2>The Top Chicks&nbsp; · · · · · · &nbsp;</h2>
			<ul class="entry-list">
				<c:forEach var="auser" items="${users}">
					<li class="clearfix"><a href="home.do?uid=${ auser.uid }" target="_blank"> <img
						src="view.do?uid=${ auser.uid }" width="100" height="100" alt="" />
				</a>
					<div class="info">
						<h4>
							<a href="home.do?uid=${ auser.uid }" target="_blank">${ auser.fname } ${ auser.lname } </a>
						</h4>
						<dl>
							<dt class="gword">First Name:</dt>
							<dd>${ auser.fname }</dd>
							<dt class="gword">Last Name:</dt>
							<dd>${ auser.lname }</dd>
							<dt class="gword">Time Zone:</dt>
							<dd>${ auser.tzone }</dd>
							<dt class="gword">Current Time:</dt>
							<dd>
							<script type="text/javascript">
							document.write(calTime(${ auser.offset }))
							</script>
							</dd>
							<dt class="gword">stories:</dt>
							<dd>${ auser.scount }</dd>
							<dt class="gword">comments:</dt>
							<dd>${ auser.ccount }</dd>
							<dt class="gword">Following:</dt>
							<dd>${ auser.following }</dd>
							<dt class="gword">Follower:</dt>
							<dd>${ auser.follower }</dd>
							<dt class="gword">Signature:</dt>
							<dd>${ auser.signature }</dd>
						</dl>
					</div></li>
					<!-- entry -->
				</c:forEach>
		</div>
		<!--  user-pop-->
		<div class="weird grey r-box">
			<ul class="entry-list">
				<c:forEach var="astory" items="${storys}">
					<li class="clearfix"><a href="home.do?uid=${ astory.uid }" target="_blank"> <img
						src="view.do?uid=${ astory.uid }" width="50" height="50" alt="" />
				</a>
					<div class="info">
						<h4>
							<a href="home.do?uid=${ astory.uid }" target="_blank">${ astory.fname } ${ astory.lname } </a> 
						</h4>
						<div class="description">
								posted at <fmt:formatDate pattern="yyyy-MM-dd hh:mm:ss" value="${ astory.date }" />
						</div>
						<p>${ astory.content }</p>
					</div></li>
				<!-- entry -->
				</c:forEach>
			</ul>
		</div>
	</div>
</div>
<!-- body -->
<jsp:include page="template-bottom.jsp" />
