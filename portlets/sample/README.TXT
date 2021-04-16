RELATIVE TO REACT APPLICATION
----------------------------------

Note than only the generated static files are present in git sources (/src/main/webapp/js/react2/static/js)
      -  main.* which corresponds to index.js
      -  2.* which corresponds to react modules
      -  run-time.* which is the start script

This files are referenced into the portlet
  - in /src/main/webapp/WEB-INF/jboss-portlet.xml (main-* and 2-*)
  - content of the runtime script is recopied in /webapp/WEB-INF/jsp/react.jsp 
  
Original source of the react portlet are located in /src/main/react/src

After modifying the sources, to build React App :

1/ go to /src/main/react
2/ npm install
3/ npm run build
4/ copy 2 the modified static files in /webapp/js/react2/static (main-* and 2-*)
5/ update the references (jboss-portlet.xml and react.jsp)