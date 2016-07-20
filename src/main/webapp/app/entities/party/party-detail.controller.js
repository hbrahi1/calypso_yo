(function() {
    'use strict';

    angular
        .module('calypsoApp')
        .controller('PartyDetailController', PartyDetailController);

    PartyDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Party', 'Member'];

    function PartyDetailController($scope, $rootScope, $stateParams, entity, Party, Member) {
        var vm = this;

        vm.party = entity;

        var unsubscribe = $rootScope.$on('calypsoApp:partyUpdate', function(event, result) {
            vm.party = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
