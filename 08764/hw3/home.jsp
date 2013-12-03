<%@ page import="hw2.amixyue.model.*" %>
<%@ page import="hw2.amixyue.util.*" %>
<!doctype html>
<!-- Yue Xing, yuexing@andrew.cmu.edu, 08764 -->
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta content="yue xing" name="author"/>
		<meta content="homework1, Yue, yue, weird" name="description"/>
		<link rel="shortcut icon" href="images/weird.ico" type="image/x-icon" />
		<link rel="stylesheet" href="css/reset.css" type="text/css"/>
		<link rel="stylesheet" href="css/green.css" type="text/css"/>
		<link rel="stylesheet" href="css/home.css" type="text/css"/>
		<title>Welcome To Yue's Weird </title>
	</head>
	<body>
		<div class="wrapper">
			<div class="head clearfix">
				<h1>
					<a class="logo" href="welcome.jsp">Yue's Weird </a>
				</h1>               
				<ul class="nav">
					<li class="hotweird">
						<a href="hotweird.htm" target="_blank" class="green-word">HOT WEIRD </a> 
					</li>
					<li>/ </li>
					<li class="chicks">
						<a href="chicks.htm" target="_blank" class="blue-word">CHICKS </a> 
					</li>
					<li>/ </li>
					<li class="groups">
						<a href="groups.htm" target="_blank" class="orange-word">Groups </a> 
					</li>
					<li>/ </li>
					<li class="aboutus"> 
						<a href="aboutus.htm" target="_blank" class="grey-word">ABOUT US </a> 
					</li>
					<li>/ </li>
				</ul>
			</div>
			<% 
			User currentUser = (User)request.getSession().getAttribute("user");
			if(currentUser == null) {
				response.sendRedirect("welcome.jsp");
				return;
				}
			
			%>
            <!-- head -->
			<div class="body clearfix">
				<h2><%=currentUser.getFname() %> <%=currentUser.getLname() %> </h2>
				<div class="lcol">
					<div class="xbar clearfix"> 
						<div>
							<a href="friends" target="friends">
								<span>Friends </span>
							</a>
							<a href="wall" target="wall">
								<span>Wall </span> 
							</a>
							<a href="base" class="now" target="base">
								<span>Base Info </span>
							</a>
						</div>
					</div><!-- xbar -->
					<div class="base" style="display:block">
						<ul class="entry-list">
							<li class="clearfix">
								<a href="#" target="_blank">
									<img src="images/1.jpg" width="100" height="100" alt=""/></a>
								<div class="info">                       
									<dl>
										<dt class="gword">First Name: </dt>
										<dd><%=currentUser.getFname() %> </dd>
										<dt class="gword">Last Name: </dt>
										<dd><%=currentUser.getLname() %> </dd>
										<dt class="gword">Time Zone: </dt>
										<%
										String nowDateTime = DateTool.date2String("yyyy-MM-dd HH:mm:ss");
										String userTime = DateTool.string2TimezoneDefault(nowDateTime,currentUser.getTzone().getID());
										%>
										<dd><%=currentUser.getTzone().getID() %> </dd>
										
                                        <dt class="gword">Time: </dt>
										<dd><%=userTime %></dd>
										<dt class="gword">credit: </dt>                               
										<dd>100 </dd>
										<dt class="gword">Friends: </dt>
										<dd>30 </dd>                         
										<dt class="gword">Signature: </dt>
										<dd><%=currentUser.getSignature() %> </dd>
									</dl>
								</div>
							</li><!-- entry -->
						</ul>
						<input type="button" value="Edit Profile!" class="g-box2 gedit"/></div>
                    
                    <div class="wall" style="display:none">
						<div class="grey lr-box p20">
						    <input type="button" value="Got Weird!" class="g-box gstory"/>
                        </div>
						<ul class="entry-list">							
							<li class="clearfix">
								<a href="#" target="_blank">
									<img src="images/2.jpg" width="50" height="50" alt=""/></a>
								<div class="info">
									<h4>
										<a href="#" target="_blank">Amy Xing </a>
									</h4>   
                                    <div class="description">
                                        posted at 03/21/2012 23:45:45 <a href="comment.htm" target="_blank" class="gcomment">Comment!</a>
                                    </div>                     
									<p> A glaring misspelling on a street sign outside a Manhattan high school has some laughing and stauncher critics condemning what they perceive to be a symbol of the “dumbing down" of American society. </p>  
                                  <input type="button" value="Del Weird!" class="g-box kickout"/>
								</div>
                                
							</li><!-- entry -->
							<li class="clearfix">
								<a href="#" target="_blank">
									<img src="images/3.jpg" width="50" height="50" alt=""/></a>
								<div class="info">
									<h4>
										<a href="#" target="_blank">Amy Xing </a>
									</h4> 
                                    <div class="description">
                                        posted at 02/21/2012 23:45:45 <a href="comment.htm" target="_blank" class="gcomment">Comment!</a>
                                    </div>                       
									<p> One western Pennsylvania fire department learned that there's not necessarily fire wherever there's smoke. Firefighters in New Castle, Pa., were called when an electrical outlet on a floor was smoking, only to find that happened because the family's cat urinated into the outlet. </p>
                                    <ul class="commentutil ">
                                            <li class="arrow"><i></i></li>
                                            <li class="comments">
                                            <ul>
                                                <li class="clearfix">
                                                <a href="#" target="_blank"><img src="images/3.jpg" width="20" height="20" alt=""/></a>
                                                <div class="comment">
                                                    <h5>
                                                <a href="#" target="_blank">Yue Xing</a>
                                                </h5>
                                                <div class="description">
                                        posted at 02/21/2012 23:45:45 <a href="comment.htm" target="_blank" class="gcomment">Comment!</a>
                                    </div>
                                                <p>I like it Very Much!</p>
                                                </div>
                                                </li>
                                                <li class="clearfix">
                                                <a href="#" target="_blank"><img src="images/3.jpg" width="20" height="20" alt=""/></a>
                                                <div class="comment">
                                                    <h5>
                                                <a href="#" target="_blank">Yue Xing</a>
                                                </h5>
                                                <div class="description">
                                        posted at 01/21/2012 23:45:45 <a href="comment.htm" target="_blank" class="gcomment">Comment!</a>
                                    </div>
                                                <p>I like it Very Much!</p>
                                                </div></li>
                                            </ul>
                                            </li>                                            
                                        </ul>
                                <input type="button" value="Del Weird!" class="g-box kickout"/>            
								</div>                                
							</li><!-- entry -->  
						</ul>
						</div>
                        <div class="friends" style="display:none">
                        <div class="grey lr-box p20 search">
						    <input type="text" name="frname" value="" class="search-box"/>          
						    <input type="button" name="submit" value="Search!" class="g-box gsearch"/>
                        </div> 
						<ul class="entry-list">
							<li class="clearfix">
								<a href="#" target="_blank">
									<img src="images/2.jpg" width="100" height="100" alt=""/></a>
								<div class="info">
									<h4>
										<a href="#" target="_blank">Amy Xing </a>
									</h4>                        
									<dl>
										<dt class="gword">First Name: </dt>
										<dd>Amy </dd>
										<dt class="gword">Last Name: </dt>
										<dd>Xing </dd>
										<dt class="gword">Time Zone: </dt>
										<dd>(GMT -12:00) Eniwetok, Kwajalein </dd>
                                        <dt class="gword">Current Time: </dt>
										<dd>01/25/2012 22:36:37 </dd>
										<dt class="gword">credit: </dt>                               
										<dd>100 </dd>
										<dt class="gword">Friends: </dt>
										<dd>30 </dd>                         
										<dt class="gword">Signature: </dt>
										<dd>this is a signature! </dd>
									</dl>
                                    <input type="button" value="Kick out!" class="g-box kickout"/>
								</div>
							</li><!-- entry -->
							<li class="clearfix">
								<a href="#" target="_blank">
									<img src="images/3.jpg" width="100" height="100" alt=""/></a>
								<div class="info">
									<h4>
										<a href="#" target="_blank">Amy Xing </a>
									</h4>                        
									<dl>
										<dt class="gword">First Name: </dt>
										<dd>Amy </dd>
										<dt class="gword">Last Name: </dt>
										<dd>Xing </dd>
										<dt class="gword">Time Zone: </dt>
										<dd>(GMT -12:00) Eniwetok, Kwajalein </dd>
                                        <dt class="gword">Current Time: </dt>
										<dd>01/25/2012 22:36:37 </dd>
										<dt class="gword">credit: </dt>                               
										<dd>100 </dd>
										<dt class="gword">Friends: </dt>
										<dd>30 </dd>                         
										<dt class="gword">Signature: </dt>
										<dd>this is a long signature!this is a long signature!this is a long signature!this is a long signature!this is a long signature!this is a long signature!this is a long signature! </dd>
									</dl>
                                    <input type="button" value="Kick out!" class="g-box kickout"/>
								</div>                                
							</li><!-- entry -->
							<li class="clearfix">
								<a href="#" target="_blank">
									<img src="images/4.jpg" width="100" height="100" alt=""/></a>
								<div class="info">
									<h4>
										<a href="#" target="_blank">Amy Amy Amy Amy Amy Amy Amy Amy Amy Xing </a>
									</h4>                        
									<dl>
										<dt class="gword">First Name: </dt>
										<dd>Amy Amy Amy Amy Amy Amy Amy Amy Amy </dd>
										<dt class="gword">Last Name: </dt>
										<dd>Xing </dd>
										<dt class="gword">Time Zone: </dt>
										<dd>(GMT -12:00) Eniwetok, Kwajalein </dd>
                                        <dt class="gword">Current Time: </dt>
										<dd>01/25/2012 22:36:37 </dd>
										<dt class="gword">credit: </dt>                               
										<dd>100 </dd>
										<dt class="gword">Friends: </dt>
										<dd>30 </dd>                         
										<dt class="gword">Signature: </dt>
										<dd>this is a signature! </dd>
									</dl>
                                    <input type="button" value="Kick out!" class="g-box kickout"/>
								</div>
							</li><!-- entry -->
						</ul>
						</div>
				</div><!-- lcol -->
				<div class="rcol">
                    Weird Enough!
					<input type="button" value="Log Out!" class="g-box2 gindex"/>
                </div>
			</div>
			<div class="foot">
				<span id="icp">&copy; 2005－2012 yue xing, all rights reserved </span>
			</div>
            <!-- foot -->
		</div>
        <!-- wrapper -->
		<script type="text/javascript" src="script/weird.js"></script>
	</body>
</html>
