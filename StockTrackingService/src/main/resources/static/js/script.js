var app = angular.module("mainApp", []);
app.controller("mainCtrl", function ($scope, $http, connection) {
  $scope.stockData = [
    { symbol: "BITCOIN", altSymbol: "BINANCE:BTCUSDT", value: 0 },
    { symbol: "AAPL", value: 0 },
    { symbol: "TSLA", value: 123123 },
    { symbol: "AIV", value: 123123 },
    { symbol: "AMZN", value: 123123 },
    { symbol: "AAL", value: 123123 },
    { symbol: "IC MARKET", altSymbol: "IC MARKETS:1", value: 123123 },
    { symbol: "PFE", value: 123123 },
    { symbol: "AMZ", value: 123123 },
    { symbol: "AMZ", value: 123123 },
    { symbol: "AMZ", value: 123123 },
    { symbol: "AMZ", value: 123123 }
  ];

  let getTicket = function () {
    $http
      .get("/stock-track/unsubscribe/BINANCE:BTCUSDT")
      .then(function (response) {
        let data = response.data;
        console.log(data);
      });
  };

  let findIndex = function(arr, symbol){
    return arr.findIndex(d=>d.symbol == symbol || d.altSymbol == symbol)
  }

  let connectWebSkt = function () {
//    getTicket();
    connection.connect(function(data){
        // console.log(data);
        let ind = findIndex($scope.stockData, data.symbol);
        $scope.stockData[ind].value = data.price;
        $scope.$apply();
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
    stompClient.connect({}, function (frame) {
      console.log("Connected: " + frame);
      stompClient.subscribe("/topic/updateService", function (data) {
        // console.log(JSON.parse(data["body"]));
        callback(JSON.parse(data["body"]));
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
