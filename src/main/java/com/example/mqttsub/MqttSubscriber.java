package com.example.mqttsub;

import io.vertx.core.Vertx;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttSubscriber {

    @Bean
    public MqttClient mqttClient() {
        Vertx vertx = Vertx.vertx();
        MqttClientOptions options = new MqttClientOptions()
                .setClientId("subscriberClient")
                .setCleanSession(true);
        MqttClient mqttClient = MqttClient.create(vertx, options);

        String[] topics = {"ownunit", "radar", "ais", "adsb"};
        String brokerHost = "localhost";
        int brokerPort = 1883;

        mqttClient.connect(brokerPort, brokerHost, ar -> {
            if (ar.succeeded()) {
                System.out.println("Connected to MQTT broker");

                int qos = 0;
                for (String topic : topics) {
                    mqttClient.subscribe(topic, qos, subscribeHandler -> {
                        if (subscribeHandler.succeeded()) {
                            System.out.println("Subscribed to topic: " + topic);
                        } else {
                            System.out.println("Failed to subscribe to topic: " + topic);
                        }
                    });
                }

                mqttClient.publishHandler(message -> {
                    try {
                        String receivedMessage = message.payload().toString();
                        System.out.println("Received message: " + receivedMessage);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                });
            } else {
                System.out.println("ERROR: Failed to connect to MQTT broker");
                System.out.println(ar.cause().getMessage());
            }
        });

        return mqttClient;
    }
}