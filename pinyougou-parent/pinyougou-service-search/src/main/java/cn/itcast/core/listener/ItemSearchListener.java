package cn.itcast.core.listener;

/**
 * @author wophy
 */


import cn.itcast.core.service.search.ItemSearchService;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * 自定义的消息监听器:完成商品添加solr所有库
 */
public class ItemSearchListener implements MessageListener {
    @Resource
    private ItemSearchService itemSearchService;
//Error:Cannot build artifact 'pinyougou-service-search (1):war exploded' because it is included into a circular dependency (artifact 'pinyougou-service-search (1):war exploded', artifact 'pinyougou-service-search:war exploded')
    @Override
    public void onMessage(Message message) {
        try {
            //1.取出消息
            ActiveMQTextMessage activeMQTextMessage = (ActiveMQTextMessage) message;
            String id = activeMQTextMessage.getText();
            //2.消费消息
            System.out.println("service-search获取到了id:"+id);
            itemSearchService.siShow(Long.parseLong(id));
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
