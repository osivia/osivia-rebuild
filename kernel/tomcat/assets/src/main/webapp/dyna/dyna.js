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
      else if (source.nodeName == "INPUT" && source.type == "submit")
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
        //
         Event.stop(event);
         
         ajaxCall( options, url);

      }

   }

}



function ajaxCall(options, url){
    // Setup headers
    var headers = ["ajax","true"];

    // Add the view state value
    if (view_state != null)
    {
       headers.view_state = view_state;
    }

    // note : we don't convert query string to prototype parameters as in the case
    // of a post, the parameters will be appended to the body of the query which
    // will lead to a non correct request

    // Complete the ajax request options
    options.requestHeaders = headers;
    options.onSuccess = function(t)
    {
       var resp = "";
       eval("resp =" + t.responseText + ";");
       if (resp.type == "update_markup")
       {
       	
          // New layout
           var layout = resp.layout;
           var newPage = false;
           if( layout != null){
        	     // New layout
        	   
                 var dstContainer = document.getElementById("body");
                 // Get markup fragment
                 var markup =  resp.layout;

                 // Create a temporary element and paste the innerHTML in it
                 var srcContainer = document.createElement("div");

                 // Insert the markup in the div
                 new Insertion.Bottom(srcContainer, markup);

                 // Copy the region content
                 copyInnerHTML(srcContainer, dstContainer, "layout")
                 newPage = true;
	            }
        	
          // Iterate all changes
          for (var id in resp.fragments)
          {
             var matchingElt = document.getElementById(id);

             // Different than 1 is not good
             if (matchingElt != null)
             {
                var dstContainer = matchingElt;
                if (dstContainer != null)
                {
                   // Get markup fragment
                   var markup = resp.fragments[id];

                   // Create a temporary element and paste the innerHTML in it
                   var srcContainer = document.createElement("div");

                   // Insert the markup in the div
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
             		      var windowId = resp.regions[regionName][i];
             			  var matchingWindow  = document.getElementById(windowId);
             			  
             			  if( matchingWindow == null)	{
             				  // New window
             				  // <div class="dyna-window"><div id="cG9ydGFsQQ_e_e_dcGFnZUEtYWpheA_e_e_dd2luQXBhZ2VBLWFqYXg_e" class="partial-refresh-window">

             				  var divRegion =  document.getElementById(regionName);
             				  var newWindowDiv = document.createElement("div");
             				  newWindowDiv.className = "dyna-window";
             				  var partialWindowDiv = document.createElement("div");
             				  partialWindowDiv.id = windowId;
             				  partialWindowDiv.className = "partial-refresh-window";
             				  newWindowDiv.appendChild(partialWindowDiv);
             				  
             				  divRegion.appendChild(newWindowDiv);      

                            // Get markup fragment
                            var markup = resp.fragments[id];

                            // Create a temporary element and paste the innerHTML in it
                            var srcContainer = document.createElement("div");

                            // Insert the markup in the div
                            new Insertion.Bottom(srcContainer, markup);

                            // Copy the region content
                            copyInnerHTML(srcContainer, newWindowDiv, "partial-refresh-window")

              			  }
             		  }
                 }
             }
          }
          
          if( newPage){
       	   observePortlets();
          }

          // update view state
          if (resp.view_state != null)
          {
             view_state = resp.view_state;
          }
       }
       else if (resp.type == "update_page")
       {
          document.location = resp.location;
       }
    };
    
    new Ajax.Request(url, options);
}






/*
 * Copy the inner content of two zones of the provided containers.
 * The zone are found using the css class names. The operation
 * will succeed only if there is exactly one zone in each container.
 */
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


function footer()
{  
    var options = new Object();
	
	// We have a get
    options.method = "get"

    // We don't block
    options.asynchronous = false;

	ajaxCall(options, window.location.href);
}

