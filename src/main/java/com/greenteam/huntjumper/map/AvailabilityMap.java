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
   public static final byte POLYGON_BORDER_EXECUTED = 4;
   public static final byte POLYGON_BORDER_CHECKPOINT = 5;
   public static final byte MAKE_POLYGON_START_POINT = 6;

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

   private boolean isFree(IntPoint p)
   {
      return !isValid(p) || getValue(p) == FREE;
   }
   
   private void fillPolygon(IntPoint startPoint)
   {
      List<IntPoint> wavePoints = new ArrayList<IntPoint>();
      wavePoints.add(startPoint);

      while (wavePoints.size() > 0)
      {
         List<IntPoint> newWavePoints = new ArrayList<IntPoint>();

         for (IntPoint p : wavePoints)
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
                     if (isFree(nearPoint))
                     {
                        borderPoint = true;
                        break;
                     }
                  }

                  setValue(next, borderPoint ? POLYGON_BORDER : POLYGON);
                  newWavePoints.add(next);
               }
            }
         }

         wavePoints = newWavePoints;
      }
   }
   
   private List<IntPoint> findFreePoints(IntPoint p)
   {
      List<IntPoint> res = new ArrayList<IntPoint>();
      for (Direction d : Direction.values())
      {
         IntPoint next = p.plus(d);
         if (isFree(next))
         {
            res.add(next);
         }
      }
      return res;
   }
   
   private int findMinSquareDistance(List<IntPoint> points1, List<IntPoint> points2)
   {
      int min = Integer.MAX_VALUE;
      for (IntPoint p1 : points1)
      {
         for (IntPoint p2 : points2)
         {
            int dist = p1.squareDistanceTo(p2);
            if (dist < min)
            {
               min = dist;
            }
         }
      }
      return min;
   }
   
   private IntPoint findNearestBorderByFreePoint(IntPoint start)
   {
      List<IntPoint> nearFreePoints = findFreePoints(start);
      
      Set<IntPoint> executedPoints = new HashSet<IntPoint>();
      List<IntPoint> wavePoints = new ArrayList<IntPoint>();
      wavePoints.add(start);

      IntPoint nearest = null;
      int minDist = Integer.MAX_VALUE;
      while (wavePoints.size() > 0 && nearest == null)
      {
         List<IntPoint> newWavePoints = new ArrayList<IntPoint>();

         for (IntPoint p : wavePoints)
         {
            for (Direction d : Direction.values)
            {
               IntPoint next = p.plus(d);

               if (isValid(next) && getValue(next) != FREE && !executedPoints.contains(next))
               {
                  if (getValue(next) == POLYGON_BORDER)
                  {
                     List<IntPoint> nextFreePoints = findFreePoints(next);
                     int dist = findMinSquareDistance(nearFreePoints, nextFreePoints);
                     
                     if (dist < minDist)
                     {
                        minDist = dist;
                        nearest = next;
                     }
                  }

                  executedPoints.add(next);
                  newWavePoints.add(next);
               }
            }
         }

         wavePoints = newWavePoints;
      }
      
      return nearest;
   }

   private Polygon makePolygon(IntPoint startPoint)
   {
      final float minDistToPrev = 1.0f;
      List<Segment> segments = new ArrayList<Segment>();
      setValue(startPoint, MAKE_POLYGON_START_POINT);
      
      IntPoint curr = startPoint;
      Point currP = startPoint.toPoint();
      
      IntPoint end1 = curr;
      Point end1P = end1.toPoint();
      
      List<IntPoint> prevList = new ArrayList<IntPoint>();
      List<Point> prevPList = new ArrayList<Point>();
      
      while (true)
      {
         IntPoint next = findNearestBorderByFreePoint(curr);

         if (next == null)
         {
            setValue(end1, POLYGON_BORDER_CHECKPOINT);
            segments.add(new Segment(end1P, startPoint.toPoint()));
            break;
         }
         Point nextP = next.toPoint();

         Segment s = new Segment(end1P, nextP);
         Point mostFarPrevP = s.findMostFarPoint(prevPList);
         if (mostFarPrevP != null)
         {
            int i = prevPList.indexOf(mostFarPrevP);
            IntPoint prev = prevList.get(i);
            Point prevP = prevPList.get(i);
            
            float dist = s.distanceTo(prevP);
            if (dist > minDistToPrev)
            {
               segments.add(new Segment(end1P, prevP));
               
               if (getValue(end1) != MAKE_POLYGON_START_POINT)
               {
                  setValue(end1, POLYGON_BORDER_CHECKPOINT);
               }
               
               end1 = prev;
               end1P = prevP;

               prevList = new ArrayList<IntPoint>(prevList.subList(i+1, prevList.size()));
               prevPList = new ArrayList<Point>(prevPList.subList(i+1, prevPList.size()));
            }
         }

         if (getValue(curr) == POLYGON_BORDER)
         {
            setValue(curr, POLYGON_BORDER_EXECUTED);
         }

         prevList.add(curr);
         prevPList.add(currP);

         curr = next;
         currP = nextP;
      }
      
      return new Polygon(segments);
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
