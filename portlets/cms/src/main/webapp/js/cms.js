function ShowMessage() {
    alert("Hello World!");
}


$JQry(function () {
    var $cmsEdition = $JQry(".cms-edition");


    if (!$cmsEdition.data("loaded")) {
        

 		var sourceElement;

        // Draggable
        $JQry(".cms-edition-draggable").draggable({
            addClasses: false,
            appendTo: "body",
            connectToFancytree: true,
            cursor: "move",
            distance: 10,
            revert: "invalid",
            revertDuration: 200,
            opacity: 1,
            cursor: "move",
            cursorAt: { top: -5, left: -5 }   ,   
            
            scroll: true, 
            scrollSpeed: 200,
            scrollSensitivity: 50,


            start: function (event, ui) {
	 			var $target = $JQry(event.target);
                sourceElement =$target.closest('.cms-edition-draggable').data("src-window");
            },

            stop: function (event, ui) {
				console.log("drop");

            }
        });


        // Droppable
        $JQry(".cms-edition-droppable").droppable({
            addClasses: false,
            hoverClass: "bg-info",
            tolerance: "pointer",

            accept: function ($draggable) {
               

                return true;
            },

            drop: function (event, ui) {
	
	 			// Target
                var $target = $JQry(event.target);
                
           

                // AJAX parameters
                var container = null;
                var options = {
                    method: "post",
                    data: {"source" : sourceElement} 
                };
                var url = $target.closest(".cms-edition-droppable").data("drop-url");
                var eventToStop = null;
                var callerId = null;

                directAjaxCall(container, options, url, eventToStop, callerId);

                
            }
        });




        // Loaded indicator
        $cmsEdition.data("loaded", true);
    }
});

$JQry(function() {
    $JQry(".portal-properties-administration-list").each(function(index, element) {
        var $element = $JQry(element);

        if (!$element.data("loaded")) {
            var $modals = $element.find(".modal");
            var $sortable = $element.find("ul.portal-properties-administration-list-sortable");

            // Update modal content
            $modals.on("show.bs.modal", function(event) {
                var $button = $JQry(event.relatedTarget);
                var $modal = $JQry(event.currentTarget);

                $modal.find("input[name=id]").val($button.data("id"));
                $modal.find("input[name=displayName]").val($button.data("display-name"));
            });

            // Submit modal form
            $modals.find("button[data-submit]").click(function(event) {
                var $button = $JQry(event.currentTarget);
                var $modal = $button.closest(".modal");

                // Close modal
                $modal.modal("hide");

                // Submit form
                $modal.find("input[type=submit]").click();
            });

            // Sortable
            $sortable.sortable({
                forcePlaceholderSize : true,
                handle: ".portal-properties-administration-list-sortable-handle",
                items: "li",
                tolerance : "pointer",

			    start: function(e, ui) {
			        // creates a temporary attribute on the element with the old index
			        $JQry(this).attr('data-previndex', ui.item.index());
			    },

                update : function(event, ui) {
                    var $item = $JQry(ui.item);
                    var $list = $item.closest("ul.portal-properties-administration-list-sortable");
                    var $form = $list.closest("form");
                    
                    $form.find("input[type=hidden][name=sortTarget]"). val(ui.item.index());
                    $form.find("input[type=hidden][name=sortSrc]"). val($JQry(this).attr('data-previndex'));
                    $JQry(this).removeAttr('data-previndex');
                    
                    var action =  $sortable.data("action");
                    
                    $form.find("input[type=hidden][name=formAction]"). val( action);
 
                    // Submit form
                    $form.find("input[type=submit]").click();
                }
            });
            $sortable.disableSelection();


            $selects = $element.find('select.move-select');
            $selects.on('change', function()
            {
                $this = $JQry(this);
                $link = $this.siblings( ".moveLink" );
                href =  $link.attr('href');
                href = href + "&destIndex="+ this.value;
                $link.attr('href', href);
                $link.click();
                

            });
            


            $element.data("loaded", true);
        }
    });
});




$JQry(function() {
    $JQry(".portal-properties-administration-detail-modal").each(function(index, element) {
        var $element = $JQry(element);

        if (!$element.data("loaded")) {

            // Submit modal form
            $element.find("a.callBack").each(function(index, element) {
                var $element = $JQry(element);
                var $modal = $JQry("#osivia-modal");

                // Close modal
                $modal.modal("hide");

                    // Save location
                    var container = null;
                    var options = {
                        method: "post"
                    };
                    var url = $element.attr('href');
                    var eventToStop = null;
                    var callerId = null;
				// Restore state
                view_state = $modal.data("saved_state");                    
                directAjaxCall(container, options, url, eventToStop, callerId);
            });

            $element.data("loaded", true);
        }
    });
});





$JQry(function() {
	
	$JQry("input[data-import-submit-id]").each(function(index, element) {
		var $element = $JQry(element);

		if (!$element.data("loaded")) {
			$element.change(function(event) {
				var $target = $JQry(event.target);
				
				var $repositoryName= $JQry("#" + $target.data("import-repository-name-id"));
				$repositoryName.val( $target.data("repository-name"));
				
				var $submit = $JQry("#" + $target.data("import-submit-id"));
				
				
				$submit.click();
			});
			
			$element.data("loaded", true);
		}
	});
	
});


$JQry(function() {
    
    $JQry("input[data-merge]").each(function(index, element) {
        var $element = $JQry(element);

        if (!$element.data("loaded")) {
            $element.change(function(event) {
                var $target = $JQry(event.target);
            
                var $submit = $JQry("#" + $target.data("merge")); 
                $submit.click();
            });
            
            
            
            $element.data("loaded", true);
        }
    });
    
});






