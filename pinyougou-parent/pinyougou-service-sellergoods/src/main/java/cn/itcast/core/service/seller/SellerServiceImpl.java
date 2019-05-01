package cn.itcast.core.service.seller;

import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.pojo.seller.SellerQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class SellerServiceImpl implements SellerService {
    @Resource
    private SellerDao sellerDao;

    @Transactional
    @Override
    public void add(Seller seller) {
        // 初始化该商家审核的状态：待审核-0
      seller.setStatus("0");
        seller.setCreateTime(new Date());
        // 需要对密码进行加密：md5、加盐（盐值）spring
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String password = bCryptPasswordEncoder.encode(seller.getPassword());
        seller.setPassword(password);
        sellerDao.insertSelective(seller);
    }

    /**
     * 未审批的注册商家展示
     * @param page
     * @param rows
     * @param seller
     */
    @Override
    public PageResult search(Integer page, Integer rows, Seller seller) {
        //设置分页参数
        PageHelper.startPage(page,rows);
        //设置查询条件
        SellerQuery sellerQuery = new SellerQuery();
        if (seller.getStatus()!=null&&!"".equals(seller.getStatus().trim())){

            sellerQuery.createCriteria().andStatusEqualTo(seller.getStatus());
        }
        Page<Seller> p = (Page<Seller>) sellerDao.selectByExample(sellerQuery);
        return new PageResult(p.getTotal(),p.getResult());
    }

    @Override
    public Seller findOne(String sellerId) {
        return sellerDao.selectByPrimaryKey(sellerId);
    }

    /**
     * 根据商家的名称id 跟新审核状态
     * @param sellerId
     * @param status
     */
    @Transactional
    @Override
    public void updateStatus(String sellerId, String status) {
        Seller seller = new Seller();
        seller.setStatus(status);
        seller.setSellerId(sellerId);

        sellerDao.updateByPrimaryKeySelective(seller);
    }

    @Override
    public List<Seller> findAll() {
        return sellerDao.selectByExample(null);

    }

}
