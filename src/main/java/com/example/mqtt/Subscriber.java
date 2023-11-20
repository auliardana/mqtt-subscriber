package com.example.mqtt;

import io.vertx.core.Vertx;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Subscriber {

    @Bean
    public MqttClient mqttClient() {
        Vertx vertx = Vertx.vertx();

        MqttClientOptions options = new MqttClientOptions()
                .setClientId("subscriberClient")
                .setCleanSession(true);

        MqttClient mqttClient = MqttClient.create(vertx, options);

        String brokerHost = "localhost";
//        String brokerHost = "public.mqtthq.com";
        int brokerPort = 1883;

        mqttClient.connect(brokerPort, brokerHost, ar -> {
            if (ar.succeeded()) {
                System.out.println("Connected to MQTT broker");

                String topic = "ownunit";
                int qos = 0;

                mqttClient.subscribe(topic, qos, subscribeHandler -> {
                    if (subscribeHandler.succeeded()) {
                        System.out.println("Subscribed to topic: " + topic);
                    } else {
                        System.out.println("Failed to subscribe to topic: " + topic);
                    }
                });

                mqttClient.publishHandler(message -> {
                    try {
                        String receivedMessage = message.payload().toString();
                        System.out.println("Received message: " + receivedMessage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } else {
                System.out.println("Failed to connect to MQTT broker");
                ar.cause().printStackTrace();
            }
        });

        return mqttClient;
    }
}
