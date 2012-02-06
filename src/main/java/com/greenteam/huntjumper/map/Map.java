package com.greenteam.huntjumper.map;

import com.greenteam.huntjumper.Camera;
import com.greenteam.huntjumper.IVisibleObject;
import com.greenteam.huntjumper.utils.*;
import net.phys2d.math.ROVector2f;
import net.phys2d.raw.StaticBody;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import java.util.ArrayList;
import java.util.List;

/**
 * User: GreenTea Date: 05.02.12 Time: 19:36
 */
public class Map implements IVisibleObject
{
   private List<StaticBody> allPolygons;
   private Image mapImage;
   private Vector2D translationVector;
   
   public Map(AvailabilityMap map)
   {
      translationVector = map.getTranslationVector();
      List<Polygon> polygons = map.splitOnPolygons();
      allPolygons = new ArrayList<StaticBody>();

      for (Polygon p : polygons)
      {
         ROVector2f[] physPoints = new ROVector2f[p.getSegments().size()];
         for (int i = 0; i < physPoints.length; ++i)
         {
            physPoints[i] = p.getSegments().get(i).getEnd1().toVector2f();
         }
         net.phys2d.raw.shapes.Polygon physP = new net.phys2d.raw.shapes.Polygon(physPoints);
         StaticBody body = new StaticBody(physP);
         body.setRestitution(1.0f);
         allPolygons.add(body);
      }

      try
      {
         mapImage = new Image(map.countX, map.countY);
         Graphics g = mapImage.getGraphics();

         g.setColor(ViewConstants.defaultGroundColor);
         g.fill(new Rectangle(0, 0, map.countX, map.countY));

         for (int x = 0; x < map.countX; ++x)
         {
            for (int y = 0; y < map.countY; ++y)
            {
               byte value = map.getValue(x, y);
               if (value != AvailabilityMap.FREE)
               {
                  g.setColor(ViewConstants.defaultMapColor);
                  g.draw(new Rectangle(x, y, 2, 2));
               }
//               else
//               {
//                  g.setColor(ViewConstants.defaultGroundColor);
//                  g.draw(new Rectangle(x, y, 2, 2));
//               }
            }
         }
         g.flush();
      }
      catch (SlickException e)
      {
         throw new RuntimeException(e);
      }
   }

   @Override
   public void draw(Graphics g)
   {
      Point p = new Point(-translationVector.getX(), -translationVector.getY());
      Point viewPoint = Camera.instance().toView(p);
      g.drawImage(mapImage, viewPoint.getX(), viewPoint.getY(), Color.white);

//      for (StaticBody b : allPolygons)
//      {
//         g.setColor(Color.black);
//         org.newdawn.slick.geom.Polygon viewPolygon = Utils.toViewPolygon(b);
//         g.draw(viewPolygon);
//      }
   }

   public List<StaticBody> getAllPolygons()
   {
      return allPolygons;
   }
}
