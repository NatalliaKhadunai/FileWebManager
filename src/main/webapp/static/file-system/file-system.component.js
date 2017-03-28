(function () {
    'use strict';
    angular.module('fileApp')
        .component('fileSystemComponent', {
            templateUrl: '/static/file-system/file-system.template.html',
            controller: 'fileSystemCtrl',
            bindings: {
                userRoles: '='
            }
        });
})();