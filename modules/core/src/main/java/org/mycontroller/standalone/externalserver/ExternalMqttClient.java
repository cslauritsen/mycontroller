/*
 * Copyright 2015-2016 Jeeva Kandasamy (jkandasa@gmail.com)
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mycontroller.standalone.externalserver;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.mycontroller.standalone.restclient.RestFactory.TRUST_HOST_TYPE;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Jeeva Kandasamy (jkandasa)
 * @since 0.0.3
 */
@Slf4j
public class ExternalMqttClient {
    public static final long TIME_TO_WAIT = 100;
    public static final long DISCONNECT_TIME_OUT = 1000 * 1;
    public static final int CONNECTION_TIME_OUT = 1000 * 5;
    public static final int KEEP_ALIVE = 1000 * 5;
    public static final int QOS = 0;

    private IMqttClient mqttClient = null;
    private MqttConnectOptions connectOptions = new MqttConnectOptions();

    public ExternalMqttClient(String url, String clientId, String username, String password,
            TRUST_HOST_TYPE trustHostType) {
        try {
            mqttClient = new MqttClient(url, clientId);
            connectOptions.setConnectionTimeout(CONNECTION_TIME_OUT);
            connectOptions.setKeepAliveInterval(KEEP_ALIVE);
            if (username != null && password.length() > 0) {
                connectOptions.setUserName(username);
                connectOptions.setPassword(password.toCharArray());
            }
            mqttClient.connect(connectOptions);
        } catch (Exception ex) {
            _logger.error("Exception, ", ex);
        }
    }

    public Boolean isConnected() {
        if (mqttClient == null) {
            _logger.error("This client was not initialized correctly!");
            return null;
        } else {
            return mqttClient.isConnected();
        }
    }

    public void reConnect() {
        if (isConnected() != null && !isConnected()) {
            try {
                mqttClient.connect(connectOptions);
            } catch (MqttException ex) {
                _logger.error("Exception,", ex);
            }
        }
    }

    public void publish(String topic, String value) {
        if (isConnected() == null) {
            return;
        }
        if (isConnected()) {
            try {
                MqttMessage message = new MqttMessage(value.getBytes());
                message.setQos(QOS);
                mqttClient.publish(topic, message);
            } catch (MqttException ex) {
                _logger.error("Unable to send MQTT message, ", ex);
            }
        } else {
            _logger.warn("This client is not connected with broker!");
        }
    }

    public void disconnect() {
        if (isConnected() != null && isConnected()) {
            try {
                mqttClient.disconnect();
            } catch (MqttException ex) {
                _logger.error("exception, ", ex);
            }
        }
    }
}
