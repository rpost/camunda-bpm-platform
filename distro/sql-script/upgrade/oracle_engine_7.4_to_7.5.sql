-- tenant id --

ALTER TABLE ACT_RE_DEPLOYMENT
  ADD TENANT_ID_ NVARCHAR2(64);

create index ACT_IDX_DEPLOYMENT_TENANT_ID on ACT_RE_DEPLOYMENT(TENANT_ID_);

ALTER TABLE ACT_RE_PROCDEF
  ADD TENANT_ID_ NVARCHAR2(64);

ALTER TABLE ACT_RE_PROCDEF
  DROP CONSTRAINT ACT_UNIQ_PROCDEF;

create index ACT_IDX_PROCDEF_TENANT_ID ON ACT_RE_PROCDEF(TENANT_ID_);

ALTER TABLE ACT_RU_EXECUTION
  ADD TENANT_ID_ NVARCHAR2(64);

create index ACT_IDX_EXEC_TENANT_ID on ACT_RU_EXECUTION(TENANT_ID_);

ALTER TABLE ACT_RU_VARIABLE
  ADD TENANT_ID_ NVARCHAR2(64);

create index ACT_IDX_VARIABLE_TENANT_ID on ACT_RU_VARIABLE(TENANT_ID_);

ALTER TABLE ACT_RU_EVENT_SUBSCR
  ADD TENANT_ID_ NVARCHAR2(64);

create index ACT_IDX_EVENT_SUBSCR_TENANT_ID on ACT_RU_EVENT_SUBSCR(TENANT_ID_);  

ALTER TABLE ACT_RU_JOB
  ADD TENANT_ID_ NVARCHAR2(64);

create index ACT_IDX_JOB_TENANT_ID on ACT_RU_JOB(TENANT_ID_); 

ALTER TABLE ACT_RU_JOBDEF
  ADD TENANT_ID_ NVARCHAR2(64);

create index ACT_IDX_JOBDEF_TENANT_ID on ACT_RU_JOBDEF(TENANT_ID_); 

-- user on historic decision instance --

ALTER TABLE ACT_HI_DECINST
  ADD USER_ID_ NVARCHAR2(255);
