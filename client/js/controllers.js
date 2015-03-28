'use strict';

/* Controllers */

cotaApp.controller('HomeCtrl', ['$scope',
	function($scope) {

		$scope.data = [
			{partido: "PT", valor: 10000},
			{partido: "PSDB", valor: 6000},
			{partido: "PMDB", valor: 12000},
			{partido: "PSOL", valor: 2000},
			{partido: "PP", valor: 9000},
			{partido: "PV", valor: 1000},
		];
	}
]);
