'use strict';

angular.module('myApp.view1', [])

    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/view1', {
            templateUrl: 'view1/view1.html',
            // controller: 'View1Ctrl'
        });
    }])

    // .controller('View1Ctrl', ['$scope', '$firebaseObject', function ($scope, $firebase) {
    //     var name = firebase.database().ref('test_case/').child("name");
    //     var t_case = firebase.database().ref('test_case/name');
    //
    //     console.log(name);
    //     console.log(t_case);
    //     var ref = firebase.database().ref().child("test_case");
    //
    //     console.log(ref);
    //     console.log(123);
    //     download the data into a local object
        // var syncObject = $firebaseObject(ref);
        // synchronize the object with a three-way data binding
        // click on `index.html` above to see it used in the DOM!
        // syncObject.$bindTo($scope, "test_case");
    // }]);