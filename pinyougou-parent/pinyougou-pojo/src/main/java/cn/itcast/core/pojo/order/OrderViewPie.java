package cn.itcast.core.pojo.order;

import java.io.Serializable;
import java.util.List;

public class OrderViewPie implements Serializable {
    private String year;
    private List date;

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public List getDate() {
        return date;
    }

    public void setDate(List date) {
        this.date = date;
    }
}
