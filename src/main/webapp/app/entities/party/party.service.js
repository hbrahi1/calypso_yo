(function() {
    'use strict';
    angular
        .module('calypsoApp')
        .factory('Party', Party);

    Party.$inject = ['$resource'];

    function Party ($resource) {
        var resourceUrl =  'api/parties/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
