package cn.itcast.core.controller.address;

import cn.itcast.core.pojo.address.Address;
import cn.itcast.core.service.address.AddressService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 1wophy
 */
@RestController
@RequestMapping("/address")
public class AddressController {


    @Reference
    private AddressService addressService;

    @RequestMapping("/search.do")
    public List<Address> search(){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return  addressService.findAddressListByLoginUser(userId);
    }
    @RequestMapping("/add.do")
    private Result add(@RequestBody Address address){
        try {
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();
            address.setUserId(userId);

            addressService.add(address);
            return new Result(true,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }

    @RequestMapping("/update.do")
    private Result update(@RequestBody Address address){
        try {
            addressService.update(address);
            return new Result(true,"更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"更新失败");
        }
    }

    @RequestMapping("/delete.do")
    private Result delete(Long id){
        try {
            addressService.delete(id);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }
    @RequestMapping("/findOne.do")
    public Address findOne(Long id){
        System.out.println(addressService.findOne(id).toString());
       return addressService.findOne(id);
    }
    @RequestMapping("/default.do")
    public Result defaultAddress(Long id){
        try {
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();
            addressService.defaultAddress(id,userId);
            return new Result(true,"修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }

}
