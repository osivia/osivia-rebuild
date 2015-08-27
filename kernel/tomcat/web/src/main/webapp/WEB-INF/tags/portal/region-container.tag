<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ attribute name="name" description="Region name." required="true" rtexprvalue="true" type="java.lang.String" %>
<%@ taglib prefix="p" uri="portal" %>


<div class="panel panel-default">
    <div class="panel-heading">R&eacute;gion ${name}</div>
    
    <div class="panel-body">
       <p:region name="${name}" />
    </div>
</div>
