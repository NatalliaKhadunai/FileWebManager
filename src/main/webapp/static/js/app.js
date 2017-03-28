(function () {
    'use strict';
    angular.module('fileApp', []);
})();
(function () {
    'use strict';
    angular.module('fileApp',['ui.router']).config(function ($stateProvider, $urlRouterProvider) {
        $urlRouterProvider.otherwise('/');
        $stateProvider
            .state('main', {
                url: '/',
                template: '<file-system></file-system>'
            })
            .state('users', {
                url: '/users',
                template: '<h1>USERS</h1>',
            });
    });
})();