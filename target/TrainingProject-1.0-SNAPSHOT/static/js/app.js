(function () {
    'use strict';
    var app = angular.module('fileApp', []);

    app.controller('indexCtrl', indexController);

    function indexController($http) {
        let $ctrl = this;
        $ctrl.files = [];
        $ctrl.loadRootFiles = function() {
            $http.get('/rootFiles').then(function(response) {
                $ctrl.files = response.data;
            });
        };
        $ctrl.updateFiles = function (chosenFile) {
            $http({
                url: '/chosenFile',
                method: 'GET',
                params: {path : chosenFile}
            }).then(function(response) {
                $ctrl.files = response.data;
            });
        }
        $ctrl.loadRootFiles();
    }
})();