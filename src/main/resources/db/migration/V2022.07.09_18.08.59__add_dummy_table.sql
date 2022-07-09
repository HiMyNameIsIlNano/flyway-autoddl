create table dummy
(
    id         int8 not null,
    foo        int4,
    natural_id varchar(255),
    primary key (id)
);
create index dummy_nid_idx on dummy (natural_id);

alter table if exists dummy
drop
constraint if exists UK_7filo6lm6r3sf74jbegcjsw13;

alter table if exists dummy
    add constraint UK_7filo6lm6r3sf74jbegcjsw13 unique (natural_id);
create sequence dummy_id_gen start 1 increment 50;
