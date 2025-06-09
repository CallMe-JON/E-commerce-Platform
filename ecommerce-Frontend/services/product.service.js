app.service('ProductService', function($http, API_BASE_URL ){
 
    

    this.getAllProducts = function(){
        return $http.get( API_BASE_URL + '/products');
    };

    this.getProductById = function(id){
        return $http.get(API_BASE_URL + '/products/' + id);
    };

    this.create = function(product) {
        return $http.post(API_BASE_URL + '/products', product);
    };

    this.update = function(product) {
        return $http.put(API_BASE_URL + '/products/' + product.id, product);
    };

    this.delete = function(id) {
        return $http.delete(API_BASE_URL + '/products/' + id);
    };

})