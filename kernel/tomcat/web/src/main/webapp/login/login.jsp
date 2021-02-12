<%@ page language="java" contentType="text/html; charset=UTF-8"     pageEncoding="UTF-8"%>

<html>
<head>
<title>Login Page</title>
<link href="/osivia-portal-themes-generic-5.0-SNAPSHOT/css/socle.css" 	rel="stylesheet"/>
<script src="/portal-assets/components/bootstrap/js/bootstrap.bundle.min.js"></script>
</head>
<body>

	<div class="container">
		<div class="row h-100">
			<div class="m-auto">
				<div class="card card-body">
					<h5 class="card-title">Login page</h5>
					<form method="POST"
						action='<%=response.encodeURL("j_security_check")%>'>
						<div class="form-group">
							<label for="exampleInputEmail1">Email address</label> <input
								type="text" id="exampleInputEmail1" name="j_username"
								class="form-control" placeholder="Enter login"> <small
								id="emailHelp" class="form-text text-muted">We'll never
								share your id with anyone else.</small>
						</div>
						<div class="form-group">
							<label for="exampleInputPassword1">Password</label> <input
								type="password" name="j_password" id="exampleInputPassword1"
								class="form-control" placeholder="Password">
						</div>

						<button type="submit" class="btn btn-primary">Submit</button>
					</form>
				</div>
			</div>
		</div>
	</div>


</body>
</html>
