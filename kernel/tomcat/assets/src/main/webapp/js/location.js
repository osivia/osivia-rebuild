$JQry(function() {
	var $location = $JQry("[data-location-url]");

	if (!$location.data("location-loaded")) {
		// Location popover
		$location.find("a[data-location-path]").each(function(index, element) {
		
			var $element = $JQry(element);
			
			$element.popover({
				content: function () {
					var $this = $JQry(this);
					var result;
	
					jQuery.ajax({
						url: $location.data("location-url"),
						async: false,
						cache: true,
						headers: {
							"Cache-Control": "max-age=86400, public"
						},
						data: {
							path: $this.data("location-path")
						},
						dataType: "html",
						success: function (data, status, xhr) {
							result = data;
						}
					});
	
					return result;
				},
				html: true,
				placement: "bottom",
				delay: { hide: 200 },
				trigger: "focus"
			});
			
			$element.on('shown.bs.popover', function () {
				
				// Add event to process link in ajax mode
				var $this = $JQry(this);
				var popupId = $this.attr("aria-describedby");
				
				
				var $body = $JQry("[id="+popupId+"]");
				$body.find("a").each(function(index, anchor) {
					  var $anchor = $JQry(anchor);
					  $anchor.on( "click", function( event) {
						  
						  	event.preventDefault();
						  	
							// Non Ajax Response
					        var options = new Object();
					        
					    	// We have a get
					        options.method = "get"
					        	
					        // We don't block
					        options.asynchronous = false;

					    	directAjaxCall(null,options, $anchor.attr('href'),null);
					  });
				  });
			});
			
			$element.on('hidden.bs.popover', function () {
				// Remove event to process link in ajax mode
				var $this = $JQry(this);
				var popupId = $this.attr("aria-describedby");
				
				var $body = $JQry("[id="+popupId+"]");
				$body.find("a").each(function(index, anchor) {
					var $anchor = $JQry(anchor);
					$anchor.off();
				  });			
			});
			
		
		});
		

		// Loaded indicator
		$location.data("location-loaded", true);
	}

});
