package com.concordsoft.navi.controller;

import com.concordsoft.navi.Helper.QueryHelper;
import com.concordsoft.navi.model.NaviNode;
import com.google.gson.Gson;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.cypher.query.Pagination;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.awt.List;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.concordsoft.navi.Helper.QueryHelper.getNaviNodes1;
import static com.concordsoft.navi.Helper.QueryHelper.innerJoin;
import static org.apache.commons.lang3.math.NumberUtils.min;

@RestController
@RequestMapping()
public class NaviController {

    //  Configuration info for connecting to the Neo4J database
    static private final String SERVER_URI = "http://localhost:7474";
    static private final String SERVER_USERNAME = "neo4j";
    static private final String SERVER_PASSWORD = "password";
    private final Map<String, String> i18n = new HashMap<>();

    {
        i18n.put("Wells", "Скважины");
        i18n.put("Bushs", "Кусты");
        i18n.put("ProdObjects", "Промобъекты");
        i18n.put("GeoStratums", "Пласты");
        i18n.put("Boreholes", "Стволы");
    }


    Configuration configuration = new Configuration.Builder().uri(SERVER_URI).credentials(SERVER_USERNAME, SERVER_PASSWORD).build();
    private final SessionFactory sessionFactory = new SessionFactory(configuration, "com.concordsoft.navi.domain", "com.concordsoft.navi.relationships");

//    @GetMapping()
//    public Object getRoot() {
//        Session session = sessionFactory.openSession();
//        Collection<Company> companies = session.loadAll(Company.class, new Pagination(0, 10));
//        return new PageResponce(companies, companies.size());// RestPreconditions.checkFound(service.getRoot());
//    }
    @CrossOrigin(origins = "*")
    @GetMapping("/")
    public Object info() {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = null;
        try {
            model = reader.read(new FileReader("pom.xml"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        return String.format("%s-%s", model.getArtifactId(), model.getVersion());
    }

    @CrossOrigin(origins = "*")
    @Cacheable
    @GetMapping("/content")
    public Object getPath(@RequestParam("object") String obj, @RequestParam("start") int startIndex, @RequestParam("count") int countElement) {
        //  Create/load a map to hold the parameter
        String distinctMode = "label";
        Session session = sessionFactory.openSession();
        String cypher;
        Map<String, Object> params = new HashMap<String, Object>(5);
        NaviNode[] nodes = null;
        if(obj.length() == 0) {
            cypher = "MATCH (c:Company) WITH {mnem:labels(c), name:c.name, id: c.uid, pid: ''} as obj RETURN obj";
        }else{
            Gson g = new Gson();
            nodes = g.fromJson(obj, NaviNode[].class);
            NaviNode node = nodes[nodes.length - 1];
            params.put ("id", node.id);
            params.put ("names", new String[] {"Well","Bush","ProdObject","GeoStratum","Borehole"});
            params.put ("path", "label".equals(distinctMode) ? Arrays.stream(nodes).filter(v -> v.id.length() > 0).map(n -> Arrays.stream(n.mnem).filter(v -> !"OilObject".equals(v)).toArray()[0]).toArray(): new String[0]) ;
            params.put ("pathNames", "name".equals(distinctMode) ? Arrays.stream(nodes).filter(v -> v.id.length() > 0).map(n -> n.name).toArray(): new String[0]);
            params.put ("label", Arrays.stream(node.mnem).filter(v -> !"OilObject".equals(v)).toArray()[0]);
            if (node.id.isEmpty()) {
                params.put ("id", node.pid);
                cypher = "MATCH (:OilObject {uid:$id})<-[:BELONG_TO]-(o:" + params.get("label") + ") " +
                        "WHERE all(l in labels(o) where not (l in $path)) AND not (o.name in $pathNames)" +
                        "WITH {mnem:labels(o), name:o.name, id: o.uid, pid: $id} as obj " +
                        "RETURN obj";
            } else {
                cypher ="MATCH (root {uid:$id})<--(n) " +
                        "unwind [l in labels(n) where l in $names AND not (l in $path) | l ] as foo WITH " +
                        "{mnem:[foo], name:foo+'s', id: '', pid: $id} as obj " +
                        "RETURN obj as foo " +
                        "UNION MATCH (root {uid:$id})<--(n) WHERE all(l in labels(n) where not (l in $names) AND not (l in $path)) AND not (n.name in $pathNames) " +
                        "WITH {mnem:labels(n), name:n.name, id:n.uid, pid: $id} as obj " +
                        "RETURN obj as foo";
            }
        }
        Iterable<Object> res = session.query(Object.class, cypher, params);// RestPreconditions.checkFound(service.getRoot());
        ArrayList<NaviNode> target = getNaviNodes1(res);

        if(nodes != null) {
           for(int i=0; i < nodes.length-2; i++) {
               if(nodes[i].id.length() > 0) {
                   params.put ("id", nodes[i].id);
                   cypher = "MATCH (:OilObject {uid:$id})<-[:BELONG_TO]-(o:" + params.get("label") + ") " +
                           "WITH {mnem:labels(o), name:o.name, id: o.uid, pid: $id} as obj " +
                           "RETURN obj";
                   res = session.query(Object.class, cypher, params);// RestPreconditions.checkFound(service.getRoot());
                   ArrayList<NaviNode> parentNodes = getNaviNodes1(res);
                   if(parentNodes.size() > 0){
                       target = innerJoin(parentNodes, target);
                   }
               }
           }
        }

        target.forEach(v -> v.name = i18n.containsKey(v.name) ? i18n.get(v.name) : v.name);
        int maxElements = min(target.size(), startIndex + countElement);
        ArrayList<Object> list = new ArrayList(target.subList(startIndex, maxElements));
        session.clear();
        return new PageResponce(list, target.size());
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/search")
    public Object search(@RequestParam("parent") String obj, @RequestParam("text") String searchText) {
        //  Create/load a map to hold the parameter
        Session session = sessionFactory.openSession();
        Map<String, Object> params = new HashMap<>(3);

        Gson g = new Gson();
        NaviNode parent = g.fromJson(obj, NaviNode.class);

        String sText = "*" + searchText.replace(' ', '*') + "*";
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

    @CrossOrigin(origins = "*")
    @GetMapping("/subobjects")
    public Object getSubobjects(@RequestParam("names") String obj) {
        int countElement = 40;
        if(obj.length() > 0) {
            Session session = sessionFactory.openSession();
            String cypher;
            String[] names = obj.split(",");
            String objs = IntStream.range(0, names.length).mapToObj(i -> String.format("(o%d:OilObject {name:'%s'})", i, names[i])).collect(Collectors.joining("<-[:BELONG_TO]-"));
            cypher = "MATCH " + objs  +
                    String.format(" MATCH (o%s)<-[:BELONG_TO]-(n) ", names.length-1) +
                            "RETURN n.name";
            Iterable<Object> res = session.query(Object.class, cypher, Collections.emptyMap());// RestPreconditions.checkFound(service.getRoot());
            ArrayList<String> target = new ArrayList<>();
            for(Object item: res) {
                target.add((String)item);
            }
            int maxElements = min(target.size(), 0 + countElement);
            ArrayList<Object> list = new ArrayList(target.subList(0, maxElements));
            session.clear();
            return new PageResponce(list, target.size());
        }
        return null;
    }

}
