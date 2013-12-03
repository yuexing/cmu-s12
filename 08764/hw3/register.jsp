<%@ page import="java.util.*" %>
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
			
            <!-- head -->
			<div class="body clearfix">
				<div class="signup">
					<h2>Welcome to Yue's Weird! </h2>
					<form action="RegisterServlet" method="post" name="joinus">
						<div class="item clearfix">
							<label for="email">Email: </label>
							<input type="text" id="email" name="email" value=""/></div>
						<div class="item clearfix">
							<label for="fname">First Name: </label>
							<input type="text" id="fname" name="fname" value=""/></div>
						<div class="item clearfix">
							<label for="lname">Last Name: </label>
							<input type="text" id="lname" name="lname" value=""/></div>
						<div class="item clearfix">
							<label for="tzone">Time Zone: </label>
							<select name="tzone" id="tzone">
                                <option value="0">Please Select ... </option>
                                <%
                                String[] tzoneIds = TimeZone.getAvailableIDs();
                                for(String tzoneId : tzoneIds){
                                %>
								<option value="<%= tzoneId%>"><%= tzoneId%> </option>
								<%
                                }
								%>
							</select>
						</div>
                        <div class="item clearfix">
							<label for="password">Password: </label>
							<input type="password" name="password" id="password" value=""/></div>
						<div class="item1 clearfix">
							<input value="Sign Up" type="submit" class="g-box" name="submit"/>
							<input value="Clear" type="reset" class="reset"/></div> 
					</form>
				</div>
				<div class="signin">
                    I Am One Of The Weird.
					<input type="button" value="Log In Now!" class="g-box2 gindex"/></div>
			</div>
             <!-- body -->
			<div class="foot">
				<span id="icp">&copy; 2005－2012 yue xing, all rights reserved </span>
			</div>
            <!-- foot -->
		</div>
        <!-- wrapper -->
        <script type="text/javascript" src="script/weird.js"></script>
        <script>
            var form = document.forms[0];
        	var email = form.email;
        	var email_p = false;
        	email.onblur = function(){
        		weird.get("ChkUserServlet?email="+email.value, function(resp){
            		if(resp==0){
            			alert("The email address has been registed\n use another one please!");
            			email.focus(); 
            		}else
            			email_p = true;
            	});
        	};
        	//more onsubmit?
        </script>
	</body>
</html>
