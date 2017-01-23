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
package org.mycontroller.standalone.provider.philipshue;

/**
 * @author Fraid(https://github.com/Fraid)
 */
public class PhilipsHueUtils {
    public static final String NODE_EUI = "philips-hue-node";

    public static Integer toPercent(int brightness) {
        return (brightness * 100) / 255;
    }

    public static Double toBrightness(double percentageValue) {
        //Verify user entry (0-100%)
        if (percentageValue > 100)
            percentageValue = 100;
        else if (percentageValue < 0)
            percentageValue = 0;
        return ((percentageValue * 255) / 100);
    }
}
