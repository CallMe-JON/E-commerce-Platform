app.controller('AuthController', function($scope, AuthService, $location) {
    $scope.user = {};
    $scope.credentials = {};
    $scope.loginError = '';
    $scope.registerError = '';

    $scope.login = function() {
        AuthService.login($scope.credentials)
            .then(function(success) {
                if (success) { $location.path('/products'); }
                else { $scope.loginError = "Invalid username or password."; }
            })
            .catch(function(error) {
                $scope.loginError = "Login failed: " + (error.data || error.statusText);
                console.error("Login error:", error);
            });
    };

    $scope.register = function() {
        AuthService.register($scope.user)
            .then(function(response) {
                alert("Registration successful! Please login.");
                $location.path('/login');
            })
            .catch(function(error) {
                $scope.registerError = "Registration failed: " + (error.data || error.statusText);
                console.error("Registration error:", error);
            });
    };
});