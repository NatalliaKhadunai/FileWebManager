(function () {
    'use strict';
    angular.module('fileApp')
        .controller('usersCtrl', function ($http, usersSrv) {
            let $ctrl = this;
            $ctrl.addAdminPermissions = function (user) {
                usersSrv.addAdminPermissions(user);
            };
            $ctrl.revokeAdminPermissions = function (user) {
                usersSrv.revokeAdminPermissions(user);
            };
            $ctrl.deleteUser = function (user) {
                usersSrv.deleteUser(user);
            };
        });
})();