(function () {
    'use strict';
    angular.module('fileApp')
        .controller('indexCtrl', indexController);

    function indexController(fileFactory) {
            let $ctrl = this;
            $ctrl.files = [];
            fileFactory.getFiles().success(function(data) {
                $ctrl.files = data;
            });
    }
});