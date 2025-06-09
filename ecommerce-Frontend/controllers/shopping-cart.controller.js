app.controller('ShoppingCartController', function($scope, CartService ,OrderService) {
    $scope.cartItems = CartService.getCartItems();
    CartService.fetchCartFromServer().then(function(items) { $scope.cartItems = items; });

    $scope.removeItem = function(productId, cartItemId) {
        CartService.removeItem(productId, cartItemId)
            .then(function(items) { $scope.cartItems = items; })
            .catch(function(error) { alert("Failed to remove item: " + (error.data || "Unknown error")); console.error(error); });
    };

    $scope.updateQuantity = function(item, quantity) {
        if (quantity > 0) {
            CartService.updateQuantity(item.product.id, item.id, quantity) // item.id is cartItemId
                .then(function(items) { $scope.cartItems = items; })
                .catch(function(error) { alert("Failed to update quantity: " + (error.data || "Unknown error")); console.error(error); });
        } else {
            $scope.removeItem(item.product.id, item.id);
        }
    };

    $scope.placeOrder = function() {
        OrderService.createOrder().then(function(response) {
        // Redirect to orders or show success
        window.location.href = '#!/orders';
        }, function(error) {
        // Handle error (show message)
        alert('Order failed: ' + (error.data && error.data.message ? error.data.message : 'Unknown error'));
        });
    };

    $scope.getTotal = function() { return CartService.getTotal(); };
});