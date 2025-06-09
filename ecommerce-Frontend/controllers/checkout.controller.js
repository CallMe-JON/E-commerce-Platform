app.controller('CheckoutController', function($scope, CartService, OrderService, $location) {
    $scope.cartItems = CartService.getCartItems();
    $scope.totalAmount = CartService.getTotal();
    CartService.fetchCartFromServer().then(function(items) {
        $scope.cartItems = items;
        $scope.totalAmount = CartService.getTotal();
    });

    $scope.placeOrder = function() {
        if ($scope.cartItems.length === 0) {
            alert("Your cart is empty!");
            return;
        }
        OrderService.createOrder()
            .then(function(response) {
                alert("Order placed successfully! Order ID: " + response.data.id);
                CartService.clearCart();
                $location.path('/orders');
            })
            .catch(function(error) {
                alert("Failed to place order: " + (error.data || "Unknown error"));
                console.error("Error placing order:", error);
            });
    };
});