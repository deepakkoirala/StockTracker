app.service("connection", function ($http, $q) {
  var stompClient = null;

  settings = {
    darkMode: undefined,
  };

  var baseUrl = "/stock-track";

  function connect(callback) {
    var socket = new SockJS("/ws");
    stompClient = Stomp.over(socket);
    stompClient.debug = null;
    // subscribeStock(callback);
    console.log("Connecting...");
    stompClient.connect(
      {},
      function (frame) {
        console.log("Connected: " + frame);
        // wsGetAllSymbols();
        callback("Connected");
      },
      function (err) {
        console.log("Error: ", err);
        callback(null, "Error");
      }
    );
  }

  function disconnect() {
    if (stompClient !== null) {
      stompClient.disconnect();
    }
    console.log("Disconnected");
  }

  function subscribeStock(callback) {
    stompClient.subscribe("/topic/updateService", function (data) {
      // console.log(JSON.parse(data["body"]));
      callback(JSON.parse(data["body"]));
    });
  }

  function subscribeSettings(callback) {
    stompClient.subscribe("/topic/getSettings", function (data) {
      // console.log(JSON.parse(data["body"]));
      callback(JSON.parse(data["body"]));
    });
  }

  function wsGetAllSymbols() {
    stompClient.send(
      "/topic/getAllSubscribedStocks",
      {},
      JSON.stringify({ name: "test" })
    );
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

  function resetStockSubscription() {
    return $http.get(baseUrl + "/resetStockSubscription");
  }

  function setSettings() {
    return $http.post(baseUrl + "/setSettings", settings);
  }

  function getSettings() {
    return $http.get(baseUrl + "/getSettings");
  }

  function toggleDarkMode() {
    // console.log(settings);
    if(settings.darkMode == undefined) { //if darkmode is not set, flip by current time.
      settings.darkMode = !isDark();
    }
    else
      settings.darkMode = !settings.darkMode;
    return $q(function (resolve, reject) {
      setSettings().then(
        function (r) {
          resolve(r);
        },
        function (e) {
          settings.darkMode = !settings.darkMode;
          reject(e);
        }
      );
    });
  }

  function getDarkMode() {
    getSettings().then(function (r) {
      // console.log(r);
      addRemoveDarkMode(r.data);
    });
  }

  function addRemoveDarkMode(r) {
    if (r && r.darkMode != undefined) {
      clearDarkModeTimer();
      if (r.darkMode == true) {
        addDarkMode();
        settings.darkMode = true;
      } else {
        settings.darkMode = false;
        removeDarkMode();
      }
    }
  }

  function resetDarkMode() {
    var tmp = settings.darkMode;
    delete settings.darkMode;
    // console.log(settings);
    return $q(function (resolve, reject) {
      setSettings().then(
        function (r) {
          resolve(r);
        },
        function (e) {
          settings.darkMode = tmp;
          reject(e);
        }
      );
    });
  }

  return {
    connect: connect,
    disconnect: disconnect,
    subscribeStock: subscribeStock,
    subscribeSettings: subscribeSettings,
    wsGetAllSymbols: wsGetAllSymbols,
    getAllSymbols: getAllSymbols,
    unSubscribeAll: unSubscribeAll,
    subscribeSymbol: subscribeSymbol,
    unSubscribeSymbol: unSubscribeSymbol,
    resetStockSubscription: resetStockSubscription,
    getSettings: getSettings,
    setSettings: setSettings,
    toggleDarkMode: toggleDarkMode,
    getDarkMode: getDarkMode,
    addRemoveDarkMode: addRemoveDarkMode,
    resetDarkMode: resetDarkMode,
  };
});
