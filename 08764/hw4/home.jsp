<%@taglib uri="http://java.sun.com/jsp/jstl/core"
          prefix="c" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<jsp:include page="template-top.jsp" />
<link rel="stylesheet" href="css/home.css" type="text/css"/>
<!-- head -->
<div class="body clearfix">
	<h2>${ homeuser.fname } ${ homeuser.lname }</h2>
	<div class="lcol">
		<div class="xbar clearfix">
			<div>
				<a href="friends" target="friends"> <span>Friends </span> </a> <a
					href="wall" target="wall"> <span>Wall </span> </a> <a href="base"
					class="now" target="base"> <span>Base Info </span> </a>
			</div>
		</div>
		<!-- xbar -->
		<div class="base" style="display: block">
			<ul class="entry-list">
				<li class="clearfix"><a href="home.do?uid=${ homeuser.uid }" target="_blank"> <img
						src="view.do?uid=${ homeuser.uid }" width="100" height="100" alt="" />
				</a>
					<div class="info">
						<dl>
							<dt class="gword">First Name:</dt>
							<dd>${ homeuser.fname }</dd>
							<dt class="gword">Last Name:</dt>
							<dd>${ homeuser.lname }</dd>
							<dt class="gword">Time Zone:</dt>
							<dd>${ homeuser.tzone }</dd>
							<dt class="gword">Time:</dt>
							<dd>
							<script type="text/javascript">
							document.write(calTime(${ homeuser.offset }));
							</script>
							</dd>
							<dt class="gword">stories:</dt>
							<dd>${ homeuser.scount }</dd>
							<dt class="gword">comments:</dt>
							<dd>${ homeuser.ccount }</dd>
							<dt class="gword">Following:</dt>
							<dd>${ homeuser.following }</dd>
							<dt class="gword">Follower:</dt>
							<dd>${ homeuser.follower }</dd>
							<dt class="gword">Signature:</dt>
							<dd>${ homeuser.signature }</dd>
						</dl>
					</div>
				</li>
				<!-- entry -->
			</ul>
			<c:if test="${ homeuser.uid eq user.uid }">
			<input type="button" value="Edit Profile!" class="g-box2 gedit" />
			</c:if>
		</div>

		<div class="wall" style="display: none">
			<div class="grey lr-box p20">
				<input type="button" value="Got Weird!" class="g-box gstory" />
			</div>
			<ul class="entry-list">
				<c:set var="count" value="0" />
				<c:forEach var="story" items="${ storys }">
					<li class="clearfix"><a href="home.do?uid=${ story.story.uid }" target="_blank"> <img
							src="view.do?uid=${ story.story.uid }" width="50" height="50" alt="" />
					</a>
						<div class="info">
							<h4>
								<a href="#" target="_blank">${ story.story.fname } ${ story.story.lname
									} </a>
							</h4>
							<div class="description">
								posted at <fmt:formatDate pattern="yyyy-MM-dd hh:mm:ss" value="${ story.story.date }" /> 
								 <a href="comment.do?sid=${ story.story.sid }" target="_blank"
									class="gcomment">Comment!</a>
							</div>
							<p>${ story.story.content }</p>
							<ul class="commentutil ">
							<c:if test="${fn:length(story.comments) > 0}">
								<li class="arrow"><i></i></li>
								<li class="comments">
									<ul>
								</c:if>
								
										<c:forEach var="comment" items="${ story.comments }">
											<li class="clearfix"><a href="home.do?uid=${ comment.uid }" target="_blank"><img
													src="view.do?uid=${ comment.uid }" width="20" height="20"
													alt="" /> </a>
												<div class="comment">
													<h5>
														<a href="home.do?uid=${ comment.uid }" target="_blank">${ comment.fname } ${
															comment.lname }</a>
													</h5>
													<div class="description">
														posted at ${ comment.date }
														
														<c:if test="${ user.uid eq comment.uid }">
														<a href="delete.do?type=cm&cid=${ comment.cid }"
															class="dcomment" query="type=cm&cid=${ comment.cid }">Delete</a>
														</c:if>
														
													</div>
													<p>${ comment.content }</p>
												</div>
											</li>
										</c:forEach>
										<c:if test="${fn:length(story.comments) > 0}">
									</ul>
								</li>
								</c:if>
							</ul>
							<c:if test="${ user.uid eq story.story.uid }">
							<input type="button" value="Del Weird!" class="g-box kickout" query="type=st&sid=${ story.story.sid }"/>
							</c:if>
						</div>
					</li>
					<c:set var="count" value="${ count + 1 }" />
					<!-- entry -->
				</c:forEach>
			</ul>
		</div>
		<div class="friends" style="display: none">
			<div class="grey lr-box p20 search">
				<form action="search.do" method="GET">
				<input type="text" name="frname" value="" class="search-box" /> <input
					type="submit" name="submit" value="Search!" class="g-box" />
				</form>				
			</div>
			<ul class="entry-list">
				<c:forEach var="auser" items="${ friends }">
					<li class="clearfix"><a href="#" target="_blank"> <img
						src="view.do?uid=${ auser.uid }" width="100" height="100" alt="" /> </a>
					<div class="info">
						<h4>
							<a href="#" target="_blank">${ auser.fname } ${ auser.lname }  </a>
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
						<c:if test="${ homeuser.uid eq user.uid }">
						<input type="button" value="Kick out!" class="g-box kickout" query="type=fr&uid=${ auser.uid }"/>
						</c:if>
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
			class="g-box2 glogout" /><br/>
			<br/>
			<br/>
			<br/>
			<c:if test="${ (homeuser.uid ne user.uid) && isFollowing eq false  }">
		A Wonderful Guy!<input type="button" value="Follow!"
			class="g-box2 gfollow" uid="${ homeuser.uid }"/>
			<br/>
			<br/>
			<br/>
			<br/>
			</c:if>		
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
