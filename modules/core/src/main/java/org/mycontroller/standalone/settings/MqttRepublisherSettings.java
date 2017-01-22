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
package org.mycontroller.standalone.settings;

import org.mycontroller.standalone.utils.McUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Jeeva Kandasamy (jkandasa)
 * @since 0.0.3
 */

@Builder
@ToString(includeFieldNames = true)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MqttRepublisherSettings {

    public static final String KEY_MQTT_REPUBLISHER = "mqttRepublisher";
    public static final String SKEY_ENABLED = "enabled";
    public static final String SKEY_URL = "url";
    public static final String SKEY_USERNAME = "username";
    public static final String SKEY_PASSWORD = "password";
    public static final String SKEY_TOPIC_PREFIX = "topicPrefix";

    private Boolean enabled;
    private String url;
    private String username;
    private String password;
    private String topicPrefix;

    public Boolean getEnabled() {
        // XXX
        return true; // || enabled == null ? false : enabled;
    }

    public static MqttRepublisherSettings get() {
        return MqttRepublisherSettings.builder()
                // XXX
                .enabled(true || McUtils.getBoolean(getValue(SKEY_ENABLED)))
                .url(getValue(SKEY_URL))
                .username(getValue(SKEY_USERNAME))
                .password(getValue(SKEY_PASSWORD))
                .topicPrefix(getValue(SKEY_TOPIC_PREFIX))
                .build();
    }

    public void save() {
        // XXX figure out later
        //        if (enabled != null) {
        //            updateValue(SKEY_ENABLED, enabled);
        //        }
        //        if (url != null) {
        //            updateValue(SKEY_URL, url.trim());
        //        }
        //        if (username != null) {
        //            updateValue(SKEY_USERNAME, username);
        //        }
        //        if (password != null) {
        //            updateValue(SKEY_PASSWORD, password);
        //        }
        //        if (topicPrefix != null) {
        //            updateValue(SKEY_TOPIC_PREFIX, topicPrefix);
        //        }
    }

    private static String getValue(String subKey) {
        // XXX Dumbing this down until i figure out how it really works
        //        return SettingsUtils.getValue(KEY_MQTT_REPUBLISHER, subKey);
        String x = System.getProperty(KEY_MQTT_REPUBLISHER + "." + subKey, "");
        System.out.printf("Getting value for %s.%s => %s%n", KEY_MQTT_REPUBLISHER, subKey, x);
        return x;
    }

    //    private void updateValue(String subKey, Object value) {
    //        SettingsUtils.updateValue(KEY_MQTT_REPUBLISHER, subKey, value);
    //    }
}
