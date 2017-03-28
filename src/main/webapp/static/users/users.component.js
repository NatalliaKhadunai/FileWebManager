(function () {
    'use strict';
    angular.module('fileApp')
        .component('usersComponent', {
            templateUrl: '/static/users/users.template.html',
            controller: 'usersCtrl',
            bindings: {
                userRoles: '=',
                users: '='
            }
        });
})();