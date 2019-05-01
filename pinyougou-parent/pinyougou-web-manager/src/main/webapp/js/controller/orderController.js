//控制层
app.controller('orderController' ,function($scope,$controller,orderService) {

    $controller('baseController', {$scope: $scope});//继承

// 查询一个:
    $scope.findById = function(orderId){
        orderService.findOne(orderId).success(function(response){
            // {id:xx,name:yy,firstChar:zz}
            $scope.entity = response;
        });
    }


    // 显示状态
    $scope.orderStatus = ["","未付款","已付款","未发货","已发货","交易成功","交易失败","待评价"];
    $scope.orderStatus2 = ["0","1","2","3","4","5","6","7"];
    $scope. paymentTypes=["","在线支付","货到付款"];
    $scope.searchEntity={};

    // 假设定义一个查询的实体：searchEntity
    $scope.search = function(page,rows){
        // 向后台发送请求获取数据:
        orderService.search(page,rows,$scope.searchEntity).success(function(response){
            $scope.paginationConf.totalItems = response.total;
            $scope.list = response.rows;
        });
    }



  /*  $scope.searchCount = function(page,rows){
        // 向后台发送请求获取数据:
        orderService.searchCount(page,rows).success(function(response){
            $scope.paginationConf.totalItems = response.total;
            $scope.list = response.rows;
        });
    }
*/

});