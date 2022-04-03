package com.example.demo.dummy;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.NaturalId;
import org.hibernate.envers.Audited;

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
        @Index(name = "dummy_nid_idx", columnList = "naturalId"),
        @Index(name = "dummy_nitem3_idx", columnList = "item3")
})
@Audited
public class Dummy {

    @Id
    @SequenceGenerator(name = "DUMMY_ID_GEN")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DUMMY_ID_GEN")
    private Long id;

    @NaturalId
    private String naturalId;

    @Column(name = "SERIAL_ID")
    private int number;

    @Column(name = "ITEM")
    private String item;

    @Column(name = "ITEM2")
    private String item2;

    @Lob
    @Column(name = "ITEM3", columnDefinition = "VARCHAR2(350)")
    @Comment("I am just a nice comment")
    private String item3;

    public Long getId() {
        return id;
    }

    public String getNaturalId() {
        return naturalId;
    }

    public int getNumber() {
        return number;
    }

    public String getItem() {
        return item;
    }

    public String getItem2() {
        return item2;
    }

    public String getItem3() {
        return item3;
    }
}