$JQry(function() {
	
	$JQry("#osivia-modal").each(function(index, element) {
		var $element = $JQry(element);
		var loaded = $element.data("loaded");
		
		if (!loaded) {
			$element.on("show.bs.modal", function(event) {
				var $target = $JQry(event.target);
                var $dialog = $target.find(".modal-dialog");
				var $header = $dialog.find(".modal-header");
				var $footer = $dialog.find(".modal-footer");
				var $clone = $target.children(".modal-clone");
				var $window = $dialog.find(".dyna-window");
				
				
				
                var url = $target.data("load-url");
                //url = adaptAjaxRedirection(url);
				var callbackFunction = $target.data("load-callback-function");
				var callbackFunctionArgs = $target.data("load-callback-function-args");
				var title = $target.data("title");
				var footer = $target.data("footer");
                var size = $target.data("size");

				// Header
				if (title) {
					$header.find(".modal-title").text(title);
					$header.removeClass("d-none");
				}
				
				// Footer
				if (footer) {
					$footer.removeClass("d-none");
				}
				
				// Body
				$window.children().clone().appendTo($clone);
				url = url.replace("/session/","/session/"+session_check+"/");

				
				$window.load(url, function(response, status, xhr) {
					
					if( !response.includes("modal_do_not_delete"))
						window.top.location.reload(true);
					
					// (ajax call) eq. footer
				    var options = new Object();
						// We have a get
				    options.method = "get"
				    // We don't block
				    options.asynchronous = false;
					ajaxCall(options, url);

					
					
					if (callbackFunction) {
						window[callbackFunction](callbackFunctionArgs);
					}

					// jQuery events
					$JQry(document).ready();
				});

				// Size
				if ((size === "xl") || (size === "extra-large")) {
					$dialog.addClass("modal-xl");
				} else if ((size === "lg") || (size === "large")) {
                    $dialog.addClass("modal-lg");
                } else if ((size === "sm") || (size === "small")) {
                    $dialog.addClass("modal-sm");
                }
			});

			$element.on("hide.bs.modal", function(event) {
				var $target = $JQry(event.target);
				var $window = $target.find(".dyna-window");
				var callbackFunction = $target.data("callback-function");
				var callbackFunctionArgs = $target.data("callback-function-args");
				var callbackUrl = $target.data("callback-url");
				
				$window.unbind("load");	
					
				if (callbackFunction) {
					window[callbackFunction](callbackFunctionArgs);
				}
				
				if (callbackUrl) {
					var container = null,
                        options = {
                            method : "post"
                        },
                        eventToStop = null,
                        callerId = null;
				
					directAjaxCall(container, options, callbackUrl, eventToStop, callerId);
				}
				
				$target.removeData("load-url");
				$target.removeData("load-callback-function");
				$target.removeData("load-callback-function-args");
				$target.removeData("callback-function");
				$target.removeData("callback-function-args");
				$target.removeData("callback-url");
				$target.removeData("title");
				$target.removeData("footer");
				$target.removeData("size");
				$target.removeData("backdrop");
			});
			
			$element.on("hidden.bs.modal", function(event) {
				var $target = $JQry(event.target);
                var $dialog = $target.find(".modal-dialog");
				var $header = $dialog.find(".modal-header");
				var $footer = $dialog.find(".modal-footer");
				var $clone = $target.children(".modal-clone");
				var $window = $dialog.find(".dyna-window");
				
				// Header
				$header.addClass("d-none");
				$header.find(".modal-title").empty();
				
				// Footer
				$footer.addClass("d-none");
				
				// Body
				$window.empty();
				$clone.children().appendTo($window);


				// Size
                $dialog.removeClass("modal-xl modal-lg modal-sm");
			});
			
			$element.data("loaded", true);
		}
	});
	
	
	$JQry("[data-target='#osivia-modal']").each(function(index, element) {
		var $element = $JQry(element);
		var loaded = $element.data("loaded");
		
		if (!loaded) {
			$element.click(function(event) {
				var $target = $JQry(event.target).closest("a, button");
				var loadUrl = $target.data("load-url");
				var loadCallbackFunction = $target.data("load-callback-function");
				var loadCallbackFunctionArgs = $target.data("load-callback-function-args");
				var callbackFunction = $target.data("callback-function");
				var callbackFunctionArgs = $target.data("callback-function-args");
				var callbackUrl = $target.data("callback-url");
				var title = $target.data("title");
				var footer = $target.data("footer");
                var size = $target.data("size");
                var backdrop = $target.data("backdrop");
				var $modal = $JQry("#osivia-modal");
	
				$modal.data("load-url", loadUrl);
				$modal.data("load-callback-function", loadCallbackFunction);
				$modal.data("load-callback-function-args", loadCallbackFunctionArgs);
				$modal.data("callback-function", callbackFunction);
				$modal.data("callback-function-args", callbackFunctionArgs);
				$modal.data("callback-url", callbackUrl);
				$modal.data("title", title);
				$modal.data("footer", footer);
				$modal.data("size", size);
				$modal.data("backdrop", backdrop);

				$modal.modal("show");
			});
			
			$element.data("loaded", true);
		}
	});
	
	
	$JQry("#osivia-modal [data-close-modal=true]").each(function(index, element) {
		var $modal = $JQry("#osivia-modal");
		
		$modal.modal("hide");
	});
	
});
