import React from "react";
import ReactDOM from "react-dom";
import Game from "./components/Game";


var reactContainerFactory = document.getElementById("react-container-factory");
var reactContainerId=reactContainerFactory.getAttribute("current-container");


ReactDOM.render(<Game />, document.getElementById(reactContainerId));

