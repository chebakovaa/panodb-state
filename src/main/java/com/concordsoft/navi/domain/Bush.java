package com.concordsoft.navi.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import org.neo4j.ogm.annotation.NodeEntity;

@JsonIdentityInfo(generator = JSOGGenerator.class)
@NodeEntity
public class Bush extends OilObject{

    public Bush() {}

    public Bush(String name, String id) {
        super(name, id);
    }

}
