(function () {
    'use strict';
    angular.module('fileApp')
        .factory('usersSrv', usersSrv);

    function usersSrv($http) {
        //API
        return {
            getLoggedUserRoles,
            getUsers,
            addAdminPermissions,
            revokeAdminPermissions,
            deleteUser
        };

        //IMPL
        function getLoggedUserRoles() {
            return $http.get('/training/role').then(function (response) {
                return response.data;
            }, function (response) {
                alert(response.status);
            });
        }

        function getUsers() {
            return $http.get('/training/admin/users').then(function (response) {
                return response.data;
            }, function (response) {
                alert(response.status);
            });
        }

         function addAdminPermissions(user) {
            $http.post('/training/admin/addAdminPermissions',user)
                .then(function (response) {
                    user.roles.push('ADMIN');
                }, function (response) {
                    alert(response.status);
                });
        }

        function revokeAdminPermissions(user) {
            $http.post('/training/admin/revokeAdminPermissions',user)
                .then(function (response) {
                    var index = user.roles.indexOf('ADMIN');
                    if (index > -1) {
                        user.roles.splice(index, 1);
                    }
                }, function (response) {
                    alert(response.status);
                });
        }

        function deleteUser(user) {
            $http({
                url: '/training/admin/deleteUser',
                method: 'DELETE',
                params: {username: user.username}
            }).then(function () {
                var index = $ctrl.users.indexOf(user);
                if (index > -1) {
                    $ctrl.users.splice(index, 1);
                }
            }, function (response) {
                alert(response.status);
            });
        }
    }
})();