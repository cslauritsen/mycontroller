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
package org.mycontroller.standalone.exceptions;

/**
 * @author Jeeva Kandasamy (jkandasa)
 * @since 0.0.3
 */

public class McException extends Exception {

    /**  */
    private static final long serialVersionUID = 7453421272595510071L;

    public McException() {

    }

    public McException(String message) {
        super(message);
    }

    public McException(Throwable throwable) {
        super(throwable);
    }

    public McException(String message, Throwable throwable) {
        super(message, throwable);
    }
}