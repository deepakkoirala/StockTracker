var app = angular.module("mainApp", ["ngAnimate", "angucomplete-alt"]);

app.filter("numberFormat", function () {
  return function (input, num) {
    // console.log(input, num);
    if (input) {
      if (!num) num = 4;
      return Number(input.toFixed(num)).toString();
    } else return input;
  };
});

var isDark = function () {
  var hour = new Date().getHours();
  // var secs = new Date().getSeconds(); //code for testing purpose
  // hour = 20;
  // secs = 20;
  // return ((secs <= 30) && (hour >= 20 || hour <= 7)) ? "dark" : "";
  return hour >= 20 || hour <= 7 ? "dark" : "";
};

var addDarkMode = function () {
  document.querySelector("body").classList.add("dark");
};

var removeDarkMode = function () {
  document.querySelector("body").classList.remove("dark");
};

var darkModeTimer;
var intervalRunning = false;

var stopDarkModeTimer = function () {
  if (intervalRunning) {
    clearInterval(darkModeTimer);
    intervalRunning = false;
  }
};

var checkDark = function () {
  if (isDark()) addDarkMode();
  else removeDarkMode();
};

var startDarkModeTimer = function () {
  if (!intervalRunning) {
    checkDark();
    darkModeTimer = setInterval(checkDark, 6 * 1000);
    intervalRunning = true;
  }
};

var startTimeBasedDarkMode = function () {
  startDarkModeTimer();
};

app.service("utils", function () {
  return {
    isDark: isDark,
  };
});

angular.element(document).ready(function () {
  // startTimeBasedDarkMode();
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
          // if (data.darkMode != undefined) {
          connection.addRemoveDarkMode(data);
          // } else startTimeBasedDarkMode();
        });
      }
    });
  };

  var init = function () {
    connectWebSkt();
  };

  init();
});
