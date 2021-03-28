package com.concordsoft.navi.controller;

import com.concordsoft.navi.Helper.QueryHelper;
import com.concordsoft.navi.model.NaviNode;
import com.concordsoft.navi.model.SearchMessage;
import com.google.gson.Gson;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import java.util.*;

@Controller
public class SearchController {

    //  Configuration info for connecting to the Neo4J database
    static private final String SERVER_URI = "http://localhost:7474";
    static private final String SERVER_USERNAME = "neo4j";
    static private final String SERVER_PASSWORD = "password";


    Configuration configuration = new Configuration.Builder()
            .uri(SERVER_URI)
            .credentials(SERVER_USERNAME, SERVER_PASSWORD)
            .build();
    private final SessionFactory sessionFactory =
            new SessionFactory(configuration,
                    "com.concordsoft.navi.domain",
                    "com.concordsoft.navi.relationships");


    @MessageMapping("/navi")
    @SendTo("/topic/nodes")
    public Object send(SearchMessage message) throws Exception {
        //  Create/load a map to hold the parameter
        Session session = sessionFactory.openSession();
        Map<String, Object> params = new HashMap<String, Object>(3);

        Gson g = new Gson();
        NaviNode parent = g.fromJson(message.getObject(), NaviNode.class);

        String sText = "*" + message.getText().replace(' ', '*') + "*";
        params.put ("text", sText);
        if(parent.id.length() > 0) {
            params.put ("pid", parent.id); //
        } else {
            params.put ("pid", parent.pid); //
        }
        String cypher = "CALL db.index.fulltext.queryNodes('nameSearch', $text) YIELD node as obj " +
                "MATCH p=((:OilObject {uid: $pid})<-[:BELONG_TO*1..3]-(obj)) " +
                "WHERE ALL(n1 IN nodes(p) WHERE size([n2 IN nodes(p) WHERE labels(n1) = labels(n2)]) = 1) " +
                "RETURN [v IN nodes(p)|v{id:v.uid, mnem:labels(v), .name, pid: ''}] " +
                "LIMIT 40";
        Iterable<Object> res = session.query(Object.class, cypher, params);// RestPreconditions.checkFound(service.getRoot());
        session.clear();
        return QueryHelper.getNaviNodes(res);
    }

}
