// 定义服务层:
app.service("userUpdateService",function($http){
    this.findAll = function(){
        return $http.get("../user/findAll.do");
    }

    this.findPage = function(page,rows){
        return $http.get("../brand/findPage.do?pageNo="+page+"&pageSize="+rows);
    }



    this.update=function(entity){
        return $http.post("../brand/update.do",entity);
    }








});