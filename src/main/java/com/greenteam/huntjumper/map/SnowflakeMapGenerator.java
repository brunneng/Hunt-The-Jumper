package com.greenteam.huntjumper.map;

import com.greenteam.huntjumper.utils.Point;
import com.greenteam.huntjumper.utils.Segment;
import com.greenteam.huntjumper.utils.Vector2D;

import java.util.ArrayList;
import java.util.List;

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

      List<Segment> segments = new ArrayList<Segment>();
      segments.add(new Segment(p1, p2));
      segments.add(new Segment(p1, p3));
      segments.add(new Segment(p2, p3));

      AvailabilityMap availabilityMap = new AvailabilityMap(segments);

      return null;
   }
}
