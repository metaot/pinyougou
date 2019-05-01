// 定义服务层:
app.service("orderCountService",function($http) {

    this.search = function(searchEntity){
        return $http.post("../orderCount/searchCount.do",searchEntity);
    }


});