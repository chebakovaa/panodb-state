package com.concordsoft.navi.Helper;

import com.concordsoft.navi.model.NaviNode;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

public class QueryHelper {

    public static ArrayList<ArrayList<NaviNode>> getNaviNodes(Iterable<Object> res){
        ArrayList<ArrayList<NaviNode>> queryResult = new ArrayList<>();
        for(Object item: res) {
            ArrayList<NaviNode> queryLevel = new ArrayList<>();
            ArrayList<Object> elems = (ArrayList<Object>)item;
            Map<String, Object> mapElem;
            for (Object elem: elems) {
                mapElem = (LinkedHashMap<String, Object>)elem;
                mapToNaviNode(queryLevel, mapElem);
            }
            queryResult.add(queryLevel);
        }
        queryResult.sort(Comparator.comparingInt(a -> a.size()));
        return queryResult;
    }

    public static ArrayList<NaviNode> getNaviNodes1(Iterable<Object> res){
        ArrayList<NaviNode> queryResult = new ArrayList<>();
        for(Object item: res) {
            Map<String, Object> mapElem = (LinkedHashMap<String, Object>)item;
            mapToNaviNode(queryResult, mapElem);
        }
        return queryResult;
    }

    public static ArrayList<NaviNode> innerJoin(ArrayList<NaviNode> parent, ArrayList<NaviNode> child){
        ArrayList<NaviNode> res = new ArrayList<>();
        child.forEach(v -> {
            if(v.id.length()==0){
                res.add(v);
            }
        });
        Map<String, NaviNode> tmp = child.stream().filter(v -> v.id.length() > 0).collect(Collectors.toMap((v) -> v.id, (v) -> v));
        for(NaviNode nn:parent) {
            if(tmp.containsKey(nn.id)){
                res.add(nn);
            }
        }
        return res;
    }

    private static void mapToNaviNode(ArrayList<NaviNode> queryResult, Map<String, Object> mapElem) {
        NaviNode nn = new NaviNode();
        nn.name = (String)mapElem.get("name");
        nn.pid = (String)mapElem.get("pid");
        nn.mnem = ((ArrayList<String>)mapElem.get("mnem")).toArray(new String[0]);
        nn.id = (String)mapElem.get("id");
        queryResult.add(nn);
    }

}
