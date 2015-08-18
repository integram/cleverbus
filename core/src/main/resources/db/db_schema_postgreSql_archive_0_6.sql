CREATE SCHEMA cleverbus_archive
  AUTHORIZATION cbssesb;



CREATE TABLE cleverbus_archive.archive_external_call
(
  call_id bigint NOT NULL,
  creation_timestamp timestamp without time zone NOT NULL,
  entity_id character varying(150) NOT NULL,
  failed_count integer NOT NULL,
  last_update_timestamp timestamp without time zone NOT NULL,
  msg_timestamp timestamp without time zone NOT NULL,
  msg_id bigint NOT NULL,
  operation_name character varying(100) NOT NULL,
  state character varying(20) NOT NULL
)
WITH (
  OIDS=FALSE
);
ALTER TABLE cleverbus_archive.archive_external_call
  OWNER TO cbssesb;


CREATE TABLE cleverbus_archive.archive_message
(
  msg_id bigint NOT NULL,
  correlation_id character varying(100) NOT NULL,
  msg_timestamp timestamp without time zone NOT NULL,
  receive_timestamp timestamp without time zone NOT NULL,
  service character varying(30) NOT NULL,
  source_system character varying(15) NOT NULL,
  state character varying(15) NOT NULL,
  start_process_timestamp timestamp without time zone,
  object_id character varying(50),
  entity_type character varying(30),
  operation_name character varying(100) NOT NULL,
  payload text NOT NULL,
  envelope text,
  failed_desc text,
  failed_error_code character varying(5),
  failed_count integer NOT NULL,
  last_update_timestamp timestamp without time zone,
  custom_data character varying(20000),
  business_error character varying(20000),
  parent_msg_id bigint,
  funnel_value character varying(50),
  process_id character varying(100),
  parent_binding_type character varying(25),
  guaranteed_order boolean NOT NULL DEFAULT false,
  exclude_failed_state boolean NOT NULL DEFAULT false,
  funnel_component_id character varying(50)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE cleverbus_archive.archive_message
  OWNER TO cbssesb;


CREATE TABLE cleverbus_archive.archive_request
(
  req_id bigint NOT NULL,
  msg_id bigint,
  res_join_id character varying(100) NOT NULL,
  uri character varying(400) NOT NULL,
  req_envelope text NOT NULL,
  req_timestamp timestamp without time zone NOT NULL
)
WITH (
  OIDS=FALSE
);
ALTER TABLE cleverbus_archive.archive_request
  OWNER TO cbssesb;


CREATE TABLE cleverbus_archive.archive_response
(
  res_id bigint NOT NULL,
  req_id bigint,
  res_envelope text,
  failed_reason text,
  res_timestamp timestamp without time zone,
  failed boolean NOT NULL DEFAULT false,
  msg_id bigint,
  CONSTRAINT response_pkey PRIMARY KEY (res_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE cleverbus_archive.archive_response
  OWNER TO cbssesb;
  


CREATE INDEX response__request_id_idx
   ON response
   USING btree
   (req_id);

   CREATE INDEX request__message_id_idx
   ON request
   USING btree
   (msg_id);

CREATE INDEX external_call__message_id_idx
   ON external_call
   USING btree
   (msg_id);







CREATE OR REPLACE FUNCTION archive_records(
     timestamp without time zone,
     integer)
   RETURNS boolean AS
$BODY$
   declare
       KeepTime ALIAS FOR $1;
       MaxAffectedRows ALIAS FOR $2;
       IsMoreToDelete boolean;
       DeletedCount integer;
   begin

RAISE NOTICE 'Older records in the number of months: %', KeepTime;
RAISE NOTICE 'Maximum limit for the records to be archived: %', MaxAffectedRows;

RAISE NOTICE '---- begin %', timeofday()::timestamp;

DROP TABLE IF EXISTS deleted_message;
create temp table deleted_message on commit drop as (select
         msg_id,correlation_id,msg_timestamp,receive_timestamp,
service,source_system,state,start_process_timestamp,object_id,entity_type,
operation_name,payload,envelope,failed_desc,failed_error_code,failed_count,
last_update_timestamp,custom_data,business_error,parent_msg_id,funnel_value,
process_id,parent_binding_type,guaranteed_order,exclude_failed_state,funnel_component_id
        from message where state in ('OK', 'FAILED', 'CANCEL')
             and message.last_update_timestamp < KeepTime
         limit MaxAffectedRows);
ALTER TABLE deleted_message ADD PRIMARY KEY (msg_id);
RAISE NOTICE '---- deleted_message tmp %', timeofday()::timestamp;
select count(*) into DeletedCount from deleted_message;
RAISE NOTICE '---- deleted_message tmp %', DeletedCount;


DROP TABLE IF EXISTS deleted_request;
create temp table deleted_request on commit drop as
        -- only request with reference on message
        (select
t.req_id,t.msg_id,t.res_join_id,t.uri,t.req_envelope,t.req_timestamp
             from request t, deleted_message
             where deleted_message.msg_id = t.msg_id)
        union
        -- all other without reference on message
        (select
             req_id,msg_id,res_join_id,uri,req_envelope,req_timestamp
             from request
             where msg_id is null
             and req_timestamp < KeepTime
             limit MaxAffectedRows);
ALTER TABLE deleted_request ADD PRIMARY KEY (req_id);
RAISE NOTICE '---- deleted_request tmp %', timeofday()::timestamp;
select count(*) into DeletedCount from deleted_request;
RAISE NOTICE '---- deleted_request tmp %', DeletedCount;

DROP TABLE IF EXISTS deleted_response;
create temp table deleted_response on commit drop as
        -- response with reference on request
        (select
res.res_id,res.req_id,res.res_envelope,res.failed_reason,res.res_timestamp,res.failed,res.msg_id
             from response as res
             inner join deleted_request as req on res.req_id = req.req_id)
         union
        -- response without reference on request
        (select
res.res_id,res.req_id,res.res_envelope,res.failed_reason,res.res_timestamp,res.failed,res.msg_id
             from response as res
             where res.req_id is null
             and res.res_timestamp < KeepTime
             limit MaxAffectedRows);
ALTER TABLE deleted_response ADD PRIMARY KEY (res_id);
RAISE NOTICE '---- deleted_response tmp %', timeofday()::timestamp;
select count(*) into DeletedCount from deleted_response;
RAISE NOTICE '---- deleted_response tmp %', DeletedCount;


DROP TABLE IF EXISTS deleted_external_call;
create temp table deleted_external_call as
        select
t.call_id,t.creation_timestamp,t.entity_id,t.failed_count,t.last_update_timestamp,
             t.msg_timestamp,t.msg_id,t.operation_name,t.state
             from external_call as t, deleted_message
             where deleted_message.msg_id = t.msg_id;
ALTER TABLE deleted_external_call ADD PRIMARY KEY (call_id);
RAISE NOTICE '---- deleted_external_call tmp %', timeofday()::timestamp;
select count(*) into DeletedCount from deleted_external_call;
RAISE NOTICE '---- deleted_external_call tmp %', DeletedCount;


insert into cleverbus_archive.archive_response (
res_id,req_id,res_envelope,failed_reason,res_timestamp,failed,msg_id
        )
        select res_id,req_id,res_envelope,failed_reason,res_timestamp,failed,msg_id
        from deleted_response;
RAISE NOTICE '---- archive_response insert %', timeofday()::timestamp;

delete from response as t
     USING deleted_response as res
             where
             t.res_id = res.res_id;

GET DIAGNOSTICS DeletedCount = ROW_COUNT;
RAISE NOTICE 'Calling delete from response - %', DeletedCount;
IsMoreToDelete = IsMoreToDelete or (DeletedCount = MaxAffectedRows);
RAISE NOTICE '---- %', timeofday()::timestamp;


insert into cleverbus_archive.archive_request (
        req_id,msg_id,res_join_id,uri,req_envelope,req_timestamp
        ) select * from deleted_request;
RAISE NOTICE '---- archive_request insert %', timeofday()::timestamp;

delete from request as t
             USING deleted_request as req
             where
             t.req_id = req.req_id;

GET DIAGNOSTICS DeletedCount = ROW_COUNT;
RAISE NOTICE 'Calling delete from request - %', DeletedCount;
IsMoreToDelete = DeletedCount = MaxAffectedRows;
RAISE NOTICE '---- %', timeofday()::timestamp;


insert into cleverbus_archive.archive_external_call (
call_id,creation_timestamp,entity_id,failed_count,last_update_timestamp,
        msg_timestamp,msg_id,operation_name,state
        )
        select * from deleted_external_call;
RAISE NOTICE '---- archive_external_call insert %', timeofday()::timestamp;

delete from external_call as t
     USING deleted_external_call as call
             where
             t.call_id = call.call_id;

GET DIAGNOSTICS DeletedCount = ROW_COUNT;
RAISE NOTICE 'Calling delete from external_call - %', DeletedCount;
IsMoreToDelete = IsMoreToDelete or (DeletedCount = MaxAffectedRows);
RAISE NOTICE '---- %', timeofday()::timestamp;


insert into cleverbus_archive.archive_message (
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
             from deleted_message;
RAISE NOTICE '---- archive_message insert %', timeofday()::timestamp;

delete from message as t
     USING deleted_message as msg
         where t.msg_id = msg.msg_id;

GET DIAGNOSTICS DeletedCount = ROW_COUNT;
RAISE NOTICE 'Calling delete from message - %', DeletedCount;
IsMoreToDelete = IsMoreToDelete or (DeletedCount = MaxAffectedRows);
RAISE NOTICE '---- %', timeofday()::timestamp;


return IsMoreToDelete;

end;
$BODY$
   LANGUAGE plpgsql VOLATILE
   COST 100;
ALTER FUNCTION archive_records(timestamp without time zone, integer)
   OWNER TO cbssesb;

CREATE OR REPLACE FUNCTION rebuildIndexes()
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

ALTER FUNCTION rebuildIndexes()
  OWNER TO cbssesb;