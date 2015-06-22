--
-- DB increment script for version 2.0.4
--

--
-- tables: request and response
--
drop table if exists funnel cascade;

create table funnel (
    funnel_id int8 not null,
    msg_id int8 not null,
    funnel_value varchar(50) not null,
    primary key (funnel_id)
);

alter table funnel add constraint fk_funnel_message foreign key (msg_id) references message;

insert into funnel(funnel_id, msg_id, funnel_value)
select nextval('hibernate_sequence'), msg_id, funnel_value from message where funnel_value IS NOT NULL AND state <> 'OK'

alter table message drop column funnel_value



