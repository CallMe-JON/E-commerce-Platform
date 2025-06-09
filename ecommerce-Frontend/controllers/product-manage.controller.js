app.controller('ProductManageController', function($scope, ProductService) {
    $scope.products = [];
    $scope.newProduct = {};

    // Load all products for management
    function loadProducts() {
        ProductService.getAllProducts().then(function(response) {
            $scope.products = response.data.map(function(product){
                return {
                id: product.id,
                name: product.name || '',
                description: product.description || '',
                imageUrl: product.imageUrl || '',
                price: product.price != null ? product.price : 0,
                stock: product.stock != null ? product.stock : 0
            };   
            });
        });
    }

    // CREATE
    $scope.addToProduct = function(product) {
        if (!product || !product.name) {
            alert("Product name is required.");
            return;
        }
        product.description = product.description || '';
        product.imageUrl = product.imageUrl || '';
        product.price = product.price || 0;
        product.stock = product.stock || 0;

        ProductService.create(product)
            .then(function() {
                // alert("Product added successfully!");
                $scope.newProduct = {};
                loadProducts();
            })
            .catch(function(error) {
                alert("Failed to add product: " + (error.data && error.data.message ? error.data.message : "Unknown error"));
                console.error(error);
            });
    };

    // UPDATE
    $scope.editProduct = function(product) {
        product.editing = true;
        product._backup = angular.copy(product);
    };

    $scope.cancelEdit = function(product) {
        angular.extend(product, product._backup);
        product.editing = false;
    };

    $scope.updateProduct = function(product) {
        ProductService.update(product)
            .then(function() {
                alert("Product updated!");
                product.editing = false;
                loadProducts();
            })
            .catch(function(error) {
                alert("Failed to update product.");
                console.error(error);
            });
    };

    // DELETE
    $scope.deleteProduct = function(product) {
        if (confirm('Delete this product?')) {
            ProductService.delete(product.id)
                .then(function() {
                    alert("Product deleted!");
                    loadProducts();
                })
                .catch(function(error) {
                    alert("Failed to delete product.");
                    console.error(error);
                });
        }
    };

    loadProducts();
});