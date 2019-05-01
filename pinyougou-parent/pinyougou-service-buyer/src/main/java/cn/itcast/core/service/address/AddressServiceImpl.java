package cn.itcast.core.service.address;

import cn.itcast.core.dao.address.AddressDao;
import cn.itcast.core.pojo.address.Address;
import cn.itcast.core.pojo.address.AddressQuery;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author wophy
 */
@Service
public class AddressServiceImpl implements AddressService {

    @Resource
    private AddressDao addressDao;

    @Override
    public List<Address> findAddressListByLoginUser(String username) {
        AddressQuery addressQuery = new AddressQuery();
        addressQuery.createCriteria().andUserIdEqualTo(username);
        return addressDao.selectByExample(addressQuery);
    }

    @Transactional
    @Override
    public void add(Address address) {
        address.setIsDefault("0");
        address.setCreateDate(new Date());
        addressDao.insertSelective(address);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(Address address) {
        addressDao.updateByPrimaryKeySelective(address);
    }

    @Override
    public void delete(Long id) {
        addressDao.deleteByPrimaryKey(id);
    }


    @Override
    public Address findOne(Long id) {
        return addressDao.selectByPrimaryKey(id);
    }


    /**
     * 修改默认地址
     *
     * @param id
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void defaultAddress(Long id, String userId) {
        //取消原来默认
        AddressQuery query = new AddressQuery();
        AddressQuery.Criteria criteria = query.createCriteria();
        criteria.andUserIdEqualTo(userId);
        List<Address> addressList = addressDao.selectByExample(query);
        for (Address address : addressList) {
            //如果是默认
            if (address.getIsDefault().equals("1")){
                address.setIsDefault("0");
                addressDao.updateByPrimaryKeySelective(address);
            }
            //如果是要修的address
            if (address.getId().equals(id)){
                address.setIsDefault("1");
                addressDao.updateByPrimaryKeySelective(address);
            }
        }
    }
}
