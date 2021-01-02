app.controller("adminCtrl", function ($scope, $http, connection, utils) {
  $scope.stockData = [
    // {symbol: "aapl"}
  ];

  $scope.inputSymbol = '';

  var cleanInputSym = function(){
    $scope.inputSymbol = '';
  }

  var setDataScope = function (data) {
    $scope.stockData = data;
  };

  var showAllSymbols = function () {
    connection.getAllSymbols().then(function (r) {
      // console.log(r.data);
      setDataScope(r.data);
    });
  };

  $scope.unSubscribeAll = function () {
    connection.unSubscribeAll().then(function (r) {
      // console.log(r);
      showAllSymbols();
    });
  };

  $scope.subscribeSymbol = function (symbol) {
    connection.subscribeSymbol(symbol).then(function (r) {
      // console.log(r);
      showAllSymbols();
      cleanInputSym();
    });
  };

  $scope.unSubscribeSymbol = function (symbol) {
    connection.unSubscribeSymbol(symbol).then(function (r) {
      // console.log(r);
      showAllSymbols();
    });
  };

  var init = function () {
    showAllSymbols();
  };

  init();
});
