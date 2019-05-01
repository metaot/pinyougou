// 定义服务层:
app.service("orderService",function($http) {

    this.findOne=function(orderId){
        return $http.get("../order/findOne.do?orderId="+orderId);
    }

    this.search = function(page,rows,searchEntity){
        return $http.post("../order/search.do?page="+page+"&rows="+rows,searchEntity);
    }



});