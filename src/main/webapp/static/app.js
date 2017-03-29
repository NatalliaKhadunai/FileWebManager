(function () {
    'use strict';
    angular.module('fileApp', []);
})();
(function () {
    'use strict';
    angular.module('fileApp',['ui.router']).config(function ($stateProvider, $urlRouterProvider) {
        $urlRouterProvider.otherwise('/');
        $stateProvider
            .state('file-system', {
                url: '/',
                template: '<file-system-component user-roles="$resolve.userRoles"></file-system-component>',
                resolve: {
                    userRoles: function (usersSrv) {
                        return usersSrv.getLoggedUserRoles();
                    }
                }
            })
            .state('users', {
                url: '/users',
                template: '<users-component users="$resolve.users"></users-component>',
                resolve: {
                    users: function (usersSrv) {
                        return usersSrv.getUsers();
                    }
                }
            });
    });
})();