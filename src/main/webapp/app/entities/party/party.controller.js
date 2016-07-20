(function() {
    'use strict';

    angular
        .module('calypsoApp')
        .controller('PartyController', PartyController);

    PartyController.$inject = ['$scope', '$state', 'Party'];

    function PartyController ($scope, $state, Party) {
        var vm = this;
        
        vm.parties = [];

        loadAll();

        function loadAll() {
            Party.query(function(result) {
                vm.parties = result;
            });
        }
    }
})();
