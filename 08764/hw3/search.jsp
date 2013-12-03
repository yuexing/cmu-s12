<%@ page import="hw2.amixyue.model.*" %>
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
            <div class="lcol">
            <div class="friends">
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
                                    <input type="button" value="Add Chick!" class="g-box kickout"/>
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
                                    <input type="button" value="Add Chick!" class="g-box kickout"/>
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
                                    <input type="button" value="Add Chick!" class="g-box kickout"/>
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
             <!-- body -->
			<div class="foot">
				<span id="icp">&copy; 2005－2012 yue xing, all rights reserved </span>
			</div>
            <!-- foot -->
		</div>
            <!-- wrapper -->
            <script type="text/javascript" src="script/weird.js"></script>
	</body>
</html>
