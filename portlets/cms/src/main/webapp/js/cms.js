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

