package com.orctom.bitflow.winter.pipe;

import com.orctom.pipeline.annotation.Actor;
import com.orctom.pipeline.precedure.Outlet;
import com.orctom.rmq.Ack;
import com.orctom.rmq.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Actor(role = "winter", interestedRoles = "autumn")
public class Winter extends Outlet {

  private static final Logger LOGGER = LoggerFactory.getLogger(Winter.class);
  private static final Logger DATA_LOGGER = LoggerFactory.getLogger("com.e6wifi.bitflow.data");

  @Override
  public Ack onMessage(Message message) {
    try {
      // process message
      DATA_LOGGER.info(message.getId());

      return Ack.DONE;
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      return Ack.DONE;
    }
  }
}
