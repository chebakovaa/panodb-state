package com.concordsoft.navi.domain;


import com.voodoodyne.jackson.jsog.JSOGGenerator;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@NodeEntity
public class Ngdu extends OilObject{

    public Ngdu() {}

    public Ngdu(String name, String id) {
        super(name, id);
    }

//    @Relationship(type="BELONG_TO", direction = Relationship.INCOMING)
//    private List<Oilfield> children = null;
//
//    public Collection<Oilfield> getChildren() {
//        if (children == null) {
//            children = new ArrayList<>();
//        }
//
//        return children;
//    }

}
