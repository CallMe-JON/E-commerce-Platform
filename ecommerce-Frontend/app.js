var app = angular.module('ecommerceApp', ['ngRoute']);

// Base URL for your Spring Boot API
app.constant('API_BASE_URL', 'http://localhost:8080/api');


app.config(function($httpProvider) {
    // Push the AuthInterceptor into the list of HTTP interceptors
    $httpProvider.interceptors.push('AuthInterceptor');
});

// Define the AuthInterceptor factory
app.factory('AuthInterceptor', function($q, $injector) { // Inject $injector to avoid circular dependency with AuthService
    return {
        // Intercept responses
        responseError: function(rejection) {
            // Check if the status is 401 (Unauthorized)
            if (rejection.status === 401) {
                console.warn("Unauthorized request detected. Token might be expired or invalid. Attempting logout and redirect to login.");
                // Use $injector to get AuthService and $location to avoid circular dependency
                var AuthService = $injector.get('AuthService');
                var $location = $injector.get('$location');

                AuthService.logout(); // Clear the local token
                $location.path('/login'); // Redirect to login page
            }
            // Reject the promise to propagate the error
            return $q.reject(rejection);
        }
    
    };
});


app.config(function($routeProvider) {
    $routeProvider
    .when('/products', { 
        templateUrl: 'views/product-list.html', 
        controller: 'ProductListController' 
    })

    .when('/product/:id', { 
        templateUrl: 'views/product-detail.html', 
        controller: 'ProductDetailController' 
    })

    .when('/cart', { 
        templateUrl: 'views/shopping-cart.html', 
        controller: 'ShoppingCartController' 
    })

    .when('/checkout', {
        templateUrl: 'views/checkout.html',
        controller: 'CheckoutController',
        resolve: { auth: function(AuthService) { return AuthService.isAuthenticated(); } }
    })
    .when('/orders', {
        templateUrl: 'views/order-history.html',
        controller: 'OrderHistoryController',
        resolve: { auth: function(AuthService) { return AuthService.isAuthenticated(); } }
    })
    .when('/login', { templateUrl: 'views/login.html', controller: 'AuthController' })

    .when('/register', { templateUrl: 'views/register.html', controller: 'AuthController' })

    .when('/admin',{
        templateUrl: 'views/product-manage.html',
        controller:'ProductManageController'
    })
    .otherwise({ redirectTo: '/products' });
});

app.run(function($rootScope, AuthService, $location) {
    AuthService.logout();
    $rootScope.isLoggedIn = function() { return AuthService.isLoggedIn(); };
    $rootScope.isAdmin = function() {
        return AuthService.isAdmin && AuthService.isAdmin;
    };
    $rootScope.getUsername = function() { return AuthService.getUsername(); };
    $rootScope.logout = function() { AuthService.logout(); $location.path('/login'); };
    $rootScope.searchName = '';
    $rootScope.$on('$routeChangeError', function(event, current, previous, rejection) {
        if (rejection && rejection.authRequired) {
            $location.path('/login');
        }
    });
});