var app = angular.module('mainApp', []);
app.controller('mainCtrl', function($scope, $http) {
 
    $scope.stockData = [
        {symbol:"AAPL", value: 123123},
        {symbol:"TSLA", value: 123123},
        {symbol:"AIV", value: 123123},
        {symbol:"ACB", value: 123123},
        {symbol:"AMZ", value: 123123},
        {symbol:"AMZ", value: 123123},
        {symbol:"AMZ", value: 123123},
        {symbol:"AMZ", value: 123123}
    ]

    $http.get("/stock-track/subscribe/BINANCE:BTCUSDT")
    .then(function(response) {
        let data = response.data;
        console.log(data);
    });


});