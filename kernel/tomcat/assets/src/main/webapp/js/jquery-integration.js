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


function updateFeedback( element) {
  
       var $element = $JQry(element);
       $element.closest('.form-group').find('.invalid-feedback').each(function(index, msg){
           
           var message = element.validationMessage;
               
           // Don't display required message (style is sufficient)
            if($element.val() == "")
                message = "";
                                                  
            if($element.data("error") != null)
                  message = $element.data("error");
                    
             var $msg = $JQry(msg);
             $msg.text( message);
       });
   
}


function fieldValidation( field) {

    if(field.validationMessage)   {
        updateFeedback( field);
    }
    // Force the display (bootstrap css rule)
    field.parentElement.classList.add('was-validated')  

}



$JQry(function() {
    $JQry('.needs-validation').each(function(index, element) {
        var $element = $JQry(element),
            loaded = $element.data("validation-loaded");

        if (!loaded) {
             $element.find(':input').on('keyup change', function () {

             window.setTimeout( fieldValidation ,              
                                500,
                                this);
                 

            });
                
            // prevent submission
            element.addEventListener('submit', function(event) {
                if (!element.checkValidity()) {
                    
                    var focusField = null;
 
                    // display feedback on submit                  
                    $element.find(':input').each(function(index, child){
                        
                         if( child.validationMessage)    {
                              updateFeedback( child);
                               
                          if( focusField == null)
                                focusField = child;
                        }
                    });
                    
                   
                    event.preventDefault()
                    event.stopPropagation()
                    
                    if( focusField != null) {
                        focusField.focus();
                    }
                }

                element.classList.add('was-validated')
            }, false)

            $element.data("validation-loaded", true);
        }
    });
});






