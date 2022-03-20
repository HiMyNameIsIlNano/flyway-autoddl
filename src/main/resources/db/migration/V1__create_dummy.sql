create table dummy
(
    id         bigint not null,
    natural_id varchar(255),
    primary key (id)
);
create index dummy_nid_idx on dummy (natural_id);

alter table dummy
drop
constraint if exists UK_7filo6lm6r3sf74jbegcjsw13;

alter table dummy
    add constraint UK_7filo6lm6r3sf74jbegcjsw13 unique (natural_id);
create sequence dummy_id_gen start with 1 increment by 50;
