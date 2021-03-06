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
package org.mycontroller.standalone.db.migration;

import java.sql.Connection;

import org.mycontroller.standalone.db.DataBaseUtils;
import org.mycontroller.standalone.jobs.RuleDefinitionsReEnableJob;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Jeeva Kandasamy (jkandasa)
 * @since 0.0.3
 */
@Slf4j
public class V1_03_01__2016_Aug_03 extends MigrationBase {

    @Override
    public void migrate(Connection connection) throws Exception {
        _logger.debug("Migration triggered.");

        //Load dao's
        loadDao();

        /** Migration comments
         *  Description:
         *  1. Added new column for RuleDefinitionTable, [reEnable, reEnableDelay]
         *  2. Add new job to re enable rules
         **/

        /** Migration #1
         * RuleDefinitation table changed
         * steps
         * 1. check if the column exists
         * 2. add new column
         * */
        //Execute only if column not available in database
        if (!sqlClient().hasColumn("rule_definition", "reEnable")) {
            sqlClient().addColumn("rule_definition", "reEnable", "TINYINT DEFAULT FALSE");
            sqlClient().addColumn("rule_definition", "reEnableDelay", "BIGINT");
            reloadDao();
        }

        /** Migration #2
         * Add a job to check one minute once.
         */
        //Executes every 10th and 40th second (30 seconds once)
        DataBaseUtils.createSystemJob("Rule definition re-enable job", "10,40 * * * * ? *", true,
                RuleDefinitionsReEnableJob.class);

        _logger.info("Migration completed successfully.");
    }

}
