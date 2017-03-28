(function () {
    'use strict';
    angular.module('fileApp')
        .component('fileSystem', {
            templateUrl: '/static/file-system/file-system.template.html',
            controller: 'fileSystemCtrl'
        });
})();