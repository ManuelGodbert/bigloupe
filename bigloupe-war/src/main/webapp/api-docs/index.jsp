<!DOCTYPE html>
<html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<head>
    <title>Bigloupe - Rest API - Chart UI</title>
    <link rel="Shortcut Icon" href="/resources/img/bigloupe-elephant-ico.png" />
    <link href='http://fonts.googleapis.com/css?family=Droid+Sans:400,700' rel='stylesheet' type='text/css'/>
    <link href='${request.contextPath}/resources/css/swagger/hightlight.default.css' media='screen' rel='stylesheet' type='text/css'/>
    <link href='${request.contextPath}/resources/css/swagger/screen.css' media='screen' rel='stylesheet' type='text/css'/>
    <script type="text/javascript"
		src="${request.contextPath}/resources/js/jquery/1.7.1/jquery.min.js"></script>
    <script src='${request.contextPath}/resources/js/swagger/jquery.slideto.min.js' type='text/javascript'></script>
    <script src='${request.contextPath}/resources/js/swagger/jquery.wiggle.min.js' type='text/javascript'></script>
    <script src='${request.contextPath}/resources/js/swagger/jquery.ba-bbq.min.js' type='text/javascript'></script>
    <script src='${request.contextPath}/resources/js/swagger/handlebars-1.0.rc.1.js' type='text/javascript'></script>
    <script src='${request.contextPath}/resources/js/swagger/underscore-min.js' type='text/javascript'></script>
    <script src='${request.contextPath}/resources/js/swagger/backbone-min.js' type='text/javascript'></script>
    <script src='${request.contextPath}/resources/js/swagger/swagger.js' type='text/javascript'></script>
    <script src='${request.contextPath}/resources/js/swagger/swagger-ui.js' type='text/javascript'></script>
    <script src='${request.contextPath}/resources/js/swagger/highlight.7.3.pack.js' type='text/javascript'></script>

    <style type="text/css">
        .swagger-ui-wrap {
            max-width: 960px;
            margin-left: auto;
            margin-right: auto;
        }

        .icon-btn {
            cursor: pointer;
        }

        #message-bar {
            min-height: 30px;
            text-align: center;
            padding-top: 10px;
        }

        .message-success {
            color: #89BF04;
        }

        .message-fail {
            color: #cc0000;
        }
    </style>

    <script type="text/javascript">
        $(function () {
            window.swaggerUi = new SwaggerUi({
                discoveryUrl:"${fn:replace(pageContext.request.requestURL, pageContext.request.requestURI, '')}${request.contextPath}/chart/api-docs.json",
                apiKey:"",
                dom_id:"swagger-ui-container",
                supportHeaderParams: true,
                supportedSubmitMethods: ['get', 'post', 'put'],
                headers: { "Content-Type": "application/json" },
                onComplete: function(swaggerApi, swaggerUi){
                	if(console) {
                        console.log("Loaded SwaggerUI")
                        console.log(swaggerApi);
                        console.log(swaggerUi);
                    }
                  $('pre code').each(function(i, e) {hljs.highlightBlock(e)});
                },
                onFailure: function(data) {
                	if(console) {
                        console.log("Unable to Load SwaggerUI");
                        console.log(data);
                    }
                },
                docExpansion: "none"
            });

            window.swaggerUi.load();
        });

    </script>
</head>

<body>
<div id='header'>
    <div class="swagger-ui-wrap">
        <a id="logo" href="${request.contextPath}">BigLoupe REST API</a>

        <form id='api_selector'>
            <div class='input'><input placeholder="api_key" id="input_apiKey" name="apiKey" type="text"/></div>
            <div class='input'><a id="explore" href="#">Explore</a></div>
        </form>
    </div>
</div>

<div id="message-bar" class="swagger-ui-wrap">
    &nbsp;
</div>

<div id="swagger-ui-container" class="swagger-ui-wrap">

</div>

</body>

</html>
