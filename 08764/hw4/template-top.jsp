<!doctype html>
<!-- Yue Xing, yuexing@andrew.cmu.edu, 08764 -->
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta content="yue xing" name="author" />
<meta content="homework1, Yue, yue, weird" name="description" />
<link rel="shortcut icon" href="images/weird.ico" type="image/x-icon" />
<link rel="stylesheet" href="css/reset.css" type="text/css" />
<link rel="stylesheet" href="css/green.css" type="text/css" />
<script type="text/javascript">
	function calTime(offset) {
		d = new Date();
		// convert to msec
		// add local time zone offset 
		// get UTC time in msec
		utc = d.getTime() + (d.getTimezoneOffset() * 60000);

		// create new Date object for different city
		// using supplied offset
		nd = new Date(utc + (3600000 * offset));

		return nd.toLocaleString();
	}
</script>
<title>Welcome To Yue's Weird</title>
</head>
<body>
	<div class="wrapper">
		<div class="head clearfix">
			<h1>
				<a class="logo" href="#">Yue's Weird </a>
			</h1>
			<ul class="nav">
				<li class="hotweird"><a href="hotweird.htm" target="_blank"
					class="green-word">HOT WEIRD </a></li>
				<li>/</li>
				<li class="chicks"><a href="chicks.htm" target="_blank"
					class="blue-word">CHICKS </a></li>
				<li>/</li>
				<li class="groups"><a href="groups.htm" target="_blank"
					class="orange-word">Groups </a></li>
				<li>/</li>
				<li class="aboutus"><a href="aboutus.htm" target="_blank"
					class="grey-word">ABOUT US </a></li>
				<li>/</li>
			</ul>
		</div>