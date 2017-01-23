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

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.mycontroller.standalone.AppProperties;
import org.mycontroller.standalone.message.McMessage;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Jeeva Kandasamy (jkandasa)
 * @since 0.0.2
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MqttRepublisherService {
    private static boolean isRunning = false;
    public static final BlockingQueue<McMessage> QUEUE = new ArrayBlockingQueue<>(100);
    public static Thread service;

    public static synchronized void start() {
        _logger.info("MQTT Republisher start request received");
        if (!AppProperties.getInstance().getMqttRepublisherSettings().getEnabled()) {
            _logger.debug("MQTT Republisher is not enabled. Not starting...");
            return;
        }
        if (isRunning) {
            _logger.info("MQTT Republisher already running, nothing to do...");
            return;
        }
        isRunning = true;
        service = new Thread(new MqttRepublisher(), "MQTTRepublisher-Thread");
        service.setDaemon(true);
        service.start();
        _logger.info("MQTT Republisher started successfully.");
    }

    public static synchronized void stop() {
        if (!isRunning) {
            _logger.debug("MQTT Republisher is not running, nothing to do...");
            return;
        }
        service.interrupt();
        isRunning = false;
        _logger.info("MQTT Republisher has been stopped successfully");
    }

    public static synchronized void restart() {
        _logger.info("MQTT Republisher restart triggered...");
        stop();
        start();
    }

    public static void publish(McMessage mcMessage) {
        if (AppProperties.getInstance().getMqttRepublisherSettings().getEnabled()) {
            if (isRunning) {
                boolean success = MqttRepublisherService.QUEUE.offer(mcMessage);
                if (!success) {
                    _logger.error("MQTT republish failed: queue full");
                    stop();
                }
            }
        }
    }
}
