app.service('OrderService', function($http, API_BASE_URL, AuthService) {
    var service = {};

    service.createOrder = function() {
        return $http.post(API_BASE_URL + '/orders', {}, { headers: { 'Authorization': 'Bearer ' + AuthService.getToken() } });
    };

    service.getUserOrders = function() {
        return $http.get(API_BASE_URL + '/orders/my', { headers: { 'Authorization': 'Bearer ' + AuthService.getToken() } });
    };
    
    return service;
});