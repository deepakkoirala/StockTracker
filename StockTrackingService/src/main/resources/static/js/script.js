var app = angular.module("mainApp", ["ngAnimate"]);
app.controller("mainCtrl", function ($scope, $http, connection) {
  $scope.stockData = [];

  $scope.getDarkClass = function () {
    let hour = new Date().getHours();
    // hour = 20
    return hour >= 20 || hour <= 7 ? "dark" : "";
  };

  let setDataScope = function (data) {
    $scope.stockData = data;
    $scope.$apply();
  };

  let currSecTime = 0;
  let setData = function (data) {
    if (bowser.parse(window.navigator.userAgent).platform.type == "mobile") {
      if (currSecTime != new Date().getSeconds()) {
        setDataScope(data);
        currSecTime = new Date().getSeconds();
      }
    } else {
      setDataScope(data);
    }
  };

  let getTicket = function () {
    $http
      .get("/stock-track/unsubscribe/BINANCE:BTCUSDT")
      .then(function (response) {
        let data = response.data;
        console.log(data);
      });
  };
  let connectWebSkt = function () {
    connection.connect(function (data, err) {
      if (err) {
        setTimeout(() => {
          console.log("Error, reconnecting in 5 Seconds...");
          connectWebSkt();
        }, 5000);
      } else {
        setData(data);
      }
    });
  };

  let init = function () {
    connectWebSkt();
  };

  init();
});

app.service("connection", function () {
  var stompClient = null;

  function connect(callback) {
    var socket = new SockJS("/ws");
    stompClient = Stomp.over(socket);
    stompClient.debug = null;
    subscribe(callback);
  }

  function disconnect() {
    if (stompClient !== null) {
      stompClient.disconnect();
    }
    console.log("Disconnected");
  }

  function subscribe(callback) {
    console.log("Connecting...");
    stompClient.connect(
      {},
      function (frame) {
        console.log("Connected: " + frame);
        stompClient.subscribe("/topic/updateService", function (data) {
          // console.log(JSON.parse(data["body"]));
          callback(JSON.parse(data["body"]));
        });
      },
      function (err) {
        console.log("Error: ", err);
        callback(null, "Error");
      }
    );
  }

  function sendName() {
    stompClient.send("/app/hello", {}, JSON.stringify({ name: "test" }));
  }

  return {
    connect,
    disconnect,
    sendName,
  };
});
