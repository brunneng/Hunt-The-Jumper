package com.greenteam.huntjumper.map;

import com.greenteam.huntjumper.utils.Point;
import com.greenteam.huntjumper.utils.Segment;
import com.greenteam.huntjumper.utils.Vector2D;

import java.util.*;

/**
 * User: GreenTea Date: 29.01.12 Time: 20:39
 */
public class AvailabilityMap
{
   public static final byte FREE = 0;
   public static final byte WALL = 1;

   int countX;
   int countY;
   private Vector2D translationVector;
   private byte[][] map;

   public AvailabilityMap(Collection<Segment> segments)
   {
      float minX = Integer.MAX_VALUE;
      float minY = Integer.MAX_VALUE;
      float maxX = Integer.MIN_VALUE;
      float maxY = Integer.MIN_VALUE;

      for (Segment s : segments)
      {
         float x1 = s.getEnd1().getX();
         float y1 = s.getEnd1().getY();

         float x2 = s.getEnd2().getX();
         float y2 = s.getEnd2().getY();

         if (x1 < minX)
         {
            minX = x1;
         }
         if (x2 < minX)
         {
            minX = x2;
         }

         if (x1 > maxX)
         {
            maxX = x1;
         }
         if (x2 > maxX)
         {
            maxX = x2;
         }

         if (y1 < minY)
         {
            minY = y1;
         }
         if (y2 < minY)
         {
            minY = y2;
         }

         if (y1 > maxY)
         {
            maxY = y1;
         }
         if (y2 > maxY)
         {
            maxY = y2;
         }
      }

      List<Segment> tSegments = new ArrayList<Segment>(segments.size());
      Vector2D tv = new Vector2D(-minX, -minY);
      translationVector = tv;

      for (Segment s : segments)
      {
         tSegments.add(new Segment(
                 new Point(s.getEnd1()).plus(tv), new Point(s.getEnd2()).plus(tv)));
      }

      countX = (int)(maxX - minX) + 1;
      countY = (int)(maxY - minY) + 1;

      map = new byte[countY][countX];
      List<Integer> iPoints = new ArrayList<Integer>();

      for (int yIndex = 0; yIndex < countY; ++yIndex)
      {
         float y = yIndex + 0.5f;
         Segment lineSegment = new Segment(new Point(0, y), new Point(countX, y));
         iPoints.clear();
         for (Segment s : tSegments)
         {
            Point ip = lineSegment.intersectionWith(s);
            if (ip != null)
            {
               iPoints.add((int)ip.getX());
            }
         }
         Collections.sort(iPoints);

         Iterator<Integer> i = iPoints.iterator();
         if (i.hasNext())
         {
            int start = i.next();

            int iNum = 1;
            while (start < countX-1)
            {
               int end = i.hasNext() ? i.next() : countX-1;

               if (iNum % 2 == 1)
               {
                  for (int xIndex = start; xIndex < end; ++xIndex)
                  {
                     map[yIndex][xIndex] = WALL;
                  }
               }

               start = end;
               iNum++;
            }
         }
      }
   }

   public void drawFreeLine(Segment segment, float radius)
   {
      Segment tSegment = new Segment(
              new Point(segment.getEnd1()).plus(translationVector), 
              new Point(segment.getEnd2()).plus(translationVector));
      
      for (int y = 0; y < countY; ++y)
      {
         for (int x = 0; x < countX; ++x)
         {
            if (getValue(x, y) > 0)
            {
               Point p = new Point(x + 0.5f, y + 0.5f);
               float dist = tSegment.distanceTo(p);
//               System.out.println(dist);
               if (dist < radius)
               {
                  setValue(x, y, (byte)0);
               }
            }
         }
      }
   }

   public int getCountX()
   {
      return countX;
   }

   public int getCountY()
   {
      return countY;
   }

   public byte getValue(int x, int y)
   {
      return map[y][x];
   }

   public byte setValue(int x, int y, byte value)
   {
      return map[y][x] = value;
   }
}
