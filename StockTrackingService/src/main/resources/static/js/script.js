var app = angular.module("mainApp", []);
app.controller("mainCtrl", function ($scope, $http, connection) {
  $scope.stockData = [
    { symbol: "AAPL", value: 123123 },
    { symbol: "TSLA", value: 123123 },
    { symbol: "AIV", value: 123123 },
    { symbol: "ACB", value: 123123 },
    { symbol: "AMZ", value: 123123 },
    { symbol: "AMZ", value: 123123 },
    { symbol: "AMZ", value: 123123 },
    { symbol: "AMZ", value: 123123 },
  ];

  let getTicket = function () {
    $http
      .get("/stock-track/unsubscribe/BINANCE:BTCUSDT")
      .then(function (response) {
        let data = response.data;
        console.log(data);
      });
  };

  let connectWebSkt = function () {
    getTicket();
    connection.connect();
  };

  let init = function () {
    connectWebSkt();
  };

  init();
});

app.service("connection", function () {
  var stompClient = null;

  function connect() {
    var socket = new SockJS("/gs-guide-websocket");
    stompClient = Stomp.over(socket);
    subscribe();
  }

  function disconnect() {
    if (stompClient !== null) {
      stompClient.disconnect();
    }
    console.log("Disconnected");
  }

  function subscribe() {
    stompClient.connect({}, function (frame) {
      console.log("Connected: " + frame);
      stompClient.subscribe("/topic/greetings", function (greeting) {
        console.log(greeting);
        console.log(JSON.parse(greeting.body).content);
      });
    }, function(err){
        console.log("Error: ", err);
    });
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
