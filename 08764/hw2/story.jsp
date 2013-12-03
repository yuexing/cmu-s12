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
		<link rel="stylesheet" href="css/signup.css" type="text/css"/>
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
            <div class="signup">
                <form action="home.jsp" method="post" class="grey p20" name="story">
                <fieldset >
							<legend>Post Weird </legend> 
							<label for="story">weird: </label>     
							<textarea name="weird" rows="8" cols="70" id="story"></textarea>                  
							<div class="description"> </div>
						</fieldset>
                <div class="item1 clearfix">
							<input value="Weird!" type="submit" class="g-box"/>
							<input value="Clear" type="reset" class="reset"/></div>
            </form>
            </div>            
            <div class="signin">
                    No Comment, Pity!
					<input type="button" value="Go Back Home!" class="g-box2 ghome"/></div> 
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