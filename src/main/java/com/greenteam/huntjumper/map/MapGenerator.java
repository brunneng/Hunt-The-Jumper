package com.greenteam.huntjumper.map;

import com.greenteam.huntjumper.utils.GameConstants;
import com.greenteam.huntjumper.utils.Point;
import com.greenteam.huntjumper.utils.Utils;
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
      int anglesCount = GameConstants.DEFAULT_MAP_RING_ANGLES_COUNT;

      List<Point> rotationPoints = Utils.getRotationPoints(new Point(0, 0), radius, 0, anglesCount);
      List<ROVector2f> points = Point.toVector2f(rotationPoints);

      Polygon polygon = new Polygon(points.toArray(new ROVector2f[points.size()]));
      StaticBody innerBody = new StaticBody(polygon);
      innerBody.setRestitution(1.0f);

      float biggerRadius = radius * 1.1f;
      rotationPoints = Utils.getRotationPoints(new Point(0, 0), biggerRadius, 0, anglesCount);
      points = Point.toVector2f(rotationPoints);

      polygon = new Polygon(points.toArray(new ROVector2f[points.size()]));
      StaticBody outerBody = new StaticBody(polygon);
      outerBody.setRestitution(1.0f);

      List<StaticBody> polygons = new ArrayList<StaticBody>();
      return new Map(outerBody, innerBody, polygons);
   }
}
