create table DUMMY
(
    id        bigint not null,
    BAR       integer,
    BAZ       VARCHAR(350),
    FOO       integer,
    naturalId varchar(255),
    primary key (id)
);

comment
on column DUMMY.BAZ is
        'I am just a nice comment';

create table DUMMY_AUD
(
    id        bigint  not null,
    REV       integer not null,
    REVTYPE   smallint,
    BAR       integer,
    BAZ       VARCHAR(350),
    FOO       integer,
    naturalId varchar(255),
    primary key (REV, id)
);

create table REVINFO
(
    REV      integer not null,
    REVTSTMP bigint,
    primary key (REV)
);
create index dummy_nid_idx on DUMMY (naturalId);

alter table if exists DUMMY
drop
constraint if exists UK_fdcr0bviqegjrjokhe3p792ys;

alter table if exists DUMMY
    add constraint UK_fdcr0bviqegjrjokhe3p792ys unique (naturalId);
create sequence DUMMY_ID_GEN start with 1 increment by 50;
create sequence REVINFO_SEQ start with 1 increment by 50;

alter table if exists DUMMY_AUD
    add constraint FKeo75xa84b3xf4oo689ra6kxs1
    foreign key (REV)
    references REVINFO;
