'use strict';

/* App Module */

var cotaApp = angular.module('cotaApp', [
  'ngRoute'
]);

cotaApp.config(['$routeProvider',
  function($routeProvider) {
    $routeProvider.
      when('/', {
        templateUrl: 'partials/home.html',
        controller: 'HomeCtrl'
      }).
      when('/average', {
        templateUrl: 'partials/average.html',
        controller: 'AverageCtrl'
      }).
      when('/partido', {
        templateUrl: 'partials/partido.html',
        controller: 'PartidoCtrl'
      }).
      when('/partido/:partido', {
        templateUrl: 'partials/partido.html',
        controller: 'PartidoCtrl'
      }).
      otherwise({
        redirectTo: '/'
      });
  }
]);
