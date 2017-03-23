(function () {
    'use strict';
    angular.module('fileApp')
        .controller('fileSystemCtrl', function indexController($http, $window) {
            let $ctrl = this;
            $ctrl.files = [];
            $ctrl.currentPathElements = [];
            $ctrl.sortField = 'fileName';
            $ctrl.search = {};

            $ctrl.loadRootFiles = function () {
                $http.get('/training/files').then(function (response) {
                    $ctrl.files = response.data;
                });
            };
            $ctrl.updateFiles = function (chosenFile) {
                if (chosenFile.directory) {
                    $http({
                        url: '/training/files',
                        method: 'GET',
                        params: {path: $ctrl.currentPathElements.join('\\') + '\\' + chosenFile.fileName}
                    }).then(function (response) {
                        $ctrl.files = response.data;
                        $ctrl.currentPathElements.push(chosenFile.fileName);
                    });
                }
                else {
                    var url = '/training/downloadFile';
                    var param = 'path=' + $ctrl.currentPathElements.join('\\') + '\\' + chosenFile.fileName;
                    $window.open(encodeURI(url + '?' + param));
                }
            };
            $ctrl.deleteFile = function (chosenFile) {
                $http({
                    url: '/training/files/delete',
                    method: 'DELETE',
                    params: {path: $ctrl.currentPathElements.join('\\') + '\\' + chosenFile.fileName}
                }).then(function () {
                    var index = $ctrl.files.indexOf(chosenFile);
                    if (index > -1) {
                        $ctrl.files.splice(index, 1);
                    }
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
                        params: {path: $ctrl.currentPathElements.join('\\')}
                    }).then(function (response) {
                        $ctrl.files = response.data;
                    });
                }
                else if ($ctrl.currentPathElements.length == 1) {
                    $ctrl.currentPathElements.pop();
                    $ctrl.loadRootFiles();
                }
            };
            $ctrl.createDirectory = function () {
                $http.post('/training/files/addDirectory',
                    $ctrl.currentPathElements.join('\\') + '\\' + $ctrl.newDirectoryName)
                    .then(function (response) {
                        $ctrl.files.push(response.data);
                    });
            };
            $ctrl.formPath = function () {
                return $ctrl.currentPathElements.join('\\');
            };
            $ctrl.loadRootFiles();
        });
})();