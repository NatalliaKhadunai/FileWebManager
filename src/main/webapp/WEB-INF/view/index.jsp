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
    <script src="static/js/app.js"></script>
    <script src="static/js/file-system.controller.js"></script>
</head>
<body>
<div class="container" ng-controller="fileSystemCtrl as ctrl">
    <a href="#" ng-click="ctrl.directoryBack()">Back</a>
    <a href="/training/logout">Logout</a>
    <p>Enter filename for search:</p>
    <input type="text" ng-model="ctrl.search.fileName">
    <table class="table">
        <thead>
        <tr>
            <th ng-click="ctrl.sort('fileName')">Filename</th>
            <th ng-click="ctrl.sort('contentType')">Type</th>
            <th ng-click="ctrl.sort('creationDate')">Creation date</th>
            <th ng-click="ctrl.sort('modificationDate')">Modification date</th>
            <th ng-click="ctrl.sort('fileSize')">Size</th>
            <sec:authorize access="hasAuthority('ROLE_ADMIN')">
                <th>Actions</th>
            </sec:authorize>
        </tr>
        </thead>
        <tbody>
        <tr ng-repeat="file in ctrl.files
            | orderBy : ['-directory', ctrl.sortField]
            | filter : ctrl.search">
            <td><a ng-click="ctrl.updateFiles(file)">{{file.fileName}}</a></td>
            <td>{{file.contentType}}</td>
            <td>{{file.creationDate | date}}</td>
            <td>{{file.modificationDate | date}}</td>
            <td>{{file.fileSize}} KB</td>
            <sec:authorize access="hasAuthority('ADMIN')">
                <td>
                    <button class="btn btn-danger" ng-click="ctrl.deleteFile(file)">Delete</button>
                </td>
            </sec:authorize>
        </tr>
        </tbody>
    </table>
    <sec:authorize access="hasAuthority('ADMIN')">
        <div class="panel-group">
            <div class="panel panel-default">
                <div class="panel-heading">Create directory</div>
                <div class="panel-body">
                    <p>Enter new directory name:</p>
                    <input type="text" ng-model="ctrl.newDirectoryName">
                    <button class="btn btn-success" ng-click="ctrl.createDirectory()">Create</button>
                </div>
            </div>
            <div class="panel panel-default">
                <div class="panel-heading">Upload file</div>
                <div class="panel-body">
                    <form action="/training/files/addFile" method="post" enctype="multipart/form-data">
                        <p>Choose file:</p>
                        <input type="file" name="file">
                        <input type="hidden" name="path" ng-value="ctrl.formPath()">
                        <input type="submit" class="btn">
                    </form>
                </div>
            </div>
        </div>
    </sec:authorize>
</div>
</body>
</html>