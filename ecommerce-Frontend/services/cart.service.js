app.service('CartService', function($http, API_BASE_URL, AuthService) {
    var service = {};
    service.cartItems = [];


    service.loadCartFromLocalStorage = function() {
        var storedCart = localStorage.getItem('shoppingCart');
        if (storedCart) { service.cartItems = JSON.parse(storedCart); }
        else { service.cartItems = []; }
    };
    service.saveCartToLocalStorage = function() { localStorage.setItem('shoppingCart', JSON.stringify(service.cartItems)); };

    // Using backend API for cart for logged in users
    service.fetchCartFromServer = function() {
        if (AuthService.isLoggedIn()) {
            return $http.get(API_BASE_URL + '/cart', { headers: { 'Authorization': 'Bearer ' + AuthService.getToken() } })
                .then(function(response) {
                    service.cartItems = response.data;
                    return response.data;
                })
                .catch(function(error) {
                    console.error("Error fetching cart from server:", error);
                    service.loadCartFromLocalStorage();
                    return service.cartItems;
                });
        } else {
            service.loadCartFromLocalStorage();
            return Promise.resolve(service.cartItems);
        }
    };

    service.addItem = function(product, quantity) {
        if (AuthService.isLoggedIn()) {
            return $http.post(API_BASE_URL + '/cart/add?productId=' + product.id + '&quantity=' + quantity, {}, { headers: { 'Authorization': 'Bearer ' + AuthService.getToken() } })
                .then(function(response) {
                    return service.fetchCartFromServer(); // Refresh cart after adding
                });
        } else {
            var found = false;
            for (var i = 0; i < service.cartItems.length; i++) {
                if (service.cartItems[i].product.id === product.id) {
                    service.cartItems[i].quantity += quantity;
                    found = true;
                    break;
                }
            }
            if (!found) { service.cartItems.push({ product: product, quantity: quantity }); }
            service.saveCartToLocalStorage();
            return Promise.resolve(service.cartItems);
        }
    };

    service.removeItem = function(productId, cartItemId) {
        if (AuthService.isLoggedIn()) {
            return $http.delete(API_BASE_URL + '/cart/remove/' + cartItemId, { headers: { 'Authorization': 'Bearer ' + AuthService.getToken() } })
                .then(function(response) {
                    return service.fetchCartFromServer();
                });
        } else {
            service.cartItems = service.cartItems.filter(item => item.product.id !== productId);
            service.saveCartToLocalStorage();
            return Promise.resolve(service.cartItems);
        }
    };

    service.updateQuantity = function(productId, cartItemId, quantity) {
        if (AuthService.isLoggedIn()) {
            return $http.put(API_BASE_URL + '/cart/update/' + cartItemId + '?quantity=' + quantity, {}, { headers: { 'Authorization': 'Bearer ' + AuthService.getToken() } })
                .then(function(response) {
                    return service.fetchCartFromServer();
                });
        } else {
            for (var i = 0; i < service.cartItems.length; i++) {
                if (service.cartItems[i].product.id === productId) {
                    service.cartItems[i].quantity = quantity;
                    break;
                }
            }
            service.saveCartToLocalStorage();
            return Promise.resolve(service.cartItems);
        }
    };

    service.getCartItems = function() { return service.cartItems; };

    service.getTotal = function() {
        return service.cartItems.reduce((total, item) => total + (item.product.price * item.quantity), 0);
    };

    service.clearCart = function() {
        if (AuthService.isLoggedIn()) {
            return $http.delete(API_BASE_URL + '/cart/clear', { headers: { 'Authorization': 'Bearer ' + AuthService.getToken() } })
                .then(function() {
                    service.cartItems = [];
                    service.saveCartToLocalStorage(); // Also clear local storage
                });
        } else {
            service.cartItems = [];
            service.saveCartToLocalStorage();
            return Promise.resolve();
        }
    };

    // Initialize cart when service is loaded
    service.fetchCartFromServer(); // Try fetching from server first
    return service;
});