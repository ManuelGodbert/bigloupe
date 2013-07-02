<%@page import="java.io.PrintWriter"%>
<%@ page isErrorPage="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
Error ${pageContext.errorData.statusCode}, URI : ${pageContext.errorData.requestURI}, Message : ${pageContext.errorData.throwable.message}

