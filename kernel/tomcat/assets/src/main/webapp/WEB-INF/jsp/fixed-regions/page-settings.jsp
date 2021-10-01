<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op" %>

<%--Modal--%>
<div id="osivia-modal" class="modal fade" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <%--Header--%>
            <div class="modal-header d-none">
                <h5 class="modal-title"></h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>

            <%--Body--%>
            <div class="modal-body">
                <div class="dyna-region">
                    <div id="modal-region">
                        <div class="dyna-window">
                            <div id="modal-window" class="partial-refresh-window">
                                <div class="dyna-window-content">
                                    <div class="p-4 text-center">
                                        <div class="spinner-border" role="status">
                                            <span class="sr-only">Loading...</span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <%--Footer--%>
            <div class="modal-footer d-none">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">
                    <span><op:translate key="CLOSE"/></span>
                </button>
            </div>
        </div>
    </div>

    <div class="modal-clone d-none"></div>
    
    
</div>


<div id="react-container-factory"></div>
