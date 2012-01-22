package com.greenteam.huntjumper.map;

import com.greenteam.huntjumper.utils.GameConstants;
import com.greenteam.huntjumper.utils.Point;
import com.greenteam.huntjumper.utils.Vector2D;
import net.phys2d.math.ROVector2f;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.shapes.Polygon;

import java.util.ArrayList;
import java.util.List;

/**
 * User: GreenTea Date: 14.01.12 Time: 21:26
 */
public class MapGenerator
{
   public static Map generateRingMap(float radius)
   {
      Vector2D v = new Vector2D(new Point(0, 0), new Point(radius, 0));
      int anglesCount = GameConstants.DEFAULT_MAP_RING_ANGLES_COUNT;
      float angleStep = 360.0f / anglesCount;

      List<ROVector2f> points = new ArrayList<ROVector2f>();
      points.add(v.toVector2f());
      for (int i = 0; i < anglesCount; ++i)
      {
         v.rotate(angleStep);
         points.add(v.toVector2f());
      }

      float biggerRadius = radius * 1.2f;
      v.setLength(biggerRadius);
      points.add(v.toVector2f());
      for (int i = 0; i < anglesCount; ++i)
      {
         v.rotate(-angleStep);
         points.add(v.toVector2f());
      }

      Polygon polygon = new Polygon(points.toArray(new ROVector2f[points.size()]));
      List<StaticBody> res = new ArrayList<StaticBody>();
      StaticBody body = new StaticBody(polygon);
      body.setRestitution(1.0f);
      res.add(body);
      return new Map(res);
   }
}
