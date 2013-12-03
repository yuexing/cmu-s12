<%@page import="impl.dao.hw2.amixyue.ArrayUserDao"%>
<%@page import="dao.hw2.amixyue.*"%>
<%@ page import="model.hw2.amixyue.*" %>
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
		<link rel="stylesheet" href="css/index.css" type="text/css"/>
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
			UserDao userDao = new ArrayUserDao();
			%>
			<%
			//if has loged in
			User user = (User)request.getSession().getAttribute("user");
			%>
            <!-- head -->
			<div class="body">
				<div class="upper clearfix">
					<div class="grey rr-box p20 signup">
						<h2>Yue's Weird </h2>
						<p>
							<b>654 Cities </b>
							<b><%=userDao.count() %> Chicks </b>
							<b>2,600 Groups </b>
							<br/>
                    Create Weird, Share Weird, Enjoy Weird
							<br/>
							<%if(user == null){ %>
							<input type="button" value="Join US!" class="g-box2 joinus"/>
							<%}else{ 
							%>
							<input type="button" value="Home Page!" class="g-box2 ghome"/>
							<%} %>
						</p>     
					</div>
					
					<form action="LoginServlet" method="post" class="grey lr-box p20 signin">
					<%
					String errorMsg = (String)request.getAttribute("errorMsg");
					String errPwdUsr = (String)request.getAttribute("errPwdUsr");
					if(errorMsg != null){
					%>
					<span class="error"><%=errorMsg %></span>
					<%
					}%>
						<div class="item clearfix">
							<label for="email">Email: </label>
							<%
							if(errorMsg != null && errPwdUsr!=null){
							%>
							<input type="text" name="email" value="<%=errPwdUsr %>" id="email"/></div>
							<%
							}else if(user == null){
							%>
							<input type="text" name="email" value="" id="email"/></div>
							<%
							}else{
							%>
							<input type="text" name="email" value="<%=user.getEmail() %>" id="email" disabled="disabled"/></div>
						<%}
							if(user == null){
							%>
						<div class="item clearfix">
							<label for="password">Password: </label>
							<input type="password" name="password" value="" id="password"/></div>
						<div class="item1 clearfix">
							<label for="remember" class="remember ">Remember Me! </label>
							<input type="checkbox" id="remember" name="remember" value="1" checked/></div>                    
						<input type="submit" name="submit" value="Log In" class="g-box"/>
						<%
						;
						}else{%>
						<div class="item clearfix">
							<label for="password">Name: </label>
							<input value="<%= user.getFname()%> <%= user.getLname()%>" disabled="disabled"/></div>
						<input type="button" value="Log Out!" class="g-box2 gindex"/>
						<%} %>
						</form>                 
				</div>
				<div class="lower clearfix">
					<div class="user-pop">
						<h2>The Top Chicks&nbsp; · · · · · · &nbsp; </h2>
						<ul class="entry-list">
							<li class="clearfix">
								<a href="#" target="_blank">
									<img src="images/1.jpg" width="100" height="100" alt=""/></a>
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
										<dd>300 </dd>                         
										<dt class="gword">Signature: </dt>
										<dd>this is a signature! </dd>
									</dl>
								</div>
							</li><!-- entry -->
							<li class="clearfix">
								<a href="#" target="_blank">
									<img src="images/1.jpg" width="100" height="100" alt=""/></a>
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
								</div>
							</li><!-- entry -->
							<li class="clearfix">
								<a href="#" target="_blank">
									<img src="images/1.jpg" width="100" height="100" alt=""/></a>
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
										<dd>20 </dd>                         
										<dt class="gword">Signature: </dt>
										<dd>this is a signature! </dd>
									</dl>
								</div>
							</li><!-- entry -->
						</ul> <!-- entry-list -->                   
					</div><!--  user-pop-->
					<div class="weird grey r-box">
						<ul class="entry-list">
							<li class="clearfix">
								<a href="#" target="_blank">
									<img src="images/2.jpg" width="50" height="50" alt=""/></a>
								<div class="info">
									<h4>
										<a href="#" target="_blank">Amy Xing </a>
									</h4>                        
									<p> A glaring misspelling on a street sign outside a Manhattan high school has some laughing and stauncher critics condemning what they perceive to be a symbol of the “dumbing down" of American society. </p>    
								</div>
							</li><!-- entry -->
							<li class="clearfix">
								<a href="#" target="_blank">
									<img src="images/3.jpg" width="50" height="50" alt=""/></a>
								<div class="info">
									<h4>
										<a href="#" target="_blank">Amy Xing </a>
									</h4>                        
									<p> One western Pennsylvania fire department learned that there's not necessarily fire wherever there's smoke. Firefighters in New Castle, Pa., were called when an electrical outlet on a floor was smoking, only to find that happened because the family's cat urinated into the outlet. </p>    
								</div>
							</li><!-- entry -->                         
						</ul>
					</div>
				</div>
			</div>
             <!-- body -->
			<div class="foot"><span id="icp">&copy; 2005－2012 yue xing, all rights reserved</span></div>
            <!-- foot -->
		</div>
        <!-- wrapper -->
        <script type="text/javascript" src="script/weird.js"></script>
	</body>
</html>
