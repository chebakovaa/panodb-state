package com.concordsoft.navi.relationships;

import com.concordsoft.navi.domain.OilObject;
import com.concordsoft.navi.domain.OilObjectCollection;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import org.neo4j.ogm.annotation.*;

@RelationshipEntity(type = "CONTAINS")
public class Contains {
    @Id
    @GeneratedValue
    private Long id;


    @StartNode
    private OilObject item;


    @EndNode
    private OilObjectCollection collection;

    public Contains() {
    }

    public Contains(OilObjectCollection collection, OilObject item) {
        this.collection = collection;
        this.item = item;
    }

}
