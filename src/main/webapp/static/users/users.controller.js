(function () {
    'use strict';
    angular.module('fileApp')
        .controller('usersCtrl', function ($http) {
            let $ctrl = this;
            $ctrl.addAdminPermissions = function (user) {
                $http.post('/training/admin/addAdminPermissions',user)
                    .then(function (response) {
                        user.roles.push('ADMIN');
                    });
            };
            $ctrl.revokeAdminPermissions = function (user) {
                $http.post('/training/admin/revokeAdminPermissions',user)
                    .then(function (response) {
                        var index = user.roles.indexOf('ADMIN');
                        if (index > -1) {
                            user.roles.splice(index, 1);
                        }
                    });
            };
            $ctrl.deleteUser = function (user) {
                $http({
                    url: '/training/admin/deleteUser',
                    method: 'DELETE',
                    params: {username: user.username}
                }).then(function () {
                    var index = $ctrl.users.indexOf(user);
                    if (index > -1) {
                        $ctrl.users.splice(index, 1);
                    }
                });
            };
        });
})();