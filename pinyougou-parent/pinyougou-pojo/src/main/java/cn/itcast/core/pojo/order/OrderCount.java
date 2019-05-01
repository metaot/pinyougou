package cn.itcast.core.pojo.order;

import java.io.Serializable;
import java.util.List;

/**
 * @author 订单统计pojo
 */
public class OrderCount  implements Serializable {

    /**
     * 订单总金额
     */
    private Double totleMoney;



    /**
     * 总条数
     */
    private Long totleRows;

    private List<OrderCountOfEveryDay> everyDay;

    public void setEveryDay(List<OrderCountOfEveryDay> everyDay) {
        this.everyDay = everyDay;
    }

    public List<OrderCountOfEveryDay> getEveryDay() {
        return everyDay;
    }

    public Double getTotleMoney() {
        return totleMoney;
    }

    public void setTotleMoney(Double totleMoney) {
        this.totleMoney = totleMoney;
    }

    public Long getTotleRows() {
        return totleRows;
    }

    public void setTotleRows(Long totleRows) {
        this.totleRows = totleRows;
    }

    @Override
    public String toString() {
        return "OrderCount{" +
                "totleMoney=" + totleMoney +
                ", totleRows=" + totleRows +
                ", everyDay=" + everyDay +
                '}';
    }
}
