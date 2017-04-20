package com.orctom.bitflow.producer;

import com.orctom.laputa.utils.SimpleMetrics;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DummyProducer {

  private static final Logger LOGGER = LoggerFactory.getLogger(DummyProducer.class);
  private static final Config CONFIG = ConfigFactory.load();
  private static final String BROKER_URL = CONFIG.getString("mqtt.url");
  private static final String TOPIC = CONFIG.getString("mqtt.topic");

  private void start() {
    LOGGER.info("[startup]");
    SimpleMetrics metrics = SimpleMetrics.create(LOGGER, 10, TimeUnit.SECONDS);
    ExecutorService es = Executors.newFixedThreadPool(200);
    int threads = CONFIG.getInt("producer.threads");
    int size = CONFIG.getInt("producer.size");
    for (int i = 0; i < threads; i++) {
      es.submit(() -> {
        BlockingConnection conn = null;
        try {
          MQTT mqtt = new MQTT();
          mqtt.setHost(BROKER_URL);
          conn = mqtt.blockingConnection();
          conn.connect();

          String content = "Message from DummyProducer " + System.currentTimeMillis();
          for (int j = 0; j < size; j++) {
            conn.publish(TOPIC, content.getBytes(), QoS.AT_LEAST_ONCE, false);
            metrics.mark("sent");
          }

        } catch (Exception e) {
          e.printStackTrace();
          System.exit(1);
        } finally {
          if (null != conn) {
            try {
              conn.disconnect();
            } catch (Exception ignored) {
            }
          }
        }
      });
    }

    es.shutdown();
    try {
      es.awaitTermination(10, TimeUnit.MINUTES);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    es.shutdownNow();
    LOGGER.info("[shutdown]");
  }

  public static void main(String... args) {
    new DummyProducer().start();
  }
}
