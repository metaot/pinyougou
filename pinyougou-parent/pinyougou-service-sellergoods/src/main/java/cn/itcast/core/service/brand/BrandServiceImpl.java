package cn.itcast.core.service.brand;

import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.good.BrandQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import entity.PageResult;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@Service
public class BrandServiceImpl implements BrandService {
    /**
     * @Resource 由jdk提供
     * @Autowired 由spring提供
     */
    @Resource
    private BrandDao brandDao;
    @Override
    public List<Brand> findAll() {
        return brandDao.selectByExample(null);
    }

    @Override
    public List<Brand> findAll1() {
        return null;
    }


    @Override
    public PageResult findPage(Integer pageNo, Integer pageSize) {
            PageHelper.startPage(pageNo, pageSize);//分页
            Page<Brand> page = (Page<Brand>) brandDao.selectByExample(null);
            return new PageResult(page.getTotal(), page.getResult());
        }

    @Override
    public PageResult search(Integer pageNo, Integer pageSize, Brand brand) {
        //设置分页参数
        PageHelper.startPage(pageNo, pageSize);
        //设置查询条件
        BrandQuery brandQuery = new BrandQuery();
        BrandQuery.Criteria criteria = brandQuery.createCriteria();
        //先对参数进行非空判断
        if (brand.getName()!=null&&!"".equals(brand.getName().trim())){
            criteria.andNameLike("%"+brand.getName()+"%");
        }
        if (brand.getFirstChar()!=null&&!"".equals(brand.getFirstChar().trim())){
            criteria.andFirstCharEqualTo(brand.getFirstChar());

        }
        // 根据id排序
        brandQuery.setOrderByClause("id desc");
        //根据条件查询
        Page<Brand> page = (Page<Brand>) brandDao.selectByExample(brandQuery);

        return new PageResult(page.getTotal(), page.getResult());
    }
    @Transactional
    @Override
    public void add(Brand brand) {
        brandDao.insertSelective(brand);
    }

    @Override
    public Brand findOne(Long id) {
        return brandDao.selectByPrimaryKey(id);
    }
    @Transactional
    @Override
    public void update(Brand brand) {
        brandDao.updateByPrimaryKeySelective(brand);
    }
    @Transactional
    @Override
    public void delete(Long[] ids) {
       /* for (Long id : ids) {
            brandDao.deleteByPrimaryKey(id);
        }*/

            //批量删除
            if (ids!=null&&ids.length>0){
                brandDao.deleteByPrimaryKeys(ids);

            }
    }

    @Override
    public List<Map> selectOptionList() {
        return brandDao.selectOptionList();
    }

}

