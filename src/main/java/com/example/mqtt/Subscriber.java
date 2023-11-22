package com.example.mqtt;

import io.netty.handler.codec.mqtt.MqttQoS;
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
        int brokerPort = 1883;
        String topic = "radar";

        mqttClient.connect(brokerPort, brokerHost, ar -> {
            if (ar.succeeded()) {
                System.out.println("Connected to MQTT broker");

                // Subscribe to a specific topic with QoS level 1
                mqttClient.subscribe(topic, MqttQoS.AT_MOST_ONCE.value(), subscribeResult -> {
                    if (subscribeResult.succeeded()) {
                        System.out.println("Subscribed to the topic!");

                    } else {
                        System.out.println("Failed to subscribe: " + subscribeResult.cause().getMessage());
                    }
                });

                mqttClient.publishHandler(message -> {
                    String receivedTopic = message.topicName();
                    if (topic.equals(receivedTopic)) {
                        // Hanya proses pesan jika topik adalah "ownunit"
                        System.out.println("Received message on topic: " + receivedTopic);
                        System.out.println("Message content: " + message.payload().toString());
                        System.out.println("QoS: " + message.qosLevel());
                    } else {
//                        System.out.println("Ignoring message from topic: " + receivedTopic);
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
