package com.concordsoft.navi.domain;

import com.voodoodyne.jackson.jsog.JSOGGenerator;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class Well extends OilObject{

    public Well() {
    }

    public Well(String name, String id) {
    super(name, id);
}

}
