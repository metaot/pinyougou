package cn.itcast.core.listener;

import cn.itcast.core.service.staticpage.StaticPageService;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * 自定义监听器 删除商品详情页的静态页
 */
public class PageDelListener implements MessageListener {

    @Resource
    private StaticPageService staticPageService;

    //获取消息 并且消费消息
    @Override
    public void onMessage(Message message) {
        try {
            //取出消息
            ActiveMQTextMessage activeMQTextMessage= (ActiveMQTextMessage) message;

            String id = activeMQTextMessage.getText();
            //消费消息
            System.out.println("service-pagedel获取的id:"+id);

            staticPageService.delHtml(Long.parseLong(id));
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
