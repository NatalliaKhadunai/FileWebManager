<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<html lang="en" ng-app="fileApp">
<head>
    <meta charset="UTF-8">
    <title>Main</title>
    <link rel="stylesheet" href="static/js-test/node_modules/bootstrap/dist/css/bootstrap.css">
    <script src="static/js-test/node_modules/jquery/dist/jquery.js"></script>
    <link rel="stylesheet" href="static/js-test/node_modules/bootstrap/dist/js/bootstrap.js">
    <script src="static/js-test/node_modules/angular/angular.js"></script>
    <script src="static/js-test/node_modules/angular-ui-router/release/angular-ui-router.js"></script>
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