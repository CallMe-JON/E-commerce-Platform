app.controller('OrderHistoryController', function($scope, OrderService) {
    $scope.orders = [];
    OrderService.getUserOrders()
        .then(function(response) { $scope.orders = response.data; })
        .catch(function(error) { console.error("Error fetching orders:", error); alert("Failed to fetch order history."); });
});