(function() {
    'use strict';

    angular
        .module('calypsoApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('party', {
            parent: 'entity',
            url: '/party',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'calypsoApp.party.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/party/parties.html',
                    controller: 'PartyController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('party');
                    $translatePartialLoader.addPart('partyStatus');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('party-detail', {
            parent: 'entity',
            url: '/party/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'calypsoApp.party.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/party/party-detail.html',
                    controller: 'PartyDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('party');
                    $translatePartialLoader.addPart('partyStatus');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Party', function($stateParams, Party) {
                    return Party.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('party.new', {
            parent: 'party',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/party/party-dialog.html',
                    controller: 'PartyDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                partyId: null,
                                partyName: null,
                                partyStatus: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('party', null, { reload: true });
                }, function() {
                    $state.go('party');
                });
            }]
        })
        .state('party.edit', {
            parent: 'party',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/party/party-dialog.html',
                    controller: 'PartyDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Party', function(Party) {
                            return Party.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('party', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('party.delete', {
            parent: 'party',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/party/party-delete-dialog.html',
                    controller: 'PartyDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Party', function(Party) {
                            return Party.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('party', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
