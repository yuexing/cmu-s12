<%@ page import="java.util.*" %>
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
				<h2><%=currentUser.getFname() %> <%=currentUser.getFname() %> </h2>
				<div class="signup">
					<form action="ProfileServlet" method="post" name="edit">
						<div class="item clearfix">
							<label for="email">Email: </label>
							<input type="text" name="email" value="<%=currentUser.getEmail() %>" id="email"/></div>
						<div class="item clearfix">
							<label for="fname">First Name: </label>
							<input type="text" name="fname" value="<%=currentUser.getFname() %>" id="fname"/></div>
						<div class="item clearfix">
							<label for="lname">Last Name: </label>
							<input type="text" name="lname" value="<%=currentUser.getLname() %>" id="lname"/></div>
						<div class="item clearfix">
							<label for="tzone">Time Zone: </label>
							<select name="tzone" id="tzone">
                                <%
                                String[] tzoneIds = TimeZone.getAvailableIDs();
                                for(String tzoneId : tzoneIds){
                                	if(tzoneId.equals(currentUser.getTzone().getID())){
                                %>
                                <option value="<%= tzoneId%>" selected="selected"><%= tzoneId%> </option>
                                <%
                                }else{%>
								<option value="<%= tzoneId%>"><%= tzoneId%> </option>
								<%
                                }
                                }
								%>
							</select>
						</div>
						<fieldset >
							<legend>Profile Image </legend> 
							<label for="image">Picture: </label>     
							<input type="file" name="image" id="image"/>                  
							<div class="description">Your virtual face or picture. Pictures larger than 1024x1024 pixels will be scaled down. Please include a recognizable head and shoulder shot for your profile. </div>
						</fieldset>
						<div class="item clearfix">
							<label for="signature">Signature: </label>
							<textarea name="signature" rows="8" cols="40" id="signature"><%=currentUser.getSignature() %></textarea>
						</div>
						<div class="item1 clearfix">
							<input value="Sign Up" type="submit" class="g-box"/>
							<input value="Clear" type="reset" class="reset"/></div>
					</form>  
				</div>
				<div class="signin">
                    Nothing To Edit Here.
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