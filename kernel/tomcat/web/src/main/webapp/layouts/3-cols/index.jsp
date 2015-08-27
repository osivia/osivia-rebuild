<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
<!DOCTYPE html>


<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>3 colonnes</title>
    
    <script type="text/javascript" src="${pageContext.request.contextPath}/components/jquery/jquery-2.1.4.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/components/bootstrap/js/bootstrap.min.js"></script>
    
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap/bootstrap.min.css">
</head>


<body>
    <div class="container">
        <div class="page-header">
            <h1>Titre <small>Sous-Titre</small></h1>
        </div>
    
        <div class="row">
            <div class="col-sm-3">
                <p class="lead">Colonne #1</p>
                
            </div>
            
            <div class="col-sm-9">
                <div class="row">
                    <div class="col-sm-6">
                        <p class="lead">Colonne #2</p>
                        
                    </div>
                    
                    <div class="col-sm-6">
                        <p class="lead">Colonne #3</p>
                        
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>

</html>
