package com.example.demo.dummy;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.NaturalId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "DUMMY", indexes = {
        @Index(name = "dummy_nid_idx", columnList = "naturalId")
})
public class Dummy {

    @Id
    @SequenceGenerator(name = "DUMMY_ID_GEN")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DUMMY_ID_GEN")
    private Long id;

    @NaturalId
    private String naturalId;

    @Column(name = "FOO")
    private int foo;

    @Column(name = "BAR", columnDefinition = "INTEGER")
    @Comment("I am just a nice comment")
    private int bar;

    public Long getId() {
        return id;
    }

    public String getNaturalId() {
        return naturalId;
    }

    public int getFoo() {
        return foo;
    }

    public int getBar() {
        return bar;
    }
}