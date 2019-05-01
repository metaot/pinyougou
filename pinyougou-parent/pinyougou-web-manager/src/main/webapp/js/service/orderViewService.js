// 定义服务层:
app.service("orderCountService",function($http) {

    this.searchView = function(){
        return $http.post("../orderView/searchView.do");
    }


});