package com.orctom.bitflow.summer.pipe;

import com.orctom.pipeline.annotation.Actor;
import com.orctom.pipeline.precedure.Pipe;
import com.orctom.rmq.Ack;
import com.orctom.rmq.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Actor(role = "summer", interestedRoles = "spring")
public class Summer extends Pipe {

  private static final Logger LOGGER = LoggerFactory.getLogger(Summer.class);
  private static final Logger DATA_LOGGER = LoggerFactory.getLogger("com.e6wifi.bitflow.data");

  @Override
  public Ack onMessage(Message message) {
    try {
      // process message
      DATA_LOGGER.info(message.getId());

      // sen to next pipe
      sendToSuccessors(message);
      return Ack.DONE;
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      return Ack.DONE;
    }
  }
}
