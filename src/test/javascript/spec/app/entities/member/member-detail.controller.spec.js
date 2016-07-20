'use strict';

describe('Controller Tests', function() {

    describe('Member Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockMember, MockParty;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockMember = jasmine.createSpy('MockMember');
            MockParty = jasmine.createSpy('MockParty');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Member': MockMember,
                'Party': MockParty
            };
            createController = function() {
                $injector.get('$controller')("MemberDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'calypsoApp:memberUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
