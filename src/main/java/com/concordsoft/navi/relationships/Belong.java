package com.concordsoft.navi.relationships;

import com.concordsoft.navi.domain.OilObject;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import org.neo4j.ogm.annotation.*;

@RelationshipEntity(type = "BELONG_TO")
public class Belong {
    @Id
    @GeneratedValue
    private Long id;


    @StartNode
    private OilObject child;


    @EndNode
    private OilObject parent;

    public Belong() {
    }

    public Belong(OilObject parent, OilObject child) {
        this.parent = parent;
        this.child = child;
    }

}
