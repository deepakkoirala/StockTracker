var app = angular.module("mainApp", ["ngAnimate"]);

var isDark = function () {
  var hour = new Date().getHours();
  // hour = 20
  return hour >= 20 || hour <= 7 ? "dark" : "";
};

var addDarkMode = function () {
  document.querySelector("body").classList.add("dark");
};

var removeDarkMode = function () {
  document.querySelector("body").classList.remove("dark");
};

app.service("utils", function () {
  return {
    isDark: isDark,
  };
});

angular.element(document).ready(function () {
  var checkDark = function () {
    if (isDark()) addDarkMode();
    else removeDarkMode();
  };

  checkDark();
  setInterval(checkDark, 60000);
});

app.controller("mainCtrl", function ($scope, $http, connection, utils) {
  $scope.stockData = [
    // {symbol: "aapl"}
  ];

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

  var connectWebSkt = function () {
    connection.connect(function (data, err) {
      if (err) {
        setTimeout(function () {
          console.log("Error, reconnecting in 5 Seconds...");
          connectWebSkt();
        }, 5000);
      } else {
        connection.subscribeStock(function (data) {
          setData(data);
        });

        connection.subscribeSettings(function (data) {
          console.log(data);
          connection.addRemoveDarkMode(data);
        });
      }
    });
  };

  var init = function () {
    connectWebSkt();
  };

  init();
});
