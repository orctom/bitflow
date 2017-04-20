package com.orctom.bitflow.spring.mqtt;

import com.orctom.pipeline.Pipeline;
import com.typesafe.config.Config;
import org.fusesource.mqtt.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.Arrays;

public class MqttClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(MqttClient.class);

  private static final MqttClient INSTANCE = new MqttClient();

  private Config mqttConfig;
  private CallbackConnection connection;

  private MqttClient() {
    try {
      MQTT mqtt = initMQTT();
      connection = mqtt.callbackConnection();
      connect();
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    }
  }

  private MQTT initMQTT() throws URISyntaxException {
    MQTT mqtt = new MQTT();

    mqttConfig = Pipeline.getInstance().getConfig().getConfig("emqtt");
    String url = mqttConfig.getString("url");
    String clientId = mqttConfig.getString("clientId");
    int keepAlive = mqttConfig.getInt("keepAlive");
    boolean cleanSession = mqttConfig.getBoolean("clearSession");
    String version = mqttConfig.getString("version");

    mqtt.setHost(url);
    mqtt.setClientId(clientId);
    mqtt.setKeepAlive((short) keepAlive);
    mqtt.setCleanSession(cleanSession);
    mqtt.setVersion(version);
    return mqtt;
  }

  private void connect() {
    connection.connect(new Callback<Void>() {
      @Override
      public void onSuccess(Void value) {
        LOGGER.info("Connected");
      }

      @Override
      public void onFailure(Throwable t) {
        LOGGER.error(t.getMessage(), t);
      }
    });
  }

  public static MqttClient getInstance() {
    return INSTANCE;
  }

  public void subscribe(Listener listener) {
    String topic = mqttConfig.getString("topic");
    LOGGER.info("subscribing to topic: {}", topic);
    connection.listener(listener);
    Topic[] topics = {new Topic(topic, QoS.EXACTLY_ONCE)};
    connection.subscribe(topics, new Callback<byte[]>() {
      @Override
      public void onSuccess(byte[] payload) {
        LOGGER.info("Subscribed {}", Arrays.toString(payload));
      }

      @Override
      public void onFailure(Throwable t) {
        LOGGER.error(t.getMessage(), t);
      }
    });
  }
}
