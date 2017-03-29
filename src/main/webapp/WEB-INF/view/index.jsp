<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<html lang="en" ng-app="fileApp">
<head>
    <meta charset="UTF-8">
    <title>Main</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.5.7/angular.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/angular-ui-router/0.4.2/angular-ui-router.min.js"></script>
    <script src="static/app.js"></script>
    <script src="static/users/users.service.js"></script>
    <script src="static/users/users.component.js"></script>
    <script src="static/users/users.controller.js"></script>
    <script src="static/file-system/file-system.component.js"></script>
    <script src="static/file-system/file-system.controller.js"></script>
</head>
<body>
<div class="container">
    <a href="/training/logout">Logout</a>
    <ul class="nav nav-tabs">
        <li ui-sref-active="active"><a ui-sref="file-system">Main</a></li>
        <sec:authorize access="hasAuthority('ADMIN')">
            <li ui-sref-active="active"><a ui-sref="users">Users</a></li>
        </sec:authorize>
    </ul>
    <ui-view></ui-view>
</div>
</body>
</html>