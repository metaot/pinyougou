package cn.itcast.core.service.address;

import cn.itcast.core.pojo.address.Address;

import java.util.List;

/**
 * @author wophy
 */
public interface AddressService{

    List<Address> findAddressListByLoginUser(String username);

    /**
     * 添加
     * @param address
     */
    void add(Address address);

    /**
     * 更新
     * @param address
     */
    void update(Address address);

    /**
     * 删除
     * @param id
     */
    void delete(Long id);

    /**
     * 修改的回显
     * @param id
     * @return
     */
    Address findOne(Long id);

    /**
     * 修改默认地址
     * @param id
     */
    void defaultAddress(Long id, String userId);
}
