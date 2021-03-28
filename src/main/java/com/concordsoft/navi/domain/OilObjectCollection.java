package com.concordsoft.navi.domain;


import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;


@NodeEntity
public class OilObjectCollection {

    @Id
    @GeneratedValue
    protected Long id;


    protected String name;


    protected Long uid;

    public OilObjectCollection() {}

    public OilObjectCollection(String name, Long id) {
        this.name = name;
        uid = id;
    }

    public Long getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return (uid != null ? uid.hashCode() : id.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OilObjectCollection obj = (OilObjectCollection) o;

        return uid != null ? uid.equals(obj.uid) : obj.uid == null;
    }

}
