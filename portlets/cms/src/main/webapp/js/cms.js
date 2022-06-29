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
                    requestHeaders: ["ajax", "true", "bilto"],
                    method: "post",
                    postBody: "source=" + sourceElement ,
                    onSuccess: function (t) {
                        onAjaxSuccess(t, null);
                    }
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
    $JQry(".portal-properties-administration-profile").each(function(index, element) {
        var $element = $JQry(element);

        if (!$element.data("loaded")) {
            var $modals = $element.find(".modal");
            var $sortable = $element.find("ul.portal-properties-administration-profile-sortable");

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
                handle: ".portal-properties-administration-sortable-handle",
                items: "li",
                tolerance : "pointer",

                update : function(event, ui) {
                    var $item = $JQry(ui.item);
                    var $list = $item.closest("ul.portal-properties-administration-profile-sortable");
                    var $form = $list.closest("form");

                    // Update order
                    $list.children("li").each(function(index, element) {
                        var $element = $JQry(element);
                        var $input = $element.find("input[type=hidden][name$=order]");

                        $input.val(index);
                    });

                    // Submit form
                    $form.find("input[type=submit]").click();
                }
            });
            $sortable.disableSelection();

            $element.data("loaded", true);
        }
    });
});




$JQry(function() {
    $JQry(".portal-properties-administration-profile-detail").each(function(index, element) {
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
                        requestHeaders: ["ajax", "true", "bilto"],
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

