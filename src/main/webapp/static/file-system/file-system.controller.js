(function () {
    'use strict';
    angular.module('fileApp')
        .controller('fileSystemCtrl', function ($http, $window) {
            let $ctrl = this;
            $ctrl.files = [];
            $ctrl.currentPathElements = [];
            $ctrl.sortField = 'fileName';
            $ctrl.search = {};

            $ctrl.loadRootFiles = function () {
                $http.get('/training/files').then(function (response) {
                    $ctrl.files = response.data;
                }, function (response) {
                    alert("Status code : " + response.data.httpStatusCode + "\n" + "Message : " + response.data.developerMessage);
                });
            };
            $ctrl.updateFiles = function (chosenFile) {
                if (chosenFile.directory) {
                    $http({
                        url: '/training/files',
                        method: 'GET',
                        params: {path: $ctrl.formCurrentPath() + '\\' + chosenFile.fileName}
                    }).then(function (response) {
                        $ctrl.files = response.data;
                        $ctrl.currentPathElements.push(chosenFile.fileName);
                    }, function (response) {
                        alert("Status code : " + response.data.httpStatusCode + "\n" + "Message : " + response.data.developerMessage);
                    });
                }
                else {
                    var url = '/training/downloadFile';
                    var param = 'path=' + $ctrl.formCurrentPath() + '\\' + chosenFile.fileName;
                    $window.open(encodeURI(url + '?' + param));
                }
            };
            $ctrl.deleteFile = function (chosenFile) {
                $http({
                    url: '/training/admin/delete',
                    method: 'DELETE',
                    params: {path: $ctrl.formCurrentPath() + '\\' + chosenFile.fileName}
                }).then(function () {
                    var index = $ctrl.files.indexOf(chosenFile);
                    if (index > -1) {
                        $ctrl.files.splice(index, 1);
                    }
                }, function (response) {
                    alert("Status code : " + response.data.httpStatusCode + "\n" + "Message : " + response.data.developerMessage);
                });
            };
            $ctrl.sort = function (sortField) {
                if ($ctrl.sortField == sortField) {
                    if ($ctrl.sortField.indexOf('-') == 0) $ctrl.sortField = $ctrl.sortField.replace('-', '');
                    else $ctrl.sortField = '-' + $ctrl.sortField;
                }
                else {
                    $ctrl.sortField = sortField;
                }
            };
            $ctrl.directoryBack = function () {
                if ($ctrl.currentPathElements.length > 1) {
                    $ctrl.currentPathElements.pop();
                    $http({
                        url: '/training/files',
                        method: 'GET',
                        params: {path: $ctrl.formCurrentPath()}
                    }).then(function (response) {
                        $ctrl.files = response.data;
                    }, function (response) {
                        alert("Status code : " + response.data.httpStatusCode + "\n" + "Message : " + response.data.developerMessage);
                    });
                }
                else if ($ctrl.currentPathElements.length == 1) {
                    $ctrl.currentPathElements.pop();
                    $ctrl.loadRootFiles();
                }
            };
            $ctrl.createDirectory = function () {
                if ($ctrl.currentPathElements.length === 0)
                    alert('Directory shouldn\'t be created along with root directories');
                else {
                    $http.post('/training/admin/addDirectory',
                        $ctrl.formCurrentPath() + '\\' + $ctrl.newDirectoryName)
                        .then(function (response) {
                            $ctrl.files.push(response.data);
                        }, function (response) {
                            alert("Status code : " + response.data.httpStatusCode + "\n" + "Message : " + response.data.developerMessage);
                        });
                }
            };
            $ctrl.formCurrentPath = function () {
                return $ctrl.currentPathElements.join('\\');
            };
            $ctrl.isLoggedUserAdmin = function () {
                for (var i=0;i<$ctrl.userRoles.length;i++) {
                    if ($ctrl.userRoles[i].authority === 'ADMIN') return true;
                };
                return false;
            };
            $ctrl.loadRootFiles();
        });
})();