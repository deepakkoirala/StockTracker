app.service("connection", function ($http) {
  var stompClient = null;
  var baseUrl = "/stock-track";

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
        wsGetAllSymbols();
      },
      function (err) {
        console.log("Error: ", err);
        callback(null, "Error");
      }
    );
  }

  function wsGetAllSymbols() {
    stompClient.send("/topics/getAllSubscribedStocks", {}, JSON.stringify({ name: "test" }));
  }

  function getAllSymbols() {
    return $http.get(baseUrl + "/getAllSubscribedStocks");
  }

  function unSubscribeAll() {
    return $http.get(baseUrl + "/unsubscribeAll");
  }

  function subscribeSymbol(symbol) {
    return $http.get(baseUrl + "/subscribe/" + symbol);
  }

  function unSubscribeSymbol(symbol) {
    return $http.get(baseUrl + "/unsubscribe/" + symbol);
  }

  return {
    connect: connect,
    disconnect: disconnect,
    wsGetAllSymbols: wsGetAllSymbols,
    getAllSymbols: getAllSymbols,
    unSubscribeAll: unSubscribeAll,
    subscribeSymbol: subscribeSymbol,
    unSubscribeSymbol: unSubscribeSymbol,
  };
});
