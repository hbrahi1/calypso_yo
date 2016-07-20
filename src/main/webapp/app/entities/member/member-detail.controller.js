(function() {
    'use strict';

    angular
        .module('calypsoApp')
        .controller('MemberDetailController', MemberDetailController);

    MemberDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Member', 'Party'];

    function MemberDetailController($scope, $rootScope, $stateParams, entity, Member, Party) {
        var vm = this;

        vm.member = entity;

        var unsubscribe = $rootScope.$on('calypsoApp:memberUpdate', function(event, result) {
            vm.member = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
