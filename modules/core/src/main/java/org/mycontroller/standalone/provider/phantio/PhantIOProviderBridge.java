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
package org.mycontroller.standalone.provider.phantio;

import org.mycontroller.standalone.AppProperties.NETWORK_TYPE;
import org.mycontroller.standalone.db.tables.Node;
import org.mycontroller.standalone.db.tables.Sensor;
import org.mycontroller.standalone.message.IProviderBridge;
import org.mycontroller.standalone.message.McMessage;
import org.mycontroller.standalone.message.McMessageUtils;
import org.mycontroller.standalone.message.RawMessage;
import org.mycontroller.standalone.message.RawMessageException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Jeeva Kandasamy (jkandasa)
 * @since 0.0.3
 */
@Slf4j
public class PhantIOProviderBridge implements IProviderBridge {

    @Override
    public void executeMcMessage(McMessage mcMessage) {
        if (mcMessage.getNetworkType() != NETWORK_TYPE.PHANT_IO) {
            _logger.error("This is not '{}' message! McMessage:{}", NETWORK_TYPE.PHANT_IO.getText(), mcMessage);
        }
        try {
            _logger.debug("McMessage about to send to gateway: [{}]", mcMessage);
            McMessageUtils.sendToGateway(new PhantIORawMessage(mcMessage).getRawMessage());
        } catch (RawMessageException ex) {
            _logger.error("Unable to process this McMessage:{}", mcMessage, ex);
        }
    }

    @Override
    public void executeRawMessage(RawMessage rawMessage) {
        if (rawMessage.getNetworkType() != NETWORK_TYPE.PHANT_IO) {
            _logger.error("This is not '{}' message! RawMessage:{}", NETWORK_TYPE.PHANT_IO.getText(), rawMessage);
        }
        try {
            _logger.debug("Received raw message: [{}]", rawMessage);
            McMessage mcMessage = new PhantIORawMessage(rawMessage).getMcMessage();
            McMessageUtils.sendToMcMessageEngine(mcMessage);
            if (rawMessage.isTxMessage()) {
                executeMcMessage(mcMessage);
            }
        } catch (RawMessageException ex) {
            _logger.error("Unable to process this rawMessage:{}", rawMessage, ex);
        }
    }

    @Override
    public boolean validateSensorId(Sensor sensor) {
        if (sensor.getSensorId().contains(" ")) {
            throw new RuntimeException("Sensor Id should not contain any space");
        }
        return true;
    }

    @Override
    public boolean validateNodeId(Node node) {
        if (node.getEui().contains(" ")) {
            throw new RuntimeException("Node EUI should not contain any space");
        }
        return true;
    }

    @Override
    public RawMessage getRawMessage(McMessage mcMessage) throws RawMessageException {
        return new PhantIORawMessage(mcMessage).getRawMessage();
    }
}
