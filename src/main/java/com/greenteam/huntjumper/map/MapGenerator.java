package com.greenteam.huntjumper.map;

import com.greenteam.huntjumper.utils.GameConstants;
import com.greenteam.huntjumper.utils.Point;
import com.greenteam.huntjumper.utils.Vector2D;
import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.shapes.Polygon;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: GreenTea Date: 14.01.12 Time: 21:26 To change this template use
 * File | Settings | File Templates.
 */
public class MapGenerator
{
   public static Map generateRingMap(float radius)
   {
      Vector2D v = new Vector2D(new Point(0, 0), new Point(radius, 0));
      int anglesCount = GameConstants.DEFAULT_MAP_RING_ANGLES_COUNT;
      float angleStep = 360.0f / anglesCount;

      List<ROVector2f> points = new ArrayList<ROVector2f>();
      points.add(v.toPhysVector());
      for (int i = 0; i < anglesCount; ++i)
      {
         v.rotate(angleStep);
         points.add(v.toPhysVector());
      }

      float biggerRadius = radius * 1.2f;
      v.setLength(biggerRadius);
      points.add(v.toPhysVector());
      for (int i = 0; i < anglesCount; ++i)
      {
         v.rotate(-angleStep);
         points.add(v.toPhysVector());
      }

      Polygon polygon = new Polygon(points.toArray(new ROVector2f[points.size()]));
      List<StaticBody> res = new ArrayList<StaticBody>();
      StaticBody body = new StaticBody(polygon);
      body.setRestitution(1.0f);
      res.add(body);
      return new Map(res);
   }
}
