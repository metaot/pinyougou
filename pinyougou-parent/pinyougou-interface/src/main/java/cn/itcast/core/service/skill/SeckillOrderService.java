package cn.itcast.core.service.skill;

import cn.itcast.core.pojo.seckill.SeckillOrder;
import entity.PageResult;

import java.util.List;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SeckillOrderService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<SeckillOrder> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(SeckillOrder seckillOrder);
	
	
	/**
	 * 修改
	 */
	public void update(SeckillOrder seckillOrder);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public SeckillOrder findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long[] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(SeckillOrder seckillOrder, int pageNum, int pageSize);
	
	/**
	 * 秒杀下单
	 * @param seckillId
	 * @param userId
	 */
	public void submitOrder(Long seckillId, String userId);
	
	/**
	 * 从缓存中提取订单
	 * @param userId
	 * @return
	 */
	public SeckillOrder searchOrderFromRedisByUserId(String userId);
	
	/**
	 * 保存订单到数据库
	 * @param userId
	 * @param orderId
	 * @param transactionId
	 */
	public void saveOrderFromRedisToDb(String userId, Long orderId, String transactionId);
	
	/**
	 * 删除缓存中订单
	 * @param userId
	 * @param orderId
	 */
	public void deleteOrderFromRedis(String userId, Long orderId);
}
