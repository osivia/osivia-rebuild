<html>
<head>
<title>Login Page</title>
<link href="/osivia-portal-themes-generic-5.0-SNAPSHOT/css/socle.css"
	rel="stylesheet">
<script async
	src="/osivia-portal-themes-generic-5.0-SNAPSHOT/js/bootstrap.bundle.js"></script>
</head>
<body>

	<div class="container">
		<div class="row h-100">
			<div class="m-auto">
				<div class="card card-body">
					<h5 class="card-title">Login error</h5>
<div class="alert alert-danger" role="alert">
Invalid username and/or password, please try
<a href='<%= response.encodeURL("/portal/auth") %>'>again</a>.
</div>					
					
				</div>
			</div>
		</div>
	</div>


</body>
</html>

