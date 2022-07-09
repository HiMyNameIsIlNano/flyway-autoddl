
    create table DUMMY (
       id int8 not null,
        BAR int4,
        BAZ VARCHAR(350),
        FOO int4,
        naturalId varchar(255),
        primary key (id)
    );

    comment on column DUMMY.BAZ is
        'I am just a nice comment';

    create table DUMMY_AUD (
       id int8 not null,
        REV int4 not null,
        REVTYPE int2,
        BAR int4,
        BAZ VARCHAR(350),
        FOO int4,
        naturalId varchar(255),
        primary key (id, REV)
    );

    create table REVINFO (
       REV int4 not null,
        REVTSTMP int8,
        primary key (REV)
    );
create index dummy_nid_idx on DUMMY (naturalId);

    alter table if exists DUMMY 
       drop constraint if exists UK_fdcr0bviqegjrjokhe3p792ys;

    alter table if exists DUMMY 
       add constraint UK_fdcr0bviqegjrjokhe3p792ys unique (naturalId);
create sequence DUMMY_ID_GEN start 1 increment 50;
create sequence hibernate_sequence start 1 increment 1;

    alter table if exists DUMMY_AUD 
       add constraint FKeo75xa84b3xf4oo689ra6kxs1 
       foreign key (REV) 
       references REVINFO;
