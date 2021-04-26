/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.engine.test.api.runtime.migration.batch;

import org.camunda.bpm.engine.BadUserRequestException;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.batch.Batch;
import org.camunda.bpm.engine.migration.MigrationPlan;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.VariableInstance;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.RequiredHistoryLevel;
import org.camunda.bpm.engine.test.api.runtime.migration.MigrationTestRule;
import org.camunda.bpm.engine.test.api.runtime.migration.models.ProcessModels;
import org.camunda.bpm.engine.test.util.ProcessEngineTestRule;
import org.camunda.bpm.engine.test.util.ProvidedProcessEngineRule;
import org.camunda.bpm.engine.variable.Variables;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

public class BatchSetVariablesTestSetVariables {

  protected ProcessEngineRule engineRule = new ProvidedProcessEngineRule();
  protected MigrationTestRule migrationRule = new MigrationTestRule(engineRule);
  protected BatchMigrationHelper helper = new BatchMigrationHelper(engineRule, migrationRule);
  protected ProcessEngineTestRule testRule = new ProcessEngineTestRule(engineRule);

  @Rule
  public RuleChain ruleChain = RuleChain.outerRule(engineRule).around(migrationRule).around(testRule);

  @After
  public void removeBatches() {
    helper.removeAllRunningAndHistoricBatches();
  }

  @Test
  public void shouldCreateBatchVariable() {
    // given
    ProcessDefinition sourceProcessDefinition = migrationRule.deployAndGetDefinition(ProcessModels.ONE_TASK_PROCESS);
    ProcessDefinition targetProcessDefinition = migrationRule.deployAndGetDefinition(ProcessModels.ONE_TASK_PROCESS);

    Batch batch = helper.migrateProcessInstancesAsync(5, sourceProcessDefinition,
        targetProcessDefinition, Variables.putValue("foo", "bar"));

    // when
    helper.completeSeedJobs(batch);

    // then
    VariableInstance batchVariable = engineRule.getRuntimeService()
        .createVariableInstanceQuery()
        .batchIdIn(batch.getId())
        .singleResult();

    assertThat(batchVariable)
        .extracting("name", "value")
        .containsExactly("foo", "bar");
  }

  @Test
  public void shouldCreateBatchVariables() {
    // given
    ProcessDefinition sourceProcessDefinition = migrationRule.deployAndGetDefinition(ProcessModels.ONE_TASK_PROCESS);
    ProcessDefinition targetProcessDefinition = migrationRule.deployAndGetDefinition(ProcessModels.ONE_TASK_PROCESS);

    Batch batch = helper.migrateProcessInstancesAsync(5, sourceProcessDefinition,
        targetProcessDefinition, Variables.putValue("foo", "bar").putValue("bar", "foo"));

    // when
    helper.completeSeedJobs(batch);

    // then
    List<VariableInstance> batchVariables = engineRule.getRuntimeService()
        .createVariableInstanceQuery()
        .batchIdIn(batch.getId())
        .list();

    assertThat(batchVariables)
        .extracting("name", "value")
        .containsExactlyInAnyOrder(
            tuple("foo", "bar"),
            tuple("bar", "foo")
        );
  }

  @Test
  public void shouldRemoveBatchVariable() {
  }

  @Test
  public void shouldSetVariable() {
  }

  @Test
  public void shouldThrowException_TransientVariable() {
    // given
    ProcessDefinition sourceProcessDefinition = migrationRule.deployAndGetDefinition(ProcessModels.ONE_TASK_PROCESS);
    ProcessDefinition targetProcessDefinition = migrationRule.deployAndGetDefinition(ProcessModels.ONE_TASK_PROCESS);

    String processInstanceId = engineRule.getRuntimeService()
        .startProcessInstanceById(sourceProcessDefinition.getId()).getId();

    MigrationPlan migrationPlan = engineRule.getRuntimeService()
        .createMigrationPlan(sourceProcessDefinition.getId(), targetProcessDefinition.getId())
        .mapEqualActivities()
        .setVariables(Variables.putValue("foo", Variables.stringValue("bar", true))).build();

    // when/then
    assertThatThrownBy(() -> engineRule.getRuntimeService().newMigration(migrationPlan).processInstanceIds(processInstanceId).executeAsync())
        .isInstanceOf(BadUserRequestException.class)
        .hasMessageContaining("ENGINE-13044 Setting transient variable 'foo' " +
            "asynchronously is currently not supported.");
  }

  @Test
  @RequiredHistoryLevel(ProcessEngineConfiguration.HISTORY_FULL)
  public void shouldLogOperation() {
  }

}
