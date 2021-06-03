<%--
  Created by IntelliJ IDEA.
  User: daria
  Date: 6/3/21
  Time: 5:53 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>Hello Spring MVC</title>
</head>

<body>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<form:form modelAttribute="meal">
  <form:input path="description"/>
  <input type="submit"/>
</form:form>
</body>
</html>