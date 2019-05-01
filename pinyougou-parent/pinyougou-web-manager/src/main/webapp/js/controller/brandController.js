// 定义控制器:
app.controller("brandController",function($scope,$http,$controller,brandService){
	// AngularJS中的继承:伪继承
	$controller('baseController',{$scope:$scope});

	// 查询所有的品牌列表的方法:
	$scope.findAll = function(){
		// 向后台发送请求:
		brandService.findAll().success(function(response){
			$scope.list = response;
		});
	}
    // 查询所有的品牌列表的方法:


	// 分页查询
	$scope.findPage = function(page,rows){
		// 向后台发送请求获取数据:
		brandService.findPage(page,rows).success(function(response){
			$scope.paginationConf.totalItems = response.total;
			$scope.list = response.rows;
		});
	}

	// 保存品牌的方法:
	$scope.save = function(){
		// 区分是保存还是修改
		var object;
		if($scope.entity.id != null){
			// 更新
			object = brandService.update($scope.entity);
		}else{
			// 保存
			object = brandService.add($scope.entity);
		}
		object.success(function(response){
			// {flag:true,message:xxx}
			// 判断保存是否成功:
			if(response.flag){
				// 保存成功
				alert(response.message);
				$scope.reloadList();
			}else{
				// 保存失败
				alert(response.message);
			}
		});
	}

	// 查询一个:
	$scope.findById = function(id){
		brandService.findOne(id).success(function(response){
			// {id:xx,name:yy,firstChar:zz}
			$scope.entity = response;
		});
	}

	// 删除品牌:
	$scope.dele = function() {
        if (confirm('确定要删除吗？')) {
            brandService.dele($scope.selectIds).success(function (response) {
                // 判断保存是否成功:
                if (response.flag == true) {
                    // 保存成功
                    // alert(response.message);
                    $scope.reloadList();
                    $scope.selectIds = [];
                } else {
                    // 保存失败
                    alert(response.message);
                }
            });
        }
    }

    $scope.ExportPostSensitiveWordsList = function () {
        $http({
            url:'/brand/downLoad.do',
            method: "POST",
            data: $scope.searchEntity, //this is your json data string
            responseType: 'arraybuffer'
        }).success(function (data) {
            var blob = new Blob([data], {type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"});
            var objectUrl = URL.createObjectURL(blob);
            window.open(objectUrl);
        }).error(function (data) {
            //upload failed
        });

    }
      /*  $scope.ExportPostSensitiveWordsList = function () {
            var url =  "http://localhost:9101/post/export/Export.do";
            $http({
                url: url,
                method: "POST",
                data: $scope.searchEntity, //需要带的参数
                headers: {
                    'Content-type': 'application/json'//发送内容的类型，这是使用'application/json'
                },
                responseType: 'arraybuffer'//返回结果的类型，字节流
            }).success(function (data, status, headers, config) {
                var blob = new Blob([data], {type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"});//以二进制形式存储，并转化为Excel
                var objectUrl = URL.createObjectURL(blob);
                var now=new Date();
                var fileName=now.toLocaleDateString() +'            '+now.getHours()+'/'+now.getMinutes()+'/'+now.getSeconds()+"xxx"+ ".xls";//自定义导出excel表名字，这里使用日期
                saveAs(blob, fileName);//这里使用了文件导出插件FileSaver.js
            }).error(function (data, status, headers, config) {
                Alert.error("导出失败！");
            });
        };*/


    $scope.searchEntity={};


    // 假设定义一个查询的实体：searchEntity
    $scope.search = function(page,rows){
        // 向后台发送请求获取数据:
        brandService.search(page,rows,$scope.searchEntity).success(function(response){
            $scope.paginationConf.totalItems = response.total;
            $scope.list = response.rows;
        });
    }

    });




   /* // 假设定义一个查询的实体：searchEntity
    $scope.downLoad = function(page,rows){
        // 向后台发送请求获取数据:
        brandService.downLoad(page,rows,$scope.searchEntity).success(function(response){
           //$scope.paginationConf.totalItems = response.total;
           //$scope.list = response.rows;
        });
    }
*/
