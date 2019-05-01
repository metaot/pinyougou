//控制层
app.controller('orderCountController' ,function($scope,$controller,orderCountService) {




    // 显示状态
    $scope.orderStatus = ["","未付款","已付款","未发货","已发货","交易成功","交易失败","待评价"];
    $scope.orderStatus2 = ["0","1","2","3","4","5","6","7"];
    $scope. paymentTypes=["","在线支付","货到付款"];
    $scope.searchEntity={};


    $scope.orderValue={};

   // $scope.everyDayList=entity.everyDay;

    // 假设定义一个查询的实体：searchEntity
    $scope.search = function(){
        // 向后台发送请求获取数据:
        orderCountService.search($scope.searchEntity).success(function(response){
            $scope.countP = response.countP;
            $scope.everyDayList = response.everyDayList;
        });
    }


});