app.controller('ProductDetailController', function($scope, $routeParams, ProductService, CartService) {
    $scope.product = {};
    $scope.quantity = 1;
    ProductService.getProductById($routeParams.id)
        .then(function(response) { $scope.product = response.data; })
        .catch(function(error) { console.error("Error fetching product details:", error); });
    $scope.addToCart = function(product) {
        CartService.addItem(product, $scope.quantity)
            .then(function() { alert(product.name + " added to cart!"); })
            .catch(function(error) { alert("Failed to add to cart: " + (error.data || "Unknown error")); console.error(error); });
    };
});