package cn.itcast.core.pojo.order;

import java.io.Serializable;

/**
 * @author wophy
 */
public class OrderView implements Serializable {
    /**
     * 月份
     */
    private String moths;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    /**
     * 每月数量
     */
    private Integer total;

    public String getMoths() {
        return moths;
    }

    public void setMoths(String moths) {
        this.moths = moths;
    }
}
