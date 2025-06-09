app.controller('ProductListController', function($scope, ProductService, CartService) {
    $scope.products = [];
    ProductService.getAllProducts()
        .then(function(response) { $scope.products = response.data; })
        .catch(function(error) { console.error("Error fetching products:", error); });


    $scope.addToCart = function(product) {
        CartService.addItem(product, 1)
            .then(function() { alert(product.name + " added to cart!"); })
            .catch(function(error) { alert("Failed to add to cart: " + (error.data || "Unknown error")); console.error(error); });
    };
    
});