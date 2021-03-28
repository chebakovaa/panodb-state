package com.concordsoft.navi.domain;

public class Company extends OilObject{

   public Company(String name, String id) {
      super(name, id);
   }

   public Company() {}

//   @Relationship(type="Принадлежит", direction = Relationship.INCOMING)
//   private List<Ngdu> children = null;
//
//   public List<Ngdu> getChildren() {
//      if (children == null) {
//         children = new ArrayList<>();
//      }
//      return children;
//   }


}

