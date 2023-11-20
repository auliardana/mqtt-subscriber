package com.example.mqttsub;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttServer;
import io.vertx.mqtt.MqttServerOptions;
import io.vertx.mqtt.MqttEndpoint;

import java.util.ArrayList;
import java.util.List;

public class MqttServerExample {
    private static final List<MqttEndpoint> clients = new ArrayList<>();

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        MqttServerOptions options = new MqttServerOptions()
                .setPort(1883)
                .setHost("localhost");
        MqttServer mqttServer = MqttServer.create(vertx, options);

        mqttServer.endpointHandler(client -> {
            System.out.println("MQTT client [" + client.clientIdentifier() + "] connected");
            client.accept(false);
            clients.add(client);

            client.publishHandler(message -> {
                try {
                    String payload = message.payload().toString();
                    System.out.println("Received message: " + payload);
                    broadcastMessage(payload);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            });

            client.disconnectHandler(disconnectMessage -> {
                System.out.println("Client disconnected: " + client.clientIdentifier());
                clients.remove(client);
            });
        });

        mqttServer.listen(ar -> {
            if (ar.succeeded()) {
                System.out.println("MQTT server is listening on port " + ar.result().actualPort());
            } else {
                System.out.println("Error starting the server");
                System.out.println(ar.cause().getMessage());
            }
        });
    }

    private static void broadcastMessage(String message) {
        for (MqttEndpoint client : clients) {
            //            client.publish("test/topic", Buffer.buffer(message), MqttQoS.AT_MOST_ONCE, false, false);
            client.publish("ownunit", Buffer.buffer(message), MqttQoS.AT_MOST_ONCE, false, false);
        }
    }
}
