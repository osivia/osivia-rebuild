<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page contentType="text/html" isELIgnored="false"%>


<portlet:defineObjects />

<c:set var="namespace"><portlet:namespace /></c:set>

<div id="${namespace}-root"></div>

<script type="text/babel">
// Obtain the root 
    const rootElement = document.getElementById('${namespace}-root')
// Create a ES6 class component    
    class ShoppingList extends React.Component { 
// Use the render function to return JSX component      
    render() { 
         return (
        <div className="shopping-list">
        <h1>Shopping List for {this.props.name}</h1>
          <ul>
            <li>Instagram</li>
            <li>WhatsApp</li>
            <li>Oculus</li>
          </ul>
        </div>
      );
      } 
    }
// Create a function to wrap up your component
function App(){
  return(
  <div>
    <ShoppingList name="@luispagarcia on Dev.to!"/>
  </div>
  )
}


// Use the ReactDOM.render to show your component on the browser
    ReactDOM.render(
      <App />,
      rootElement
    )


</script>





	
