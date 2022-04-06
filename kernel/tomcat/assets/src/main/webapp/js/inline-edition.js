

$JQry(function() {

    $JQry("select.select2.select2-inline-edition").each(function(index, element) {
        var $element = $JQry(element);

        if (!$element.data("loaded")) {
            var url = $element.data("url");
            var options = {
                closeOnSelect : true,
                containerCssClass : "select2-inline-edition-container",
                dropdownCssClass : "select2-inline-edition-dropdown",
                theme : "bootstrap4",
                width : "100%"
            };
            
            var tags = false;
            
            
            if( $element.attr('data-tags') == "true")	{
            	tags = true;
            }
            
            if( tags == true)	{
               	options["dropdownCssClass"] = 'd-none';
            	options["tokenSeparators"] = [','];
            	
            	
            	// BUG-FIX #completion
    	    	// When dropdownClass is hidden, the current item may not have been inserted
    	    	// ex: 'histoire-géographie' is already tagged and user enters 'histoire'
    	    	// -> select2 selects histoire-géographie and ignores histoire
            	
            	// Add listener on container because input field is recreated at each insertion
            	$element.parent().on("keyup",".select2-container--bootstrap4", function (event) {
            		var key = event.which;
            		// Carriage return
            	    if (key === 13) {
            	    	// Ensure value has been inserted
            	    	var newValue = $element.data("tag-value");
            	        if(  ! $element.val().includes(newValue))	{
        	        	      // Append it to the select
           	        		 selected = $element.val();
           	        		 selected.push(newValue);
           	        		 $element.val(selected).trigger('change');    
            	        }
           	        } 	else	{
           	        	// Memorize current value
           	        	var source = event.target ;
           	        	$element.data("tag-value", source.value);
           	        }
                });
            }

            if (url !== undefined) {
                options["ajax"] = {
                    url : adaptAjaxRedirection(url),
                    dataType : "json",
                    delay : 250,
                    data : function(params) {
                        return {
                            filter : params.term,
                        };
                    },
                    processResults : function(data, params) {
                        return {
                            results : data
                        };
                    },
                    cache : true,
                    
                     
                    transport: function (params, success, failure) {
                        var $request = $JQry.ajax(params);

                        $request.then(success);
                        $request.defaultFailure=failure;
                        $request.fail(select2LoadFailure);

                        return $request;
                      }
                };

                options["escapeMarkup"] = function(markup) {
                    return markup;
                };

                options["templateResult"] = function(params) {
                    var $result = $JQry(document.createElement("span"));

                    if (params.loading) {
                        $result.text(params.text);
                    } else {
                        $result.text(params.text);
                        if (params.level !== undefined) {
                            $result.addClass("level-" + params.level);
                        }
                        if (params.optgroup) {
                            $result.addClass("optgroup");
                        }
                    }

                    return $result;
                };

                options["templateSelection"] = function (params) {
                    var $result = $JQry(document.createElement("span"));
                    $result.addClass("text-truncate");
                    $result.text(params.text);

                    return $result;
                };
            }

            
            // Internationalization
            options["language"] = {};
            if ($element.data("input-too-short") !== undefined) {
                options["language"]["inputTooShort"] = function () {
                    return $element.data("input-too-short");
                }
            }
            if ($element.data("error-loading") !== undefined) {
                options["language"]["errorLoading"] = function () {
                    return $element.data("error-loading");
                }
            }
            if ($element.data("loading-more") !== undefined) {
                options["language"]["loadingMore"] = function () {
                    return $element.data("loading-more");
                }
            }
            if ($element.data("searching") !== undefined) {
                options["language"]["searching"] = function () {
                    return $element.data("searching");
                }
            }
            if ($element.data("no-results") !== undefined) {
                options["language"]["noResults"] = function () {
                    return $element.data("no-results");
                }
            }

            
            $element.select2(options);
            

            // Close on unselect
            $element.on("select2:unselect", function (event) {
                setTimeout(function() {
                    var search = $element.siblings().find(".select2-search__field");
                    search.val("");
                    $element.select2("close");
                }, 100);
            });
          

            
            $element.change(function(event) {
                var $form = $element.closest("form");
                var $submit = $form.find("button[type=submit], input[type=submit]");

                $submit.click();
            });


            $element.data("loaded", true);
        }
    });


    $JQry(".inline-edition textarea, .inline-edition input").each(function(index, element) {
        var $element = $JQry(element);

        if (!$element.data("loaded")) {
            var timer;

            $element.change(function(event) {
            	
                var $target = $JQry(event.target);
                if( $target.hasClass('select2-search__field') == false)	{
            	
	                // Clear timer
	                clearTimeout(timer);
	
	                var $target = $JQry(event.target);
	                var $form = $target.closest("form");
	                var $submit = $form.find("button[type=submit], input[type=submit]");
	
	                $submit.click();
                }
            });

            $element.keyup(function(event) {
            	
                var $target = $JQry(event.target);
                if( $target.hasClass('select2-search__field') == false)	{
	            	
	                // Clear timer
	                clearTimeout(timer);
	
	                timer = setTimeout(function() {
	                    var $target = $JQry(event.target);
	                    var $form = $target.closest("form");
	                    var $submit = $form.find("button[type=submit], input[type=submit]");
	
	                    $submit.click();
	                }, 500);
                }
            });

            $element.data("loaded", true);
        }
    });


    $JQry("[contenteditable=true]").each(function(index, element) {
        var $element = $JQry(element);

        if (!$element.data("loaded")) {
            var timer;

            $element.keyup(function(event) {
                // Clear timer
                clearTimeout(timer);

                timer = setTimeout(function() {
                    var $target = $JQry(event.target);
                    var $form = $target.closest("form");
                    var $input = $form.find("input[type=hidden][name='inline-values']");
                    var $submit = $form.find("input[type=submit]");

                    $input.val($target.get(0).innerText);
                    $submit.click();
                }, 500);
            });


            $element.data("loaded", true);
        }
    });
    
});    



function select2LoadFailure( request, errorType ) {
	
	var defaultFailure = true
	
	if( errorType == 'parsererror')	{
		if( handleAjaxRedirection(request.responseText))	{
				defaultFailure = false;
		}
	}
	
	if( defaultFailure == true)
		request.defaultFailure();
           
}