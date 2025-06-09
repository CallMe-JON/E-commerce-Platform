app.service('AuthService', function($http, API_BASE_URL, $window, $location) {
    var service = {};
    var tokenKey = 'jwtToken';

    service.login = function(credentials) {
        return $http.post(API_BASE_URL + '/auth/login', credentials)
            .then(function(response) {
                if (response.data.jwt) {
                    $window.localStorage.setItem(tokenKey, response.data.jwt);
                    return true;
                }
                return false;
            });
    };

    service.register = function(user) { return $http.post(API_BASE_URL + '/auth/register', user); };
    service.getToken = function() { return $window.localStorage.getItem(tokenKey); };
    service.isLoggedIn = function() { var token = service.getToken(); return !!token; };
    service.getUsername = function() {
        var token = service.getToken();
        if (token) {
            try {
                var base64Url = token.split('.')[1];
                var base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
                var jsonPayload = decodeURIComponent($window.atob(base64).split('').map(function(c) {
                    return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
                }).join(''));
                return JSON.parse(jsonPayload).sub;
            } catch (e) { console.error("Error decoding token:", e); return null; }
        }
        return null;
    };
    service.logout = function() { $window.localStorage.removeItem(tokenKey); };
    service.isAuthenticated = function() {
        if (service.isLoggedIn()) { return true; }
        else { $location.path('/login'); return false; }
    };

    service.isAdmin = function(){
        var user = JSON.parse(localStorage.getItem('user'));
        return user && user.role === 'ADMIN';
    }
    return service;

    
});