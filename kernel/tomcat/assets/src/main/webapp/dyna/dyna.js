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


var localCache = {
    /**
     * timeout for cache in millis
     * @type {number}
     */
    timeout: 300000,
    /**
     * @type {{_: number, data: {}}}
     **/
    data: {},
    remove: function (url) {
        delete localCache.data[url];
    },
    exist: function (url) {
        return !!localCache.data[url] && ((new Date().getTime() - localCache.data[url]._) < localCache.timeout);
    },
    get: function (url) {

        return localCache.data[url].data;
    },
    set: function (url, cachedData, callback) {
        localCache.remove(url);
        localCache.data[url] = {
            _: new Date().getTime(),
            data: cachedData
        };
        if ($JQry.isFunction(callback)) callback(cachedData);
    }
};

/*
Cache scripts loaded in ajax
(defined at a portlet level)
*/
$JQry.ajaxPrefilter(function (options, originalOptions, jqXHR) {
    let staticResource = false;
    if (options.dataType === 'script' || originalOptions.dataType === 'script')
        staticResource = true;

    if (staticResource) {
        const complete = originalOptions.complete || $JQry.noop;
        const url = originalOptions.url;
        //remove jQuery cache as we have our own localCache
        options.cache = false;
        options.beforeSend = function () {
            if (localCache.exist(url)) {
                complete(localCache.get(url));
                return false;
            }
            return true;
        };
        options.complete = function (data, textStatus) {
            localCache.set(url, data, complete);
        };
    }
});


function sendData(action, windowId, fromPos, fromRegionId, toPos, toRegionId) {
    $JQry.ajax({
        url: server_base_url + "/ajax",
        method: "post",
        headers: {"ajax": true},
        data: {
            "action": action,
            "windowId": windowId,
            "fromPos": fromPos,
            "fromRegion": fromRegionId,
            "toPos": toPos,
            "toRegion": toRegionId
        },
        statusCode: {
            404: function (xhr, textStatus, errorThrown) {
                alert("Error 404: location " + textStatus + " was not found.");
            }
        },
        success: function (data, status, xhr) {
        },
        error: function (xhr, textStatus, errorThrown) {
            alert("Error " + textStatus + " -- " + errorThrown);
        }
    });
}


// Check that the URL starts with the provided prefix
function isURLAccepted(url) {
    var urlPrefix = server_base_url;
    if (urlPrefix.endsWith("/auth/")) {
        urlPrefix = urlPrefix.substring(0, urlPrefix.length - "/auth/".length);
    }
    if (url.indexOf("&action=f") !== -1) {
        // Pas d'ajax pour les ressources
        return false;
    } else if (url.indexOf("&action=b") !== -1) {
        // Pas d'ajax pour les ressources
        return false;
    }

    var indexOfSessionId = urlPrefix.indexOf(";jsessionid");
    if (indexOfSessionId > 0) {
        urlPrefix = urlPrefix.substring(0, indexOfSessionId
            - ";jsessionid".length - 1);
    }

    var scheme = "";
    if (url.indexOf("http://") === 0) {
        scheme = "http://";
    } else if (url.indexOf("https://") === 0) {
        scheme = "https://";
    }
    if (scheme) {
        var indexOfSlash = url.indexOf("/", scheme.length);
        if (indexOfSlash < 0) {
            return false;
        } else {
            var host = window.location.host;
            var urlHost = url.substring(scheme.length, indexOfSlash);

            if (urlHost !== host)
                return false;

            var path = url.substring(indexOfSlash,);
            if (path.indexOf(urlPrefix) !== 0) {
                return false;
            }
        }
    } else if (url.indexOf(urlPrefix) !== 0) {
        return false;
    }

    //
    return true;
}

function bilto(event) {

    // Locate the div container of the window
    const $currentTarget = $JQry(event.currentTarget);
    const $container = $currentTarget.closest("div.dyna-window");


    // We found the window
    if ($container.length) {
        const options = {};
        let url;

        // if unknow source (IMG, SPAN, ...) , search the ancestor A, INPUT or BUTTON
        const $source = $currentTarget.closest("a, input, button");
        const source = $source.get(0);
        if (!$source.length) {
            return;
        }

        if (source.disabled) {
            return;
        }

        if (!$source.hasClass("ajax-link") && $source.closest(".no-ajax-link").length) {
            return;
        }

        //
        if (source.nodeName === "A") {
            // Check we can handle this URL
            if (isURLAccepted(source.href)) {
                // Set URL
                url = source.href;
                // We have a get
                options.method = "get"
                // We don't block
                options.async = true;
            }
        } else if ((source.nodeName === "INPUT" || source.nodeName === "BUTTON") && (source.type === "submit" || source.type === "image")) {
            // Find enclosing form
            let current = source.parentNode;
            while (current.nodeName !== 'FORM' && current.nodeName !== 'BODY') {
                current = current.parentNode;
            }

            // Check we have a form and use it
            if (current.nodeName === 'FORM') {
                const enctype = current.enctype;

                // We don't handle file upload for now
                if (enctype !== "multipart/form-data") {
                    // Check it is a POST
                    if (current.method.toLowerCase() === "post") {
                        // Check we can handle this URL
                        if (isURLAccepted(current.action)) {
                            // Set URL
                            url = current.action;

                            const $form = $JQry(current);

                            // Set the specified enctype
                            options.enctype = enctype;
                            options.async = true;
                            options.method = "post";
                            options.data = $form.serialize();
                            options.data.submit = source.name;
                        }
                    }
                } else {
                    if (!(event.type === "popstate")) {
                        event.preventDefault();
                    }

                    const $form = $JQry(current);
                    const formdata = (window.FormData) ? new FormData($form[0]) : null;
                    let data;
                    if (formdata != null) {
                        formdata.append("hash", false);
                        formdata.append(source.name, source.value);
                        data = formdata;
                    } else {
                        data = $form.serialize();
                    }

                    $JQry.ajax({
                        url: current.action,
                        method: "post",
                        headers: {"ajax": true, "session_check": session_check, "view_state": view_state},
                        contentType: false, // obligatoire pour de l'upload
                        processData: false, // obligatoire pour de l'upload
                        dataType: "json",
                        data: data,
                        success: function (data, status, xhr) {
                            onAjaxSuccess(data, null, true, null, null, url);
                        }
                    });
                }
            }
        }

        // Handle links here
        if (url != null) {
            directAjaxCall($container.get(0), options, url, event, null);
        }
    }
}


//Get unique key per session/location
function getScrollKey() {
    var session = readCookie("JSESSIONID");
    if (session == null)
        session = "";
    key = session + "/scroll/" + window.location.href;
    return key;
}


//Explicits Ajax calls from portlet
function updatePortletContent(item, url) {
    var ajaxCall = true;

    var container = Element.up(item, "div.dyna-window");
    if (container == null) {
        ajaxCall = false;
    }

    if (!item.hasClassName("ajax-link") && (item.hasClassName("no-ajax-link") || (Element.up(item, ".no-ajax-link") != null))) {
        ajaxCall = false;
    }

    if (ajaxCall) {
        // Set URL
        var options = new Object();

        // We have a get
        options.method = "get"

        // We don't block
        options.async = true;

        directAjaxCall(null, options, url, null, null);
    } else {
        document.location = url;
    }
}


function onAjaxSuccess(responseText, callerId, multipart, popState, eventToStop, url) {
    let stateObject;
    let i;
    let $dstContainer;
    let srcContainer;
    let markup;
    let resp = "";

    if (multipart) {
        resp = responseText;
    } else {
        if (responseText.length > 0) {
            try {
                eval("resp =" + responseText);
            } catch (e) {
                // If modal, redirect to history page
                if ($JQry('#osivia-modal').is(':visible')) {
                    window.location = history.state.fullUrl;
                    return;
                }

                window.location = url;
                return;
            }
        }
    }

    // call save state
    let componentStates = new Map();
    $JQry("[data-state-method]").each(function (index, element) {
        const $element = $JQry(element);
        const componentState = window[$element.data("state-method")]($element, 'save');

        componentStates.set($element.data("state-id"), componentState);
    })

    let originalId;

    let filler;
    if (resp.type === "update_markup") {
        let regionName;
        let id;

        // New layout
        const layout = resp.layout;

        let popping;
        if ((eventToStop != null) && (eventToStop.type === "popstate")) {
            popping = true;
        }

        // Save scroll
        if (resp.change_state === "toMax") {
            currentNormalScroll = window.scrollY;
        }

        let newPage = false;
        let preventHistory = false;
        let modale = false;

        if (layout != null) {
            // New layout
            copyLayout(layout);
            newPage = true;
        }

        // Is it a modal ?
        for (regionName in resp.regions) {
            for (i = 0; i < resp.regions[regionName].length; i++) {
                if (regionName === "modal-region") {
                    modale = true;

                    // Modal window must not be reloadable
                    preventHistory = true;
                }
            }
        }


        /* Update portlets resources */
        if (modale === false)
            updateResources(resp.resources, layout);
        else
            updateResources(resp.resources, null);


        // Iterate all changes
        for (id in resp.fragments) {
            originalId = id;
            id = id.replace(':', '_');

            var matchingElt = document.getElementById(id);

            // Different than 1 is not good
            if (matchingElt != null) {
                const dstContainer = matchingElt;
                if (dstContainer != null) {
                    // Get markup fragment
                    markup = resp.fragments[originalId];

                    // Create a temporary element and paste the innerHTML in it
                    srcContainer = document.createElement("div");

                    const $srcContainer = $JQry(srcContainer);
                    $srcContainer.append(markup);

                    // Regions modifications
                    let regionsModifications = false;

                    $srcContainer.find('[data-ajax-region-modified]').each(function (index, src) {
                        regionsModifications = true;

                        const $src = $JQry(src);
                        const ajaxRegion = $src.data("ajax-region-modified");

                        const $dstContainer = $JQry(dstContainer);

                        $dstContainer.find('[data-ajax-region=' + ajaxRegion + ']').each(function (index, dst) {
                            copyNodes(src, dst);
                        });
                    });

                    if (regionsModifications === false) {
                        // Empty response + hidePortlet : reinitialize then window
                        let emptyResponse = true;
                        $srcContainer.find('.dyna-portlet').each(function () {
                            emptyResponse = false;
                        });

                        if (emptyResponse === true) {
                            // Copy the region content
                            copyInnerHTML(srcContainer, dstContainer, "dyna-window-content");
                        } else {
                            // Copy the region content
                            copyInnerHTML(srcContainer, dstContainer, "dyna-portlet");
                            copyInnerHTML(srcContainer, dstContainer, "dyna-decoration");
                        }
                    }

                    if (newPage === false) {
                        // StartDynamic window in normal mode
                        $dstContainer = $JQry(dstContainer);
                        $dstContainer.find('#' + id).each(function (index) {
                            observePortlet(this);
                        });
                    }
                }

            } else {
                // It may be a new windows
                // Check the regions ...
                for (regionName in resp.regions) {
                    for (i = 0; i < resp.regions[regionName].length; i++) {
                        const windowId = resp.regions[regionName][i].replace(':', '_');

                        if (windowId === id) {
                            const matchingWindow = document.getElementById(windowId);

                            if (matchingWindow == null) {
                                // New window
                                // <div class="dyna-window"><div id="cG9ydGFsQQ_e_e_dcGFnZUEtYWpheA_e_e_dd2luQXBhZ2VBLWFqYXg_e" class="partial-refresh-window">
                                const divRegion = document.getElementById(regionName);

                                if (divRegion != null) {
                                    if (regionName === "modal-region") {
                                        // Modal region already contains a default window that must be replaced
                                        const $target = $JQry(divRegion);
                                        $target.empty();
                                    }

                                    // Prepare new window
                                    const newWindowDiv = document.createElement("div");
                                    newWindowDiv.className = "dyna-window";
                                    const partialWindowDiv = document.createElement("div");
                                    partialWindowDiv.id = windowId;
                                    partialWindowDiv.className = "partial-refresh-window";
                                    newWindowDiv.appendChild(partialWindowDiv);

                                    // Search for first inserted windows to insert before it
                                    const children = divRegion.children;

                                    let insertBefore = null;

                                    for (let iDomWindow = 0; iDomWindow < children.length && insertBefore == null; iDomWindow++) {
                                        const domWindow = children[iDomWindow];
                                        const className = domWindow.className;
                                        if (className === "dyna-window") {
                                            const insertedId = domWindow.firstChild.id;

                                            // Browse by order to find if this window is after the window to insert
                                            for (let j = i + 1; j < resp.regions[regionName].length && insertBefore == null; j++) {
                                                const orderedWindowId = resp.regions[regionName][j].replace(':', '_');
                                                if (orderedWindowId === insertedId) {
                                                    insertBefore = domWindow;
                                                }
                                            }
                                        }
                                    }

                                    if (insertBefore == null)
                                        divRegion.appendChild(newWindowDiv);
                                    else
                                        divRegion.insertBefore(newWindowDiv, insertBefore);


                                    // Get markup fragment
                                    markup = resp.fragments[originalId];

                                    // Create a temporary element and paste the innerHTML in it
                                    srcContainer = document.createElement("div");

                                    // Insert the markup in the div
                                    const $srcContainer = $JQry(srcContainer);
                                    $srcContainer.append(markup);

                                    // Copy the region content
                                    copyInnerHTML(srcContainer, newWindowDiv, "partial-refresh-window");

                                    if (newPage === false) {
                                        // StartDynamic window in normal mode
                                        $dstContainer = $JQry(newWindowDiv);
                                        $dstContainer.find('#' + id).each(function (index) {
                                            observePortlet(this);
                                        });
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (newPage) {
            if (modale === false) {
                const modalElement = document.getElementById('osivia-modal');
                const modal = bootstrap.Modal.getOrCreateInstance(modalElement);
                modal.hide();
            }

            observePortlets();
        }

        // update view state
        if (resp.view_state != null) {
            view_state = resp.view_state;
        }

        // update view state
        if (resp.session_check != null) {
            session_check = resp.session_check;
        }

        // update portal_redirection
        if (resp.portal_redirection != null) {
            portal_redirection = resp.portal_redirection;
        } else {
            portal_redirection = null;
        }

        // Save components state in history
        if (popping === undefined && resp.restore_url !== "" && preventHistory === false) {
            if (resp.push_history === "true") {
                // update the current page
                if (history.state != null) {
                    stateObject = history.state;
                    stateObject.currentScroll = currentScroll;
                    if (componentStates !== undefined) {
                        stateObject.componentStates = componentStates;
                    }
                    history.replaceState(stateObject, "", stateObject.fullUrl);
                }

                // Add the new page
                const newState = {
                    url: resp.pop_url,
                    viewState: view_state,
                    currentScroll: 0,
                    fullUrl: resp.full_state_url
                };

                if (componentStates !== undefined) {
                    newState.componentStates = componentStates;
                }

                history.pushState(newState, "", resp.full_state_url);
            } else {
                // update the current page
                if (history.state != null) {
                    stateObject = {
                        url: resp.pop_url,
                        viewState: view_state,
                        currentScroll: currentScroll,
                        componentStates: componentStates,
                        fullUrl: resp.full_state_url
                    };
                    history.replaceState(stateObject, "", stateObject.fullUrl);
                }
            }
        }

        // Call jQuery.ready() events
        $JQry(document).ready();

        //  restore component states
        let restoreComponentStates = null;

        if (popState !== undefined && popState != null) {
            restoreComponentStates = popState.componentStates;
        } else {
            if (history.state != null) {
                restoreComponentStates = history.state.componentStates;
            }
        }

        if (restoreComponentStates != null) {
            $JQry("[data-state-method]").each(function (index, element) {
                const $element = $JQry(element);
                const id = $element.data("state-id");
                const componentState = restoreComponentStates.get(id);
                if (componentState != null) {
                    window[$element.data("state-method")]($element, 'restore', componentState);
                }
            })
        }

        // Restore cursor
        filler = $JQry(".portlet-filler").first();
        if (filler !== undefined && filler.length > 0) {
            if (popState !== undefined && popState != null) {
                if (popState.currentScroll !== 0) {
                    filler.scrollTop(popState.currentScroll);
                }
            } else {
                if (resp.page_changed === "false") {
                    filler = $JQry(".portlet-filler").first();
                    if (currentScroll !== 0) {
                        filler.scrollTop(currentScroll);
                    }
                }
            }
        } else {
            if (popState !== undefined && popState != null) {
                if (popState.currentScroll !== 0) {
                    window.scrollTo({top: popState.currentScroll, left: 0, behavior: 'instant'});
                }
            } else {
                if (modale === false) {
                    if ((resp.page_changed === "true" || resp.change_state === "toMax")) {
                        window.scrollTo({top: 0, left: 0, behavior: 'instant'})
                    }

                    if (resp.change_state === "toNormal") {
                        window.scrollTo({top: currentNormalScroll, left: 0, behavior: 'instant'});
                    }
                }
            }
        }


        if (resp.regions["header-metadata"] !== undefined)
            synchronizeMetadatas();


        $JQry(".notification-container").delay(10000).fadeOut(2000);

        for (id in resp.async_windows) {
            var url = resp.async_windows[id];
            // Set URL
            const options = {};

            // We have a get
            options.method = "get"

            // We don't block
            options.async = true;

            directAjaxCall(null, options, url, null, null, null, null, id);
        }
    } else if (resp.type === "update_page") {
        if (resp.location === "/back") {
            reload(history.state, null, false)
            return;
        }

        if (resp.location === "/back-refresh") {
            reload(history.state, null, true)
            return;
        }

        if (resp.location === "/refresh") {
            reload(history.state, null, true)
            return;
        }

        document.location = resp.location;
    }
}


function getHeaders(popState, refresh, asyncWindow) {

    // Setup headers
    var headers = new Object();

    headers.ajax = true;


    var headerState = null;
    if (popState !== undefined && popState != null) {
        headerState = popState.viewState;

    } else {
        // Add the view state value
        if (view_state != null) {
            headerState = view_state;
        }
    }

    if (headerState != null) {
        headers.view_state = headerState;
    }

    if (refresh !== undefined) {
        if (refresh != null)
            headers.refresh = refresh;
    }


    if (session_check != null) {
        headers.session_check = session_check;
    }

    if (asyncWindow !== undefined) {
        headers.asyncWindow = asyncWindow;

    }

    return headers;
}


function directAjaxCall(ajaxContext, options, url, eventToStop, callerId, popState, refresh, asyncWindow) {
    const headers = getHeaders(popState, refresh, asyncWindow);

    if (ajaxContext !== undefined) {
        headers.ajax_context = ajaxContext;
    }

    // Save current scroll position
    currentScroll = 0;

    if ((eventToStop !== undefined) && (eventToStop !== null) && !(eventToStop.type === "popstate")) {
        eventToStop.preventDefault();
        eventToStop.stopPropagation();

    }

    const ajaxSettings = {
        ...options,
        url: url,
        headers: headers,
        success: function (data, status, xhr) {
            onAjaxSuccess(data, callerId, null, popState, eventToStop, url);
        }
    }
    $JQry.ajax(ajaxSettings);
}


function reload(state, event, refresh) {
    var options = new Object();
    options.method = "get";
    options.async = true;
    directAjaxCall(null, options, state.url, event, null, state, refresh);
}


window.onpopstate = function (event) {
    if (event.state) {
        reload(event.state, event)
    }

}


function updateResources(newHeaderResources, layout) {

    if (newHeaderResources != null) {


        // Remove unused scripts

        if (layout != null) {

            var section = false;

            let parent = document.querySelector("head");
            if (parent) {

                let removing = false;
                let child = parent.firstChild;
                let next = null;
                // While we still have child elements to process...
                while (child) {
                    // If we're already removing, remember that
                    let removeThis = removing;
                    // Before we remove anything, identify the next child to visit
                    next = child.nextSibling;
                    // Is this a comment node?
                    if (child.nodeType === Node.COMMENT_NODE) {
                        if (child.nodeValue.includes("portlets-begin")) {
                            // It's the node that tells us to start removing:
                            // Turn on our flag and also remove this node
                            removing = true;
                            removeThis = false;
                            section = true;
                        } else if (child.nodeValue.includes("portlets-end")) {
                            // It's the node that tells us to stop removing:
                            // Turn off our flag, but do remove this node
                            removing = false;
                            removeThis = false;
                        }
                    }
                    if (removeThis) {
                        var remove = true;
                        for (var iNewHeader = 0; iNewHeader < newHeaderResources.length; iNewHeader++) {
                            var newHeader = newHeaderResources[iNewHeader];

                            if (newHeader.tag == "LINK" && child.nodeName == "LINK") {
                                if (location.origin + newHeader.href == child.href)
                                    remove = false;
                            }
                            if (newHeader.tag == "SCRIPT" && child.nodeName == "SCRIPT") {
                                if (location.origin + newHeader.src == child.src)
                                    remove = false;
                            }
                        }

                        if (remove)
                            parent.removeChild(child);
                    }

                    // Move on to next child
                    child = next;
                }
            }

            if (section == false) {
                // Create portlet section

                var commentBegin = document.createComment("portlets-begin");
                parent.appendChild(commentBegin);
                var commentEnd = document.createComment("portlets-end");
                parent.appendChild(commentEnd);
            }


        }


        for (var iNewHeader = 0; iNewHeader < newHeaderResources.length; iNewHeader++) {

            var newHeader = newHeaderResources[iNewHeader];

            var head = document.getElementsByTagName('head')[0];
            var headers = head.children;
            var insert = true;


            for (var i = 0; i < headers.length; i++) {
                if (newHeader.tag == "LINK" && headers[i].tagName == "LINK") {
                    if (location.origin + newHeader.href == headers[i].href && headers[i].rel != "preload")
                        insert = false;
                }
                if (newHeader.tag == "SCRIPT" && headers[i].tagName == "SCRIPT") {
                    if (location.origin + newHeader.src == headers[i].src)
                        insert = false;
                }
            }

            var tagToInsert;

            if (insert && newHeader.tag == "LINK") {

                let parent = document.querySelector("head");
                if (parent) {
                    addPreload(newHeader.href);

                }


                tagToInsert = document.createElement('LINK');

                tagToInsert.rel = newHeader.rel;
                tagToInsert.type = newHeader.type;
                tagToInsert.href = newHeader.href;
                if (newHeader.media != undefined)
                    tagToInsert.media = newHeader.media;
            }

            if (insert && newHeader.tag == "SCRIPT") {
                tagToInsert = document.createElement('SCRIPT');
                tagToInsert.type = newHeader.type;
                tagToInsert.src = newHeader.src;
                // Important to preserve order
                tagToInsert.async = false;
            }

            if (tagToInsert) {
                $JQry("head").contents().filter(function () {
                    return this.nodeType == 8;
                }).each(function (i, e) {
                    if ($JQry.trim(e.nodeValue) == "portlets-end") {
                        $JQry(tagToInsert).insertBefore(e);
                    }
                });
            }
        }


    }
}


/*
 * Copy the inner content of two zones of the provided containers.
 * The zone are found using the css class names. The operation
 * will succeed only if there is exactly one zone in each container.
 */
function copyLayout(layout) {
    // TODO : a supprimer
    if (layout.includes("modal_do_not_delete"))
        return;

    // Create a temporary element and paste the innerHTML in it
    const srcContainer = document.createElement("html");
    const $srcContainer = $JQry(srcContainer);

    srcContainer.innerHTML = layout;

    const srcs = $srcContainer.find("body");
    if (srcs.length === 1) {
        document.body = srcs[0];
    }

    /* Insert dynamic resources from theme */
    insertPortalResources(srcContainer, "theme-link");
    insertPortalResources(srcContainer, "theme-script");
}

function insertPortalResources(srcContainer, section) {
    let src;

    // Check if new resources must be inserted
    const $srcContainer = $JQry(srcContainer);
    const srcs = $srcContainer.find("head");
    let changes = false;

    if (srcs.length === 1) {
        src = srcs[0];

        let checking = false;
        let child = src.firstChild;
        let next = null;
        // While we still have child elements to process...
        while (child && (changes === false)) {
            let checkThis = checking;
            next = child.nextSibling;
            // Is this a comment node?
            if (child.nodeType === Node.COMMENT_NODE) {
                if (child.nodeValue.includes(section + "-begin")) {
                    checking = true;
                    checkThis = false;
                } else if (child.nodeValue.includes(section + "-end")) {
                    checking = false;
                    checkThis = false;
                }
            }

            if (checkThis && (child.href !== undefined || child.src !== undefined)) {
                let elementChange = true;
                let parent = document.querySelector("head");
                if (parent) {
                    let headChild = parent.firstChild;
                    let nextChild = null;
                    // While we still have child elements to process...
                    while (headChild) {
                        nextChild = headChild.nextSibling;
                        if (child.href !== undefined && headChild.href === child.href && headChild.rel !== 'preload') {
                            elementChange = false;
                        }
                        if (child.src !== undefined && headChild.src === child.src) {
                            elementChange = false;
                        }
                        // Move on to next child
                        headChild = nextChild;
                    }

                    if (elementChange === true)
                        changes = true;
                }
            }

            // Move on to next child
            child = next;
        }
    }

    if (changes === true) {
        removeHeadElementsBetweenComments(section);

        if (srcs.length === 1) {
            src = srcs[0];

            let inserting = false;
            let child = src.firstChild;
            let next = null;
            // While we still have child elements to process...
            while (child) {
                let insertThis = inserting;
                next = child.nextSibling;
                // Is this a comment node?
                if (child.nodeType === Node.COMMENT_NODE) {
                    if (child.nodeValue.includes(section + "-begin")) {
                        // It's the node that tells us to start removing:
                        // Turn on our flag and also remove this node
                        inserting = true;
                        insertThis = false;
                    } else if (child.nodeValue.includes(section + "-end")) {
                        // It's the node that tells us to stop removing:
                        // Turn off our flag, but do remove this node
                        inserting = false;
                        insertThis = false;
                    }
                } else {
                    if (insertThis) {
                        if (child.nodeName === "LINK") {
                            addPreload(child.href);
                        }

                        $JQry("head").contents().filter(function () {
                            return this.nodeType === 8;
                        }).each(function (i, e) {
                            if ($JQry.trim(e.nodeValue) === section + "-end") {
                                $JQry(child).insertBefore(e);
                            }
                        });
                    }
                }
                // Move on to next child
                child = next;
            }
        }
    }
}

// Preload llow the use use of browser inner cache

function addPreload(linkRef) {

    let parent = document.querySelector("head");
    if (parent) {
        $JQry("head").find('[rel=preload][href="' + linkRef + '"]').remove()

        preloadTag = document.createElement('LINK');
        preloadTag.rel = "preload";
        preloadTag.href = linkRef;
        preloadTag.as = "style";

        parent.appendChild(preloadTag);

    }
}


function copyInnerHTML(srcContainer, dstContainer, className) {
    const classSelector = "." + className;

    const $srcContainer = $JQry(srcContainer);
    const srcs = $srcContainer.find(classSelector);
    if (srcs.length > 0) {
        const src = srcs[0];

        //
        const $dstContainer = $JQry(dstContainer);
        const dsts = $dstContainer.find(classSelector);
        if (dsts.length === 1) {
            const dst = dsts[0];

            copyNodes(src, dst);
        } else {
            // Should log that somewhere but
        }
    } else {
        // Should log that somewhere
    }
}


function copyNodes(src, dst) {
    // Remove existing non attribute children in destination
    const $dstChildren = $JQry(dst.childNodes);
    $dstChildren.each(function(index, element) {
        const $dstChild = $JQry(element);
        const dstChild = $dstChild.get(0);
        if (dstChild.nodeType !== 2) {
            $dstChild.remove();
        }
    });

    // Move src non attribute children to the destination
    while (src.hasChildNodes()) {
        const srcChild = src.firstChild;
        if (srcChild.nodeType !== 2) {
            dst.appendChild(srcChild);
        } else {
            src.removeChild(srcChild);
        }
    }
}


function observePortlets() {
    // Find the dyna portlets
    const $windows = $JQry(".partial-refresh-window");

    // Add listener for the dyna windows on the dyna-window element
    // and not async-window as this one will have its markup replaced
    $windows.each(function (index, element) {
        observePortlet(element);
    });
}


function observePortlet(refreshWindow) {
    const $window = $JQry(refreshWindow);
    if (!$window.data("ajax-observation")) {
        $window.on("click", "*", bilto);
        $window.data("ajax-observation", true);
    }
}


function removeHeadElementsBetweenComments(name) {
    //
    // remove head datas between meta-datas-begin and meta-datas-end
    //
    let parent = document.querySelector("head");
    if (parent) {
        // Uncomment if you want to see nodes before the change
        // showNodes("before", parent);
        let removing = false;
        let child = parent.firstChild;
        let next = null;
        // While we still have child elements to process...
        while (child) {
            // If we're already removing, remember that
            let removeThis = removing;
            // Before we remove anything, identify the next child to visit
            next = child.nextSibling;
            // Is this a comment node?
            if (child.nodeType === Node.COMMENT_NODE) {
                if (child.nodeValue.includes(name + "-begin")) {
                    // It's the node that tells us to start removing:
                    // Turn on our flag and also remove this node
                    removing = true;
                    removeThis = false;
                } else if (child.nodeValue.includes(name + "-end")) {
                    // It's the node that tells us to stop removing:
                    // Turn off our flag, but do remove this node
                    removing = false;
                    removeThis = false;
                }
            }
            if (removeThis) {
                // This is either stuff in-between the two comment nodes
                // or one of the comment nodes; either way, remove it
                parent.removeChild(child);
            }

            // Move on to next child
            child = next;
        }
    }
}

function removeFromHead(href) {

    let parent = document.querySelector("head");
    if (parent) {
        let child = parent.firstChild;
        let next = null;
        while (child) {
            next = child.nextSibling;
            if (child.href !== undefined && child.href.includes(href)) {
                parent.removeChild(child);
            }
            child = next;
        }
    }
}


function synchronizeMetadatas() {

    // create meta-datas section if doesn't exist
    var section = false;

    let parent = document.querySelector("head");
    if (parent) {

        let insertBefore = null;
        let child = parent.firstChild;
        let next = null;
        // While we still have child elements to process...
        while (child) {
            // Before we remove anything, identify the next child to visit
            next = child.nextSibling;
            // Is this a comment node?
            if (child.nodeType === Node.COMMENT_NODE) {
                if (child.nodeValue.includes("meta-datas-begin")) {
                    // It's the node that tells us to start removing:
                    // Turn on our flag and also remove this node
                    section = true;
                }

            }

            if (child.nodeName == "META") {
                insertBefore = child.nextSibling;
            }

            // Move on to next child
            child = next;
        }

        if (section == false) {
            // Create meta section

            var commentBegin = document.createComment("meta-datas-begin");
            var commentEnd = document.createComment("meta-datas-end");

            if (insertBefore != null) {
                parent.insertBefore(commentBegin, insertBefore);
                parent.insertBefore(commentEnd, insertBefore);
            } else {
                parent.insertBefore(commentEnd, parent.firstChild);
                parent.insertBefore(commentBegin, parent.firstChild);
            }
        }

    }


    removeHeadElementsBetweenComments("meta-datas");

    //
    // insert new elements
    //

    $JQry("dyna-window#header-metadata dyna-window-content:first-child").each(function (index, element) {
        var $element = $JQry(element);
        $element.children().each(function (headIndex, headElement) {

            $JQry("head").contents().filter(function () {
                return this.nodeType == 8;
            }).each(function (i, e) {
                if ($JQry.trim(e.nodeValue) == "meta-datas-end") {
                    $JQry(headElement).insertBefore(e);
                }
            });
        });

    });


}


function footer() {
    // Non Ajax Response
    var options = new Object();

    // We have a get
    options.method = "get"

    // We don't block
    options.async = false;

    // In firefox, even winhout any treament on json response
    // If page is already in history and Ajax.request is called with the same url
    //    -> The JSON reponse appears in source code

    var url = new URL(window.location.href);
    url.searchParams.append('_ts', Date.now());

    directAjaxCall("footer", options, url.href, null);
}


// Compatibility for admin mode
function closeFancybox() {
    history.back();
}

