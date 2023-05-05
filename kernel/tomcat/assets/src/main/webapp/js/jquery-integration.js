var $JQry = jQuery.noConflict();


// Redefine jQuery.ready() for AJAX reload
(function($, undefined) {
    var isFired = false;
    var oldReady = jQuery.fn.ready;
    $(function() {
        isFired = true;
        $(document).ready();
    });
    jQuery.fn.ready = function(fn) {
        if(fn === undefined) {
            $(document).trigger('_is_ready');
            return;
        }
        if(isFired) {
            window.setTimeout(fn, 1);
        }
        $(document).bind('_is_ready', fn);
    };
})(jQuery);


// bs5 validation
$JQry(function() {
    $JQry('.needs-validation').each(function(index, element) {
        var $element = $JQry(element),
            loaded = $element.data("validation-loaded");

        if (!loaded) {
            
            // prevent submission
            element.addEventListener('submit', function(event) {
                if (!element.checkValidity()) {
 
                    // display feedback                   
                    $element.find(':input').each(function(index, child){
                         if( child.validationMessage)    {
                               var $child = $JQry(child);
                               $child.closest('.form-group').find('.invalid-feedback').each(function(index, msg){
                                   
                                   var message = child.validationMessage;
                                   // Don't display required message (style is sufficient)
                                   if($child.val() == "")
                                        message = "";
                                   var $msg = $JQry(msg);
                                   $msg.text( message);
                               });
                        }
                    });
                    
                    event.preventDefault()
                    event.stopPropagation()
                }

                element.classList.add('was-validated')
            }, false)

            $element.data("validation-loaded", true);
        }
    });
});






