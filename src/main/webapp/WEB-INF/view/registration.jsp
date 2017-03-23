<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<html>
<head>
    <title>Registration</title>
</head>
<body>
<form:form method = "POST" modelAttribute="user" action = "/training/registration">
    <table>
        <tr>
            <td><form:label path = "username">Username</form:label></td>
            <td><form:input path = "username"/></td>
        </tr>
        <tr>
            <td><form:label path = "password">Password</form:label></td>
            <td><form:input path = "password"/></td>
        </tr>
        <tr>
            <td colspan = "2">
                <input type = "submit" value = "Submit"/>
            </td>
        </tr>
    </table>
</form:form>
</body>
</html>
