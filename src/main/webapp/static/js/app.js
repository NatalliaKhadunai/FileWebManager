(function () {
    'use strict';
    var app = angular.module('fileApp', []);

    app.controller('indexCtrl', indexController);

    function indexController($http, $window) {
        let $ctrl = this;
        $ctrl.files = [];
        $ctrl.currentPath = '';
        $ctrl.sortField = 'directory';
        $ctrl.sortOrder = true;

        $ctrl.loadRootFiles = function () {
            $http.get('/files').then(function (response) {
                $ctrl.files = response.data;
            });
        };
        $ctrl.updateFiles = function (chosenFile) {
            if (chosenFile.directory) {
                $http({
                    url: '/files',
                    method: 'GET',
                    params: {path: $ctrl.currentPath + chosenFile.fileName}
                }).then(function (response) {
                    $ctrl.files = response.data;
                    $ctrl.currentPath = $ctrl.currentPath + chosenFile.fileName + '\\';
                    console.log($ctrl.currentPath);
                });
            }
            else {
                var url = '/downloadFile';
                var param = 'path=' + $ctrl.currentPath + chosenFile.fileName;
                $window.open(encodeURI(url + '?' + param));
            }
        };
        $ctrl.sort = function (sortField) {
            if ($ctrl.sortField == sortField) {
                $ctrl.sortOrder? $ctrl.sortOrder = false : $ctrl.sortOrder = true;
            }
            else {
                $ctrl.sortField = sortField;
                $ctrl.sortOrder = true;
            }
        };
        $ctrl.loadRootFiles();
    }
})();