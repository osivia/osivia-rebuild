<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="p" uri="portal" %>


<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>2 colonnes</title>
    
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
                
                <p:region-container name="col1" />
            </div>
            
            <div class="col-sm-9">
                <p class="lead">Colonne #2</p>
                
                <p:region-container name="col2" />
            </div>
        </div>
    </div>
</body>

</html>
