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
package org.mycontroller.standalone.jobs;

import java.util.List;

import org.knowm.sundial.Job;
import org.knowm.sundial.exceptions.JobInterruptException;
import org.mycontroller.standalone.AppProperties;
import org.mycontroller.standalone.AppProperties.NETWORK_TYPE;
import org.mycontroller.standalone.AppProperties.STATE;
import org.mycontroller.standalone.McObjectManager;
import org.mycontroller.standalone.db.DaoUtils;
import org.mycontroller.standalone.db.tables.Node;
import org.mycontroller.standalone.utils.McUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeeva Kandasamy (jkandasa)
 * @since 0.0.2
 */
public class NodeAliveStatusJob extends Job {
    public static final String NAME = "node_alive_status_job";
    public static final String TRIGGER_NAME = "node_alive_status_trigger";
    private static final Logger _logger = LoggerFactory.getLogger(NodeAliveStatusJob.class);
    private static final long WAIT_TIME_TO_CHECK_ALIVE_STATUS = McUtils.SECOND * 30;
    public static final long DEFAULT_ALIVE_CHECK_INTERVAL = 30 * McUtils.MINUTE;
    private static boolean terminateAliveCheck = false;

    @Override
    public void doRun() throws JobInterruptException {
        _logger.debug("Executing 'node alive check' job");
        if (AppProperties.getInstance().getControllerSettings().getAliveCheckInterval() < McUtils.MINUTE) {
            //Nothing to do, just return from here
            return;
        }
        try {
            this.sendHearbeat();
            long referenceTimestamp = 0;
            while (referenceTimestamp <= WAIT_TIME_TO_CHECK_ALIVE_STATUS) {
                if (terminateAliveCheck) {
                    _logger.debug("Termination issued for NodeAliveStatusJob.");
                    return;
                }
                Thread.sleep(100);
                referenceTimestamp += 100;
            }
            this.checkHearbeat();
        } catch (Exception ex) {
            _logger.error("Exception, ", ex);
        }
    }

    private void sendHearbeat() {
        List<Node> nodes = DaoUtils.getNodeDao().getAll();
        for (Node node : nodes) {
            //If gateway not available, do not send
            if (McObjectManager.getGateway(node.getGatewayTable().getId()) == null
                    || McObjectManager.getGateway(node.getGatewayTable().getId()).getGateway().getState() != STATE.UP) {
                return;
            }
            //for now supports only for MySensors and MyController
            if (node.getGatewayTable().getEnabled()
                    && (node.getGatewayTable().getNetworkType() == NETWORK_TYPE.MY_SENSORS
                    || node.getGatewayTable().getNetworkType() == NETWORK_TYPE.MY_CONTROLLER)) {
                McObjectManager.getMcActionEngine().sendAliveStatusRequest(node);
            }
        }
    }

    private void checkHearbeat() {
        List<Node> nodes = DaoUtils.getNodeDao().getAll();
        long aliveCheckInterval = AppProperties.getInstance().getControllerSettings().getAliveCheckInterval();
        if (aliveCheckInterval < McUtils.MINUTE) {
            aliveCheckInterval = McUtils.MINUTE;
        }
        for (Node node : nodes) {
            if (node.getLastSeen() == null
                    || node.getLastSeen() <= (System.currentTimeMillis() - aliveCheckInterval)) {
                if (node.getGatewayTable().getEnabled()) {
                    node.setState(STATE.DOWN);
                } else {
                    node.setState(STATE.UNAVAILABLE);
                }
                DaoUtils.getNodeDao().update(node);
                _logger.debug("Node is in not reachable state, Node:[{}]", node);
            }
        }
    }

    public static synchronized void setTerminateAliveCheck(boolean terminateAliveCheck) {
        NodeAliveStatusJob.terminateAliveCheck = terminateAliveCheck;
    }
}
