//控制层
app.controller('orderViewController' ,function($scope,$controller,orderCountService) {


    // 假设定义一个查询的实体：searchEntity
    $scope.searchView = function(){
        // 向后台发送请求获取数据:
        orderCountService.searchView().success(function(response){
           /* $scope.methon = response.countP;
            $scope.everyDayList = response.everyDayList;*/
        });
    }


});