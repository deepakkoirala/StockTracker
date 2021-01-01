var app = angular.module("mainApp", ["ngAnimate"]);

app.service("utils", function () {
  var isDark = function () {
    var hour = new Date().getHours();
    // hour = 20
    return hour >= 20 || hour <= 7 ? "dark" : "";
  };

  return {
    isDark: isDark,
  };
});

app.controller("mainCtrl", function ($scope, $http, connection, utils) {
  $scope.stockData = [
    // {symbol: "aapl"}
  ];

  $scope.getDarkClass = function () {
    return utils.isDark();
  };

  var setDataScope = function (data) {
    $scope.stockData = data;
    $scope.$apply();
  };

  var currSecTime = 0;
  var device = bowser.parse(window.navigator.userAgent).platform.type;
  var setData = function (data) {
    if (device == "mobile") {
      if (currSecTime != new Date().getSeconds()) {
        setDataScope(data);
        currSecTime = new Date().getSeconds();
      }
    } else {
      setDataScope(data);
    }
  };

  var getTicket = function () {
    $http
      .get("/stock-track/unsubscribe/BINANCE:BTCUSDT")
      .then(function (response) {
        var data = response.data;
        console.log(data);
      });
  };
  var connectWebSkt = function () {
    connection.connect(function (data, err) {
      if (err) {
        setTimeout(function () {
          console.log("Error, reconnecting in 5 Seconds...");
          connectWebSkt();
        }, 5000);
      } else {
        setData(data);
      }
    });
  };

  var init = function () {
    connectWebSkt();
  };

  init();
});
