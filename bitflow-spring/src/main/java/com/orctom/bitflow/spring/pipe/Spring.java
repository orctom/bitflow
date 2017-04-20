package com.orctom.bitflow.spring.pipe;

import com.orctom.bitflow.spring.mqtt.MqttClient;
import com.orctom.pipeline.annotation.Actor;
import com.orctom.pipeline.precedure.Hydrant;
import com.orctom.pipeline.util.IdUtils;
import com.orctom.rmq.Message;
import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Actor("spring")
public class Spring extends Hydrant {

  private static final Logger LOGGER = LoggerFactory.getLogger(Spring.class);
  private static final Logger DATA_LOGGER = LoggerFactory.getLogger("com.e6wifi.bitflow.data");

  @Override
  public void run() {
    MqttClient.getInstance().subscribe(new Listener() {
      @Override
      public void onConnected() {
        LOGGER.info("Connected");
      }

      @Override
      public void onDisconnected() {
        LOGGER.info("disconnected");
      }

      @Override
      public void onPublish(UTF8Buffer utf8Buffer, Buffer body, Runnable ack) {
        Message message = new Message(IdUtils.generate(), body.toByteArray());
        sendToSuccessors(message);
        DATA_LOGGER.info(message.getId());
        ack.run();
      }

      @Override
      public void onFailure(Throwable t) {
        LOGGER.error(t.getMessage(), t);
      }
    });
  }
}
