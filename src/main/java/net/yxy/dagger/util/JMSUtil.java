package net.yxy.dagger.util;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import net.yxy.dagger.global.Constants;

import org.apache.activemq.ActiveMQConnectionFactory;

public final class JMSUtil {
	
	public static boolean sendToQueue(String queue, String json){
		 try {
             // Create a ConnectionFactory
             ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(Constants.JMS_SERVER_URL);
             // Create a Connection
             Connection connection = connectionFactory.createConnection();
             connection.start();
             // Create a Session
             Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
             // Create the destination (Topic or Queue)
             Destination destination = session.createQueue(queue);
             // Create a MessageProducer from the Session to the Topic or Queue
             MessageProducer producer = session.createProducer(destination);
             producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
             // Create a messages
             TextMessage message = session.createTextMessage(json);
             // Tell the producer to send the message
             producer.send(message);
             // Clean up
             session.close();
             connection.close();
         }
         catch (Exception e) {
             e.printStackTrace();
             return false ;
         }
		 
		 return true ;
	}
}
