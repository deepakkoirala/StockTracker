app.controller("adminCtrl", function ($scope, $http, connection, utils) {
  $scope.stockData = [
    // {symbol: "aapl"}
  ];

  $scope.inputSymbol = "";
  $scope.inputSymbolObj = {};

  var cleanInputSym = function () {
    $scope.inputSymbol = "";
    $scope.$broadcast('angucomplete-alt:clearInput');
  };

  $scope.inputChanged = function(e){
    $scope.inputSymbol = e;
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
    if(!symbol)
      symbol = $scope.inputSymbol;
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

  $scope.resetStockSubscription = function () {
    connection.resetStockSubscription().then(function (r) {
      showAllSymbols();
      connection.resetDarkMode().then(function (e) {
        console.log("Application Resettted...");
        // startTimeBasedDarkMode();
      });
    });
  };

  $scope.toggleDarkMode = function () {
    connection.toggleDarkMode().then(function (r) {
      // console.log(r);
      checkDarkMode();
    });
  };

  checkDarkMode = function () {
    connection.getDarkMode();
  };

  var init = function () {
    checkDarkMode();
    showAllSymbols();
  };

  init();
});
