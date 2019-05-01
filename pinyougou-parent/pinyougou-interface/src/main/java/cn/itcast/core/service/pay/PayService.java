package cn.itcast.core.service.pay;

import java.util.Map;

/**
 * @author wophy
 * 支付模块
 */
public interface PayService {

    /**
     * 订单支付页面的数据显示
     * @param userId
     * @return
     */
    public Map createNative(String userId);

    /**
     * 支付成功更新支付日志信息
     * @param out_trade_no 支付日志id 支付编号
     * @return
     */
    public Map queryPayStatus(String out_trade_no);
}
