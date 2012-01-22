package com.greenteam.huntjumper.map;

import com.greenteam.huntjumper.Camera;
import com.greenteam.huntjumper.IVisibleObject;
import com.greenteam.huntjumper.utils.Point;
import com.greenteam.huntjumper.utils.Utils;
import com.greenteam.huntjumper.utils.ViewConstants;
import net.phys2d.math.ROVector2f;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.shapes.Polygon;
import net.phys2d.raw.shapes.Shape;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import java.util.ArrayList;
import java.util.List;

/**
 * User: GreenTea Date: 14.01.12 Time: 22:21
 */
public class Map implements IVisibleObject
{
   private StaticBody outerPolygon;
   private StaticBody innerPolygon;

   private List<StaticBody> mapPolygons;
   private List<StaticBody> allPolygons;

   public Map(StaticBody outerPolygon, StaticBody innerPolygon, List<StaticBody> mapPolygons)
   {
      this.outerPolygon = outerPolygon;
      this.innerPolygon = innerPolygon;
      this.mapPolygons = mapPolygons;

      allPolygons = new ArrayList<StaticBody>();
      allPolygons.add(outerPolygon);
      allPolygons.add(innerPolygon);
      allPolygons.addAll(mapPolygons);
   }

   public List<Shape> toGraphicsShapes()
   {
      List<Shape> res = new ArrayList<Shape>();

      return res;
   }

   @Override
   public void draw(Graphics g)
   {
      g.setColor(ViewConstants.defaultMapColor);
      org.newdawn.slick.geom.Polygon op = Utils.toViewPolygon(this.outerPolygon);
      g.fill(op);

      g.setColor(ViewConstants.defaultGroundColor);
      org.newdawn.slick.geom.Polygon ip = Utils.toViewPolygon(this.innerPolygon);
      g.fill(ip);

      for (StaticBody b : mapPolygons)
      {
         g.setColor(ViewConstants.defaultMapColor);
         org.newdawn.slick.geom.Polygon viewPolygon = Utils.toViewPolygon(b);
         g.draw(viewPolygon);
      }
   }

   public List<StaticBody> getAllPolygons()
   {
      return allPolygons;
   }
}
