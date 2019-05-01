package cn.itcast.core.service.spec;

import cn.itcast.core.dao.specification.SpecificationDao;
import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.pojo.specification.SpecificationOption;
import cn.itcast.core.pojo.specification.SpecificationOptionQuery;
import cn.itcast.core.pojo.specification.SpecificationQuery;

import cn.itcast.core.vo.SpecVo;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class SpecServiceImpl implements SpecService {

    @Resource
    private SpecificationDao specificationDao;
    @Resource
    private SpecificationOptionDao specificationOptionDao;
    @Override
    public PageResult search(Integer page, Integer rows, Specification specification) {
        //设置分页参数
        PageHelper.startPage(page,rows);
        //设置查询条件
        SpecificationQuery example = new SpecificationQuery();
        //对条件进行非空判断
        if (specification.getSpecName()!=null&&!"".equals(specification.getSpecName().trim())){
            example.createCriteria().andSpecNameLike("%"+specification.getSpecName()+"%");
        }

        //设置根据id排序
        example.setOrderByClause("id desc");

        //根据条件查询
        Page<Specification> p = (Page<Specification>) specificationDao.selectByExample(example);

        return new PageResult(p.getTotal(),p.getResult());
    }

    /**
     * 添加规格
     * @param specVo
     */
    @Transactional
    @Override
    public void add(SpecVo specVo) {
        //先规格表
        Specification specification = specVo.getSpecification();
        //保存规格 需要回显主键
        specificationDao.insertSelective(specification);

        //现在保存规格选项
        List<SpecificationOption> specificationOptionList = specVo.getSpecificationOptionList();
        //规格选项中有规格的主键 先对其进行设置
        if (specificationOptionList!=null&&specificationOptionList.size()>0){
            for (SpecificationOption specificationOption : specificationOptionList) {
                specificationOption.setSpecId(specification.getId());
                //执行保存 但是是一个一个存 可能内存溢出
            }
        }
        //保存完规格选项 执行保存
        specificationOptionDao.insertSelectives(specificationOptionList);
    }

    @Override
    public SpecVo findOne(Long id) {
        //查询规格  specification
        Specification specification = specificationDao.selectByPrimaryKey(id);

        //查询规格项
                //封装查询条件
        SpecificationOptionQuery specificationOptionQuery = new SpecificationOptionQuery();
        specificationOptionQuery.createCriteria().andSpecIdEqualTo(id);
        List<SpecificationOption> specificationOptionList = specificationOptionDao.selectByExample
                (specificationOptionQuery);
        SpecVo specVo = new SpecVo();
        specVo.setSpecification(specification);
        specVo.setSpecificationOptionList(specificationOptionList);
        return specVo;
    }
    @Transactional
    @Override
    public void update(SpecVo specVo) {
        //更新规格 specification
        specificationDao.updateByPrimaryKeySelective(specVo.getSpecification());

        //更新 specificationOption

        //先删除
        SpecificationOptionQuery specificationOptionQuery = new SpecificationOptionQuery();
        specificationOptionQuery.createCriteria().andSpecIdEqualTo(specVo.getSpecification().getId());
        specificationOptionDao.deleteByExample(specificationOptionQuery);

        // 在插入
        List<SpecificationOption> specificationOptionList = specVo.getSpecificationOptionList();

        for (SpecificationOption specificationOption : specificationOptionList) {
            //设置外键
            specificationOption.setSpecId(specVo.getSpecification().getId());
            //逐条插入
            specificationOptionDao.insert(specificationOption);
        }
    }

    /**
     * 根据id删除
     * @param ids
     */
    @Transactional
    @Override
    public void delete(Long[] ids) {
        //删除specification 批量删除
        specificationDao.deleteByPrimaryKeys(ids);

        //在删除
        //先删除
        SpecificationOptionQuery specificationOptionQuery = new SpecificationOptionQuery();
        for (Long id : ids) {
            specificationOptionQuery.createCriteria().andSpecIdEqualTo(id);
        }

        specificationOptionDao.deleteByExample(specificationOptionQuery);

    }

    /**
     * 新建模板是规格展示
     * @return
     */
    @Override
    public List<Map> selectOptionList() {
        return specificationDao.selectOptionList();
    }
}
