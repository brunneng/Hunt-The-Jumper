package com.greenteam.huntjumper.map;

import com.greenteam.huntjumper.Camera;
import com.greenteam.huntjumper.IVisibleObject;
import com.greenteam.huntjumper.utils.Point;
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
 * Created by IntelliJ IDEA. User: GreenTea Date: 14.01.12 Time: 22:21 To change this template use
 * File | Settings | File Templates.
 */
public class Map implements IVisibleObject
{
   private List<StaticBody> mapPolygons;

   public Map(List<StaticBody> mapPolygons)
   {
      this.mapPolygons = mapPolygons;
   }

   public List<Shape> toGraphicsShapes()
   {
      List<Shape> res = new ArrayList<Shape>();

      return res;
   }

   @Override
   public void draw(Graphics g, Camera camera)
   {
      g.setColor(ViewConstants.defaultMapColor);
      for (StaticBody b : mapPolygons)
      {
         Polygon p = (Polygon)b.getShape();

         ROVector2f[] vertices = p.getVertices();
         float[] viewVertices = new float[vertices.length * 2];
         for (int i = 0; i < vertices.length; ++i)
         {
            ROVector2f v = vertices[i];
            Point viewPoint = camera.toView(v);
            viewVertices[2*i] = viewPoint.getX();
            viewVertices[2*i + 1] = viewPoint.getY();
         }

         org.newdawn.slick.geom.Polygon viewPolygon = new org.newdawn.slick.geom.Polygon(
                 viewVertices);
         viewPolygon.setAllowDuplicatePoints(false);
         g.draw(viewPolygon);
      }
   }

   public List<StaticBody> getMapPolygons()
   {
      return mapPolygons;
   }
}
