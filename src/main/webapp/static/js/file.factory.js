(function () {
    'use strict';
    angular.module('fileApp')
        .factory('fileFactory', fileFactory);

    function fileFactory($http) {
        var getFiles = function() {
            return $http.get('/files');
        };

        return {
            getFiles: getFiles
        };
    }
})();