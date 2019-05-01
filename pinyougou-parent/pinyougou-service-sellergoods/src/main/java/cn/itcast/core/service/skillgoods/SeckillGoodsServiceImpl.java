package cn.itcast.core.service.skillgoods;
import java.util.Date;
import java.util.List;

import cn.itcast.core.dao.seckill.SeckillGoodsDao;
import cn.itcast.core.pojo.seckill.SeckillGoods;
import cn.itcast.core.pojo.seckill.SeckillGoodsQuery;
import com.pinyougou.seckill.service.SeckillGoodsService;
import org.aspectj.internal.lang.annotation.ajcDeclareAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

	@Autowired
	private SeckillGoodsDao seckillGoodsMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<SeckillGoods> findAll() {
		return seckillGoodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<SeckillGoods> page=   (Page<SeckillGoods>) seckillGoodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(SeckillGoods seckillGoods) {
		seckillGoodsMapper.insert(seckillGoods);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(SeckillGoods seckillGoods){
		seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public SeckillGoods findOne(Long id){
		return seckillGoodsMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			seckillGoodsMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(SeckillGoods seckillGoods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		SeckillGoodsQuery example=new SeckillGoodsQuery();
		SeckillGoodsQuery.Criteria criteria = example.createCriteria();
		
		if(seckillGoods!=null){			
						if(seckillGoods.getTitle()!=null && seckillGoods.getTitle().length()>0){
				criteria.andTitleLike("%"+seckillGoods.getTitle()+"%");
			}
			if(seckillGoods.getSmallPic()!=null && seckillGoods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+seckillGoods.getSmallPic()+"%");
			}
			if(seckillGoods.getSellerId()!=null && seckillGoods.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+seckillGoods.getSellerId()+"%");
			}
			if(seckillGoods.getStatus()!=null && seckillGoods.getStatus().length()>0){
				criteria.andStatusLike("%"+seckillGoods.getStatus()+"%");
			}
			if(seckillGoods.getIntroduction()!=null && seckillGoods.getIntroduction().length()>0){
				criteria.andIntroductionLike("%"+seckillGoods.getIntroduction()+"%");
			}
	
		}
		
		Page<SeckillGoods> page= (Page<SeckillGoods>)seckillGoodsMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}
		
	@Autowired
	private RedisTemplate redisTemplate;

	@Override
	public List<SeckillGoods> findList() {
		
		List<SeckillGoods> seckillGoodsList =	redisTemplate.boundHashOps("seckillGoods").values();
		if(seckillGoodsList==null || seckillGoodsList.size()==0){
			SeckillGoodsQuery example=new SeckillGoodsQuery();
			SeckillGoodsQuery.Criteria criteria = example.createCriteria();
			criteria.andStatusEqualTo("1");// 审核通过的商品
			criteria.andStockCountGreaterThan(0);//库存数大于0
			criteria.andStartTimeLessThanOrEqualTo(new Date());//开始日期小于等于当前日期
			criteria.andEndTimeGreaterThanOrEqualTo(new Date());//截止日期大于等于当前日期
			seckillGoodsList = seckillGoodsMapper.selectByExample(example);
			//将列表数据装入缓存 
			for(SeckillGoods seckillGoods:seckillGoodsList){
				redisTemplate.boundHashOps("seckillGoods").put(seckillGoods.getId(), seckillGoods);
			}	
			System.out.println("从数据库中读取数据装入缓存");
		}else{
			System.out.println("从缓存中读取数据");
			
		}
		return seckillGoodsList;
		
	}

	@Override
	public SeckillGoods findOneFromRedis(Long id) {
		return  (SeckillGoods)redisTemplate.boundHashOps("seckillGoods").get(id);
	}
	
}
