<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page contentType="text/html" isELIgnored="false"%>


<portlet:defineObjects />

<c:set var="namespace"><portlet:namespace /></c:set>

<div id="${namespace}-root"></div>

<script type="module" src="/osivia-portal-portlets-sample-5.0-SNAPSHOT/js/react2/Game.js"></script>     

<script type="text/babel" data-type="module">

import Game from "/osivia-portal-portlets-sample-5.0-SNAPSHOT/js/react2/Game.js";

ReactDOM.render(<Game />, document.getElementById('${namespace}-root'));

</script>





	
