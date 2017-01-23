/*
 * Copyright 2015-2017 Jeeva Kandasamy (jkandasa@gmail.com)
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
package org.mycontroller.standalone.republisher;

import java.nio.charset.Charset;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.mycontroller.standalone.AppProperties;
import org.mycontroller.standalone.message.McMessage;
import org.mycontroller.standalone.settings.MqttRepublisherSettings;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Jeeva Kandasamy (jkandasa)
 * @since 0.0.2
 */
@Slf4j
public class MqttRepublisher implements Runnable {

    private MqttClient mqttClient;
    private final MqttRepublisherSettings settings;
    private final String topicPrefix;

    public MqttRepublisher() {
        settings = AppProperties.getInstance().getMqttRepublisherSettings();
        topicPrefix = settings.getTopicPrefix();
        if (settings.getEnabled()) {
            try {
                String url = settings.getUrl();
                _logger.debug("MQTT url: '{}'", url);
                url = "tcp://192.168.1.2:1883";
                //                url = "tcp://stap3poldwv.sherwin.com:1883";
                _logger.debug("urls equal {}", "tcp://stap3poldwv.sherwin.com:1833".equals(url));
                mqttClient = new MqttClient(url, MqttClient.generateClientId());
                MqttConnectOptions opts = new MqttConnectOptions();
                if (settings.getUsername() != null && settings.getUsername().length() > 0) {
                    opts.setUserName(settings.getUsername());
                    opts.setPassword(settings.getPassword().toCharArray());
                    mqttClient.connect(opts);
                } else {
                    mqttClient.connect();
                }
                _logger.info("Connected to MQTT Broker successfully. {}", url);
            } catch (MqttException ex) {
                _logger.error("Unable to connect to MQTT Broker, Exception, ", ex);
            }
        }
    }

    public void run() {
        boolean go = true;
        try {
            _logger.info("MQTT Republishing starting...");
            Charset utf8 = Charset.forName("UTF-8");
            StringBuilder topic = new StringBuilder();

            while (go) {
                try {
                    topic.setLength(0);
                    McMessage mcMessage = MqttRepublisherService.QUEUE.take();
                    topic.append(topicPrefix)
                    .append('/')
                    .append(mcMessage.getNodeEui())
                    .append('/')
                    .append(mcMessage.getSensorId())
                    .append('/')
                    .append(mcMessage.getType())
                    .append('/')
                    .append(mcMessage.getSubType());
                    String payload = mcMessage.getPayload();
                    MqttMessage mqttMessage = new MqttMessage(payload == null || 0 == payload.length() ? new byte[0] : payload.getBytes(utf8));
                    mqttClient.publish(topic.toString(), mqttMessage);
                } catch (InterruptedException ex) {
                    go = false;
                    _logger.error("MQTT Republisher Interrupted", ex);
                }
                catch (MqttException ex) {
                    _logger.error("Failed to republish message, ", ex);
                }
            }
        }
        catch (Exception ex) {
            _logger.error("Unexpected error, ", ex);
        }
        finally {
            _logger.info("MQTT Republishing ending.");
            try {
                mqttClient.disconnect();
                _logger.info("MQTT Republishing disconnected from broker.");
            } catch (MqttException e) {
                _logger.error("Exception disconnecting from the MQTT broker, ", e);
            }
        }
    }
}