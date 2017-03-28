(function () {
    'use strict';
    angular.module('fileApp')
        .factory('usersSrv', usersSrv);

    function usersSrv($http) {
        //API
        return {
            getLoggedUserRoles,
            getUsers
        };

        //IMPL
        function getLoggedUserRoles() {
            return $http.get('/training/role').then(function (response) {
                return response.data;
            });
        }

        function getUsers() {
            return $http.get('/training/admin/users').then(function (response) {
                return response.data;
            });
        }
    }
})();