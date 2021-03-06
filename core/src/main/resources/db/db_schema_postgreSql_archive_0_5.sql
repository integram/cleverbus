﻿﻿--
-- DB archive installation script v 0.5 for db version 0.4
-- Run with DB admin rights!
--
begin transaction;

drop schema if exists arch cascade;
create schema arch;
alter schema  arch owner to  cbssesb;

end transaction;

--
-- drop cannot be nested in transaction block, tablespace must be emty
--
drop tablespace if exists cbssesb_arch;

-- 
-- creates cbssesb_arch tablespace, the destination directory must exists
--
CREATE TABLESPACE cbssesb_arch
  OWNER cbssesb
  LOCATION 'c:\\Program Files\\PostgreSQL\\9.3\\data\\pg_tblspc\\cbssesb_arch';

begin transaction;

--
-- Setting default tablespace where the objects going to be created
-- It must be created first
SET default_tablespace = cbssesb_arch;
SET search_path = arch, pg_catalog;

--
-- table: archive_message
--
drop table if exists archive_message cascade;

create table archive_message (
  msg_id bigint not null,
  correlation_id character varying(100) not null,
  msg_timestamp timestamp without time zone not null,
  receive_timestamp timestamp without time zone not null,
  service character varying(30) not null,
  source_system character varying(15) not null,
  state character varying(15) not null,
  start_process_timestamp timestamp without time zone,
  object_id character varying(50),
  entity_type character varying(30),
  operation_name character varying(100) not null,
  payload text not null,
  envelope text,
  failed_desc text,
  failed_error_code character varying(5),
  failed_count integer not null,
  last_update_timestamp timestamp without time zone,
  custom_data character varying(20000),
  business_error character varying(20000),
  parent_msg_id bigint,
  funnel_value character varying(50),
  process_id character varying(100),
  parent_binding_type character varying(25),
  guaranteed_order boolean not null default false,
  exclude_failed_state boolean not null default false,
  funnel_component_id character varying(50)
);

ALTER TABLE archive_message OWNER TO cbssesb;

--
-- table: archive_external_call
--
drop table if exists archive_external_call cascade;

create table archive_external_call (
  call_id bigint NOT NULL,
  creation_timestamp timestamp without time zone not null,
  entity_id character varying(150) not null,
  failed_count integer NOT NULL,
  last_update_timestamp timestamp without time zone not null,
  msg_timestamp timestamp without time zone not null,
  msg_id bigint not null,
  operation_name character varying(100) not null,
  state character varying(20) not null
);

ALTER TABLE archive_external_call OWNER TO cbssesb;


--
-- tables: archive_request and archive_response
--
drop table if exists archive_request cascade;
drop table if exists archive_response cascade;

create table archive_request (
    req_id int8 not null,
    msg_id int8 null,
    res_join_id varchar(100) not null,
    uri varchar(400) not null,
    req_envelope text not null,
    req_timestamp timestamp not null
);

ALTER TABLE archive_request OWNER TO cbssesb;


create table archive_response (
    res_id int8 not null,
    req_id int8 null,
    res_envelope text null,
    failed_reason text null,
    res_timestamp timestamp null,
    failed boolean not null default false,
    msg_id int8 null
);

ALTER TABLE archive_response OWNER TO cbssesb;

--
-- function: archive_records(integer)
-- input: number of days after which the message is to be archived, minimum is 7
--
drop function if exists archive_records(integer);

create or replace function archive_records(integer)
  RETURNS text as
$BODY$
  declare
      KeepTime TIMESTAMP;
  begin
    IF $1 < 2
  then
     KeepTime := NOW() - INTERVAL '7 day';
  else
     KeepTime := NOW() - '1 day'::interval * $1;
  end if;

RAISE NOTICE 'Older records in the number of months: %', $1;
RAISE NOTICE 'Maximum limit for the records to be archived: %', KeepTime;

--
-- the place where you can insert a new table for archiving
--
insert into archive_request (
       req_id,msg_id,res_join_id,uri,req_envelope,req_timestamp
       )
       -- only request with reference on message
       select
            t.req_id,t.msg_id,t.res_join_id,t.uri,t.req_envelope,t.req_timestamp
            from request as t, message
            where message.msg_id = t.msg_id
            and message.state in ('OK', 'FAILED', 'CANCEL')
            and message.last_update_timestamp < KeepTime
       union
       -- all other without reference on message
       select
            req_id,msg_id,res_join_id,uri,req_envelope,req_timestamp
            from request
            where msg_id is null
            and req_timestamp < KeepTime;

insert into archive_response (
       res_id,req_id,res_envelope,failed_reason,res_timestamp,failed,msg_id
       )
       -- response with reference on message or withnout
       select
            res.res_id,res.req_id,res.res_envelope,res.failed_reason,res.res_timestamp,res.failed,res.msg_id
            from response as res
              left outer join request as req on res.req_id = req.req_id
                left outer join message ON req.msg_id = message.msg_id
            where message.state in ('OK', 'FAILED', 'CANCEL')
            and (message.last_update_timestamp < KeepTime) or (res.res_timestamp < KeepTime);

insert into archive_external_call (
       call_id,creation_timestamp,entity_id,failed_count,last_update_timestamp,
       msg_timestamp,msg_id,operation_name,state
       )
       select
            t.call_id,t.creation_timestamp,t.entity_id,t.failed_count,t.last_update_timestamp,
            t.msg_timestamp,t.msg_id,t.operation_name,t.state
            from external_call as t, message
            where message.msg_id = t.msg_id
            and message.state in ('OK', 'FAILED', 'CANCEL')
            and message.last_update_timestamp < KeepTime;

insert into archive_message (
       msg_id,correlation_id,msg_timestamp,receive_timestamp,
       service,source_system,state,start_process_timestamp,object_id,entity_type,
       operation_name,payload,envelope,failed_desc,failed_error_code,failed_count,
       last_update_timestamp,custom_data,business_error,parent_msg_id,funnel_value,
       process_id,parent_binding_type,guaranteed_order,exclude_failed_state,funnel_component_id
       )
       select
            msg_id,correlation_id,msg_timestamp,receive_timestamp,
            service,source_system,state,start_process_timestamp,object_id,entity_type,
            operation_name,payload,envelope,failed_desc,failed_error_code,failed_count,
            last_update_timestamp,custom_data,business_error,parent_msg_id,funnel_value,
            process_id,parent_binding_type,guaranteed_order,exclude_failed_state,funnel_component_id
            from message
            where state in ('OK', 'FAILED', 'CANCEL')
            and last_update_timestamp < KeepTime;

--
-- the place where you can insert the command for truncate new table for archiving
--

--
-- truncate request table
--
create TEMP table tmp_req as
  -- only request with reference on message
  select r1.*
    from request r1
      join message m on (m.msg_id = r1.msg_id)
      where m.state in ('OK', 'FAILED', 'CANCEL')
      and m.last_update_timestamp < KeepTime
  union
  -- all other without reference on message
  select r2.*
    from request r2
      where msg_id is null
      and req_timestamp < KeepTime;


--
-- truncate response table
--
create TEMP table tmp_resp as
  select r.*
    from response r
      left outer join request as req on r.req_id = req.req_id
        left outer join message ON req.msg_id = message.msg_id
      where message.state in ('OK', 'FAILED', 'CANCEL')
      and (message.last_update_timestamp < KeepTime) or (r.res_timestamp < KeepTime);


--
-- truncate external_call table
--
create TEMP table tmp_extcall as
  select e.*
    from external_call e
      join message m on (m.msg_id = e.msg_id)
      where (m.state not in ('OK', 'FAILED', 'CANCEL')
      or m.last_update_timestamp >= KeepTime);


--
-- truncate message table
--
create TEMP table tmp_msg AS
  select m.*
    from message m
    where (m.state not in ('OK', 'FAILED', 'CANCEL')
    or m.last_update_timestamp >= KeepTime);

execute 'TRUNCATE TABLE request CASCADE';
execute 'TRUNCATE TABLE response CASCADE';
execute 'TRUNCATE TABLE external_call CASCADE';
execute 'TRUNCATE TABLE message CASCADE';
insert into request select * from tmp_req;
insert into response select * from tmp_resp;
insert into message select * from tmp_msg;
insert into external_call select * from tmp_extcall;

return 'success';

end;
$BODY$
  language plpgsql VOLATILE
  COST 100;
ALTER FUNCTION arch.archive_records(integer) OWNER TO cbssesb;
ALTER FUNCTION arch.archive_records(integer) SET search_path=public,arch;
COMMENT ON FUNCTION arch.archive_records(integer) IS 'input: number of days after which the message is to be archived, minimum is 7.';

CREATE OR REPLACE FUNCTION arch.rebuildIndexes()
  RETURNS text AS
$BODY$
BEGIN
  reindex table message;
  reindex table external_call;
  reindex table request;
  reindex table response;
  return 'success';
END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

ALTER FUNCTION arch.rebuildIndexes() SET default_tablespace='cbssesb';

ALTER FUNCTION arch.rebuildIndexes() SET search_path=cbssesb, pg_catalog;

ALTER FUNCTION arch.rebuildIndexes()
  OWNER TO cbssesb;


end transaction;