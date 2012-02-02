package com.greenteam.huntjumper.map;

import com.greenteam.huntjumper.utils.*;

import java.util.*;

/**
 * User: GreenTea Date: 29.01.12 Time: 20:39
 */
public class AvailabilityMap
{
   private static interface IPointProcessor
   {
      void process(int x, int y);
   }

   public static final byte FREE = 0;
   public static final byte WALL = 1;
   public static final byte POLYGON = 2;
   public static final byte POLYGON_BORDER = 3;

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
         tSegments.add(s.plus(tv));
      }

      countX = (int)(maxX - minX);
      countY = (int)(maxY - minY);

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

   private void processAllPoints(IPointProcessor processor)
   {
      for (int y = 0; y < countY; ++y)
      {
         for (int x = 0; x < countX; ++x)
         {
            processor.process(x, y);
         }
      }
   }

   public void drawFreeLine(Segment segment, final float radius)
   {
      final Segment tSegment = segment.plus(translationVector);

      processAllPoints(new IPointProcessor()
      {
         @Override
         public void process(int x, int y)
         {
            if (getValue(x, y) != FREE)
            {
               Point p = new Point(x + 0.5f, y + 0.5f);
               float dist = tSegment.distanceTo(p);
               if (dist < radius)
               {
                  setValue(x, y, FREE);
               }
            }
         }
      });
   }

   public void drawWall(Segment segment, final float radius)
   {
      final Segment tSegment = segment.plus(translationVector);

      processAllPoints(new IPointProcessor()
      {
         @Override
         public void process(int x, int y)
         {
            if (getValue(x, y) == FREE)
            {
               Point p = new Point(x + 0.5f, y + 0.5f);
               float dist = tSegment.distanceTo(p);
               if (dist < radius)
               {
                  setValue(x, y, WALL);
               }
            }
         }
      });
   }
   
   private boolean isValid(IntPoint p)
   {
      return p.x >= 0 && p.x < countX && p.y >= 0 && p.y < countY;
   }
   
   private void fillPolygon(IntPoint startPoint)
   {
      List<IntPoint> notExecutedPoints = new ArrayList<IntPoint>();
      notExecutedPoints.add(startPoint);

      while (notExecutedPoints.size() > 0)
      {
         List<IntPoint> newNotExecutedPoints = new ArrayList<IntPoint>();

         for (IntPoint p : notExecutedPoints)
         {
            for (Direction d : Direction.values)
            {
               IntPoint next = p.plus(d);

               if (isValid(next) && getValue(next) == WALL)
               {
                  boolean borderPoint = false;
                  for (Direction dToNear : Direction.values)
                  {
                     IntPoint nearPoint = next.plus(dToNear);
                     if (!isValid(nearPoint) || getValue(nearPoint) == FREE)
                     {
                        borderPoint = true;
                        break;
                     }
                  }

                  setValue(next, borderPoint ? POLYGON_BORDER : POLYGON);
                  newNotExecutedPoints.add(next);
               }
            }
         }

         notExecutedPoints = newNotExecutedPoints;
      }
   }

   private Polygon makePolygon(IntPoint startPoint)
   {
      return null;
   }
   
   public List<Polygon> splitOnPolygons()
   {
      final List<Polygon> res = new ArrayList<Polygon>();
      
      processAllPoints(new IPointProcessor()
      {
         @Override
         public void process(int x, int y)
         {
            if (getValue(x, y) == WALL)
            {
               IntPoint p = new IntPoint(x, y);
               fillPolygon(p);
               Polygon polygon = makePolygon(p);
               if (polygon != null)
               {
                  res.add(polygon);
               }
            }
         }
      });

      return res;
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

   public byte getValue(IntPoint p)
   {
      return map[p.y][p.x];
   }

   public byte setValue(IntPoint p, byte value)
   {
      return map[p.y][p.x] = value;
   }
}
