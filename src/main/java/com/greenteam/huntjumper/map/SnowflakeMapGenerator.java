package com.greenteam.huntjumper.map;

import com.greenteam.huntjumper.utils.Point;
import com.greenteam.huntjumper.utils.Segment;
import com.greenteam.huntjumper.utils.Vector2D;

/**
 * User: GreenTea Date: 29.01.12 Time: 15:07
 */
public class SnowflakeMapGenerator
{
   private static void validateArgs(int segmentsCount)
   {
      if (segmentsCount % 2 == 1)
      {
         throw new IllegalArgumentException("Segments count should be even number");
      }

      final int minSegmentsCount = 4;
      if (segmentsCount < minSegmentsCount)
      {
         throw new IllegalArgumentException("Segments count should be more then " +
                 minSegmentsCount);
      }
   }

   public static Map generateMap(int segmentsCount)
   {
      validateArgs(segmentsCount);

      float segmentAngle = 360.0f / segmentsCount;

      final float r = 400;
      Vector2D v1 = new Vector2D(r, 0);
      Vector2D v2 = new Vector2D(r, 0);

      v1.rotate(270 - segmentAngle/2);
      v1.rotate(270 + segmentAngle/2);
      
      Point p1 = new Point(0, 0);
      Point p2 = new Point(v1.getX(), v1.getY());
      Point p3 = new Point(v2.getX(), v2.getY());

      Vector2D t1 = new Vector2D(p2.distanceTo(p2)/2, p2.getY());
      p1.plus(t1);
      p2.plus(t1);
      p3.plus(t1);
      Segment s1 = new Segment(p1, p2);
      Segment s2 = new Segment(p1, p3);

      int maxX = (int)t1.getX()*2 + 1;
      int maxY =  (int)t1.getY() + 1;
      byte[][] templateMap = new byte[maxX][maxY];

      for (int x = 0; x < maxX; ++x)
      {
         float xPos = x + 0.5f;
         Segment lineSegment = new Segment(new Point(xPos, 0), new Point(xPos, maxY));
         Point ip1 = lineSegment.intersectionWith(s1);
         Point ip2 = lineSegment.intersectionWith(s2);
      }


      return null;
   }
}
