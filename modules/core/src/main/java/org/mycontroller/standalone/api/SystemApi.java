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
package org.mycontroller.standalone.api;

import java.util.HashMap;
import java.util.List;

import org.mycontroller.standalone.api.jaxrs.json.McAbout;
import org.mycontroller.standalone.api.jaxrs.json.McGuiSettings;
import org.mycontroller.standalone.api.jaxrs.utils.StatusJVM;
import org.mycontroller.standalone.api.jaxrs.utils.StatusOS;
import org.mycontroller.standalone.scripts.McScriptEngineUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Jeeva Kandasamy (jkandasa)
 * @since 0.0.3
 */
@Slf4j
public class SystemApi {
    public StatusJVM getJVM() {
        return new StatusJVM();
    }

    public StatusOS getOS() {
        return new StatusOS();
    }

    public List<HashMap<String, Object>> getScriptEngines() {
        return McScriptEngineUtils.getScriptEnginesDetail();
    }

    public McAbout getAbout() {
        return new McAbout();
    }

    public McGuiSettings getGuiSettings() {
        return new McGuiSettings();
    }

    public void runGarbageCollection() {
        System.gc();
        _logger.info("Manually executed JVM Garbage Collection..");
    }
}
