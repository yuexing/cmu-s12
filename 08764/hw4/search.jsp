<%@taglib uri="http://java.sun.com/jsp/jstl/core"
          prefix="c" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<jsp:include page="template-top.jsp" />
<link rel="stylesheet" href="css/home.css" type="text/css"/>
<!-- head -->
<div class="body clearfix">
	<div class="lcol">
		<div class="friends">
			<div class="grey lr-box p20 search">
				<form action="search.do" method="GET">
				<input type="text" name="frname" value="" class="search-box" /> <input
					type="submit" name="submit" value="Search!" class="g-box" />
				</form>	
			</div>
			<ul class="entry-list">
				<c:forEach var="auser" items="${ friends }">
					<li class="clearfix"><a href="home.do?uid=${ auser.uid }" target="_blank"> <img
						src="view.do?uid=${ auser.uid }" width="100" height="100" alt="" /> </a>
					<div class="info">
						<h4>
							<a href="home.do?uid=${ auser.uid }" target="_blank">${ auser.fname } ${ auser.lname }  </a>
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
						
					</div>
				</li>
				<!-- entry -->
				</c:forEach>
			</ul>
		</div>
	</div>
	<!-- lcol -->
	<div class="rcol">
		Weird Enough! <input type="button" value="Log Out!"
			class="g-box2 glogout" />
			<br/>
			<br/>
			<br/>
			<br/>
			<input type="button" value="Home!"
			class="g-box2 ghome" />
			<br/>
			<br/>
			<br/>
			<br/>
		<input type="button" value="Weird HomePage!"
			class="g-box2 gindex" />
	</div>
</div>
<!-- body -->
<jsp:include page="template-bottom.jsp" />