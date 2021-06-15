/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2009, Red Hat Middleware, LLC, and individual                    *
 * contributors as indicated by the @authors tag. See the                     *
 * copyright.txt in the distribution for a full listing of                    *
 * individual contributors.                                                   *
 *                                                                            *
 * This is free software; you can redistribute it and/or modify it            *
 * under the terms of the GNU Lesser General Public License as                *
 * published by the Free Software Foundation; either version 2.1 of           *
 * the License, or (at your option) any later version.                        *
 *                                                                            *
 * This software is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * Lesser General Public License for more details.                            *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this software; if not, write to the Free                *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA         *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.                   *
 ******************************************************************************/

var currentSubmit;








function sendData(action, windowId, fromPos, fromRegionId, toPos, toRegionId)
{
   var options = {
      requestHeaders: ["ajax","true","bilto","toto"],
      method: "post",
      postBody: "action=" + action + "&windowId=" + windowId + "&fromPos=" + fromPos + "&fromRegion=" + fromRegionId + "&toPos=" + toPos + "&toRegion=" + toRegionId,
      onSuccess: function(t)
      {
      },
      on404: function(t)
      {
         alert("Error 404: location " + t.statusText + " was not found.");
      },
      onFailure: function(t)
      {
         alert("Error " + t.status + " -- " + t.statusText);
      },
      onLoading: function(t)
      {
      }
   };
   new Ajax.Request(server_base_url + "/ajax", options);
}


// Check that the URL starts with the provided prefix
function isURLAccepted(url)
{

   var indexOfSessionId = server_base_url.indexOf(";jsessionid");
   if (indexOfSessionId > 0)
   {
      server_base_url = server_base_url.substring(0, indexOfSessionId - ";jsessionid".length - 1);
   }

   if (url.indexOf("http://") == 0)
   {
      var indexOfSlash = url.indexOf("/", "http://".length);
      if (indexOfSlash < 0)
      {
         return false;
      }
      else if (indexOfSlash > 0)
      {
         var path = url.substring(indexOfSlash);
         if (path.indexOf(server_base_url) != 0)
         {
            return false;
         }
      }
   }
   else if (url.indexOf(server_base_url) != 0)
   {
      return false;
   }

   //
   return true;
}

function bilto(event)
{

   // Locate the div container of the window
   var source = Event.element(event);
   var container = Element.up(source, "div.dyna-window");

   //attach onclick observer to all submit buttons
   $$('input[type=submit]').invoke('observe', 'click', function(e) {
      currentSubmit = e.findElement();
   });

   // We found the window
   if (container != null)
   {

      //
      var options = new Object();
      var url;

      
      // if unknow source (IMG, SPAN, ...) , search the ancestor A, INPUT or BUTTON
      if ((source.nodeName != "A") && (source.nodeName != "INPUT") && (source.nodeName != "BUTTON")) {
          source = Element.up(source, "A, INPUT, BUTTON");
          if (source == null)
              return;
      }
      
      
      if (!source.classList.contains("ajax-link")
    	        && ((Element.up(source, ".no-ajax-link") != null) || source.hasClassName("no-ajax-link"))) {
    	        return;
    	    }
      
      //
      if (source.nodeName == "A")
      {

         // Check we can handle this URL
         if (isURLAccepted(source.href))
         {

            // Set URL
            url = source.href;

            // We have a get
            options.method = "get"

            // We don't block
            options.asynchronous = false;
         }
      }
      else if ((source.nodeName == "INPUT" || source.nodeName == "BUTTON") && source.type == "submit")
      {
         // Find enclosing form
         var current = source.parentNode;
         while (current.nodeName != 'FORM' && current.nodeName != 'BODY')
         {
            current = current.parentNode;
         }

         // Check we have a form and use it
         if (current.nodeName == 'FORM')
         {

            var enctype = current.enctype

            // We don't handle file upload for now
            if (enctype != "multipart/form-data")
            {

               // Check it is a POST
               if (current.method.toLowerCase() == "post")
               {

                  // Check we can handle this URL
                  if (isURLAccepted(current.action))
                  {
                     // Set URL
                     url = current.action;

                     // Set the specified enctype
                     options.enctype = enctype;
                     options.asynchronous = false;
                     options.method = "post"
                     options.postBody = Form.serialize(current,'',currentSubmit);
                  }
               }
            }
         }
      }

      // Handle links here
      if (url != null)
      {
         
         directAjaxCall( container, options, url, event, null);

      }

   }

}

//Get unique key per session/location
function getScrollKey() {
	var session = readCookie("JSESSIONID");
	if( session == null)
		session = "";
	key = session + "/scroll/" +  window.location.href;
	return key;
}


//Explicits Ajax calls from portlet
function updatePortletContent(item, url) {
    var ajaxCall = true;

    var container = Element.up(item, "div.dyna-window");
    if (container == null) {
        ajaxCall = false;
    }

    if (!item.hasClassName("ajax-link") && (item.hasClassName("no-ajax-link") || (Element.up(item, ".no-ajax-link") != null))) {
        ajaxCall = false;
    }

    if (ajaxCall) {
        // Set URL
        var options = new Object();

        // We have a get
        options.method = "get"

        // We don't block
        options.asynchronous = true;

        directAjaxCall(container, options, url, null, null);
    } else {
        document.location = url;
    }
}

function directAjaxCall(container, options, url, eventToStop, callerId, popState, refresh){
    // Setup headers
    var headers = ["ajax","true"],
    	$ajaxWaiter = $JQry(".ajax-waiter");

	var headerState = null;
	if (popState !== undefined) {
		headerState = popState.viewState;

	} else {
		// Add the view state value
		if (view_state != null) {
			headerState = view_state;
		}
	}
	
	if (headerState != null) {
		headers.push('view_state', headerState);
	}

	if( refresh != null)
		headers.push('refresh', refresh);

    var popping;
    if ((eventToStop != null) && (eventToStop.type === "popstate")) {
    	popping = true;
    }
    
	if( session_check != null)	{
		headers.push('session_check', session_check);
	}
	
	// Compute current scroll position
	currentScroll = 0;
	filler = $JQry(".portlet-filler").first();
    if( filler != undefined)	{
		currentScroll = filler.scrollTop();
	}	
	
	
    var preventHistory = false;

    
    // note : we don't convert query string to prototype parameters as in the case
    // of a post, the parameters will be appended to the body of the query which
    // will lead to a non correct request

    // Complete the ajax request options
    options.requestHeaders = headers;
    
    
    // Waiter
    $ajaxWaiter.delay(200).addClass("in");
    
    options.onSuccess = function(t)
    {
        $ajaxWaiter.clearQueue();
        $ajaxWaiter.removeClass("in");	
    	
       var resp = "";
       
       if( t.responseText.length > 0)	{
       
	       try	{
	    	   eval("resp =" + t.responseText + ";");
	       } catch ( e){
	    	   
	    		   window.location = url;
	    		   return;
	    	   
	       }
       }
       
       
       if (resp.type == "update_markup")
       {
       	
          // New layout
           var layout = resp.layout;
           var newPage = false;
           if( layout != null){
        	     // New layout

                 
                 copyLayout( resp.layout);
                 newPage = true;
	            }
        	
           
           /* Update resources */

           updateResources(resp.resources)
   	   
           
          // Iterate all changes
          for (var id in resp.fragments)
          {
        	 originalId = id;
        	 id = id.replace(':','_');
        	 
             var matchingElt = document.getElementById(id);

             // Different than 1 is not good
             if (matchingElt != null)
             {
                var dstContainer = matchingElt;
                if (dstContainer != null)
                {
                   // Get markup fragment
                   var markup = resp.fragments[originalId];

                   // Create a temporary element and paste the innerHTML in it
                   var srcContainer = document.createElement("div");

                   new Insertion.Bottom(srcContainer, markup);

                   // Copy the region content
                   copyInnerHTML(srcContainer, dstContainer, "dyna-portlet")
                   copyInnerHTML(srcContainer, dstContainer, "dyna-decoration")
                }
               
             }
             else
             {
           	  // It may be a new windows
           	  // Check the regions ...
           	  
                 for (var regionName in resp.regions)	{
             		  for( var i=0; i< resp.regions[regionName].length; i++)	{
             		      var windowId = resp.regions[regionName][i].replace(':','_');
             		      
             		      if( windowId == id){
             			      var matchingWindow  = document.getElementById(windowId);
             			  
	             			  if( matchingWindow == null)	{
	             				  // New window
	             				  // <div class="dyna-window"><div id="cG9ydGFsQQ_e_e_dcGFnZUEtYWpheA_e_e_dd2luQXBhZ2VBLWFqYXg_e" class="partial-refresh-window">
	
	             				  var divRegion =  document.getElementById(regionName);
	             				  
	             				  if( divRegion != null)	{
	             					  
	             					  	  if( regionName == "modal-region")	{
	             					  		// Modal region already contains a default window that must be replaced
	             					  		var $target = $JQry(divRegion);
	             					  		$target.empty();
	             					  		
	             					  		// Modal window must not be reloadable
	             					  		preventHistory = true;
	             					  	  }
	             					  
	             					  	  // Prepare new window
			             				  var newWindowDiv = document.createElement("div");
			             				  newWindowDiv.className = "dyna-window";
			             				  var partialWindowDiv = document.createElement("div");
			             				  partialWindowDiv.id = windowId;
			             				  partialWindowDiv.className = "partial-refresh-window";
			             				  newWindowDiv.appendChild(partialWindowDiv);
			             				  
			             				  
			             				  // Search for first inserted windows to insert before it
			             				  var children = divRegion.children;
			             				  console.log("child" + children.length);

			             				  var insertBefore = null;
			             				  

			             				  for( var iDomWindow=0; iDomWindow< children.length && insertBefore == null; iDomWindow++)	{
			             					 var domWindow = children[iDomWindow];
			             					 var className = domWindow.className;
			             					 if( className == "dyna-window")	{
				             					 var insertedId = domWindow.firstChild.id;
				             					 
				             					 // Browse by order to find if this window is after the window to insert
				             					 for( var j=i+1; j< resp.regions[regionName].length && insertBefore == null; j++)	{
				             						 if( resp.regions[regionName][j] == insertedId)	{
				             							 insertBefore = domWindow;
				             							 //console.log("found "+ insertedId)
				             						 }
				             					 }
			             					  }
			             				  }

			             				  
			             				if( insertBefore == null)
			             					divRegion.appendChild(newWindowDiv);
			             				else	
			             					divRegion.insertBefore(newWindowDiv, insertBefore);
			             				
			
			                            // Get markup fragment
			                            var markup = resp.fragments[originalId];
			
			                            // Create a temporary element and paste the innerHTML in it
			                            var srcContainer = document.createElement("div");
			
			                            // Insert the markup in the div
			                            new Insertion.Bottom(srcContainer, markup);
			
			                            // Copy the region content
			                            copyInnerHTML(srcContainer, newWindowDiv, "partial-refresh-window");
			                            
			                            observePortlet(partialWindowDiv);
	             				  }
	
	              			  }
             		  	  }	
             		 }
                 }
             }
          }
           
           
          
          if( newPage){
        	  
        	  $JQry('#osivia-modal').modal('hide')
        	  
       	   	  observePortlets();
          }


          // update view state
          if (resp.view_state != null)
          {
             view_state = resp.view_state;
          }

          
          
          if (popping === undefined  && resp.restore_url != "" && preventHistory == false) {

              // update the current page
        	  if( history.state != null)	{
	              var stateObject = history.state;
	              stateObject.currentScroll = currentScroll;
	              history.replaceState(stateObject,"", stateObject.fullUrl);
        	  }
        	  
        	  
              // Add the current page
              var stateObject = {
                  url: resp.pop_url,
                  viewState:view_state,
                  currentScroll:0,
                  fullUrl: resp.full_state_url
              };
              
              history.pushState(stateObject, "", resp.full_state_url);
          }
          

          
          // Call jQuery.ready() events
          $JQry(document).ready();         
          
          
          // Restore cursor
          
          if( popState !== undefined)    {
              if( popState.currentScroll != 0)	{
            	filler = $JQry(".portlet-filler").first();
            	if( filler != undefined)	{
            		filler.scrollTop(popState.currentScroll);
            	}
              }
          }	else	{
        	  if( resp.page_changed == "false"){
        		  filler = $JQry(".portlet-filler").first();
              		if( filler != undefined && currentScroll != 0)	{
              			filler.scrollTop(currentScroll);
              	}
        	  }
          }

          
          
       }
       else if (resp.type == "update_page")
       {
    	  if( resp.location == "/back")	{
    		  reload(history.state, null, false)
    		  return;
    	  }	
    	  
    	  if( resp.location == "/back-refresh")	{
    		  reload(history.state, null, true)
    		  return;
    	  }	   	  
    	  
    	  if( resp.location == "/refresh")	{
    		  reload(history.state, null, true)
    		  return;
    	  }	
    	  
   	  
    	  
     	  document.location = resp.location;
       }
    };
     

    if ((eventToStop !== undefined) &&  (eventToStop !== null) && !(eventToStop.type === "popstate")) {
        Event.stop(eventToStop);
    }

     
    
    
    
    new Ajax.Request(url, options);
}


function reload(state, event, refresh)	{
	var options = new Object();
	options.method = "get";
	options.asynchronous = true;
	directAjaxCall( null,options, state.url, event,null, state, refresh);
}


window.onpopstate = function (event) {
    if (event.state) {
    	reload(event.state, event)
    }

}



function updateResources(newHeaderResources)	{

	if( newHeaderResources != null){
		   for( var iNewHeader=0; iNewHeader< newHeaderResources.length; iNewHeader++)	{
			   
			   var newHeader = newHeaderResources[iNewHeader];
			   
			   var head  = document.getElementsByTagName('head')[0];        		   
			   var headers = head.children;
			   var insert = true;
			   
			   for( var i=0; i<  headers.length; i++)	{
	    		   if( newHeader.tag == "LINK" && headers[i].tagName == "LINK")	{
	     			   if(  location.origin+newHeader.href == headers[i].href)
	     				   insert = false;
	    		   }
	     		   if( newHeader.tag == "SCRIPT" && headers[i].tagName == "SCRIPT")	{
	 			   if(  location.origin+newHeader.src == headers[i].src )
	 				   insert = false;
	    		   }
	    	   }        		   
			   
			   if( insert && newHeader.tag == "LINK")	{
	 		    var link  = document.createElement('LINK');
	 		    link.rel  =  newHeader.rel;
	 		    link.type =  newHeader.type;
	 		    link.href =  newHeader.href;
	 		    if( newHeader.media != undefined)
	 		       link.media = newHeader.media;
	 		    head.appendChild(link);
			   }
			   
			   if( insert && newHeader.tag == "SCRIPT")	{
	 		    var script  = document.createElement('SCRIPT');
	 		    script.type  =  newHeader.type;
	 		    script.src  =  newHeader.src;	        		    
	 		    head.appendChild(script);
			   }
		   }
	}
}


/*
 * Copy the inner content of two zones of the provided containers.
 * The zone are found using the css class names. The operation
 * will succeed only if there is exactly one zone in each container.
 */
function copyLayout( layout)
{
	// TODO : a supprimer
	if( layout.includes("modal_do_not_delete"))
		return;

    // Create a temporary element and paste the innerHTML in it
    var srcContainer = document.createElement("html");
    
     
    srcContainer.innerHTML = layout;
	
    var srcs = srcContainer.select("body");
    if (srcs.length == 1)
    {
       var src = srcs[0];

       document.body = src;
    }



}

function copyInnerHTML(srcContainer, dstContainer, className)
{
   var classSelector = "." + className;
   var srcs = srcContainer.select(classSelector);
   if (srcs.length == 1)
   {
      var src = srcs[0];

      //
      var dsts = dstContainer.select(classSelector);
      if (dsts.length == 1)
      {
         var dst = dsts[0];

         // Remove existing non attribute children in destination
         var dstChildren = dst.childNodes;
         var copy = new Array();
         for (var i = 0; i < dstChildren.length; i++)
         {
            var dstChild = dstChildren.item(i);
            if (dstChild.nodeType != 2)
            {
               copy[i] = dstChildren.item(i);
            }
         }
         for (var i = 0; i < copy.length; i++)
         {
            Element.remove(copy[i]);
         }

         // Move src non attribute children to the destination
         while (src.hasChildNodes())
         {
            var srcChild = src.firstChild;
            if (srcChild.nodeType != 2)
            {
               dst.appendChild(srcChild);
            }
            else
            {
               src.removeChild(srcChild);
            }
         }
      }
      else
      {
         // Should log that somewhere but
      }
   }
   else
   {
      // Should log that somewhere
   }
}


function observePortlets()
{

   // Find the dyna portlets
   var portlets_on_page = $$(".partial-refresh-window");

   // Add listener for the dyna windows on the dyna-window element
   // and not async-window as this one will have its markup replaced
   for (var i = 0; i < portlets_on_page.length; i++)
   {
      var portlet = Element.up(portlets_on_page[i]);
      Event.observe(portlet, "click", bilto);
   }
}


function observePortlet(refreshWindow)
{

      Event.observe(refreshWindow, "click", bilto);

}


function footer()
{  

    	// Non Ajax Response
        var options = new Object();
    	
    	// We have a get
        options.method = "get"

        // We don't block
        options.asynchronous = false;

    	directAjaxCall(null,options, window.location.href,null);


}

