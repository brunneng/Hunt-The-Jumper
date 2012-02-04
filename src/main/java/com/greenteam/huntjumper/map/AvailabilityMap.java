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
   
   private static float[] findMinXYMaxXY(Collection<Segment> segments)
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
      
      return new float[] {minX, minY, maxX, maxY};
   }

   public static final byte FREE = 0;
   public static final byte WALL = 1;
   public static final byte POLYGON = 2;
   public static final byte POLYGON_BORDER = 3;
   public static final byte POLYGON_BORDER_EXECUTED = 4;
   public static final byte POLYGON_BORDER_CHECKPOINT = 5;
   public static final byte MAKE_POLYGON_START_POINT = 6;
   public static final byte GEL = 7;

   float maxXdist;
   float maxYdist;
   int countX;
   int countY;
   private Vector2D translationVector;
   private byte[][] map;

   public AvailabilityMap(Collection<Segment> segments)
   {
      float[] minXYmaxXY = findMinXYMaxXY(segments);
      float minX = minXYmaxXY[0];
      float minY = minXYmaxXY[1];
      float maxX = minXYmaxXY[2];
      float maxY = minXYmaxXY[3];

      List<Segment> tSegments = new ArrayList<Segment>(segments.size());
      Vector2D tv = new Vector2D(-minX + 0.5f, -minY + 0.5f);
      translationVector = tv;

      for (Segment s : segments)
      {
         tSegments.add(s.plus(tv));
      }

      maxXdist = maxX - minX;
      maxYdist = maxY - minY;
      countX = (int)maxXdist;
      countY = (int)maxYdist;

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

      fixErrors();
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
      List<List<Direction>> directionsWithDiagonals = Direction.getDirectionsWithDiagonals();
      while (wavePoints.size() > 0 && nearest == null)
      {
         List<IntPoint> newWavePoints = new ArrayList<IntPoint>();

         for (IntPoint p : wavePoints)
         {
            for (List<Direction> directions : directionsWithDiagonals)
            {
               IntPoint next = p;
               for (Direction d : directions)
               {
                  next = next.plus(d);
               }

               if (isValid(next))
               {
                  byte value = getValue(next);
                  if ((value == POLYGON_BORDER || value == POLYGON_BORDER_EXECUTED ||
                     value == POLYGON_BORDER_CHECKPOINT)
                          && !executedPoints.contains(next))
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
         }

         wavePoints = newWavePoints;
      }
      
      return nearest;
   }

   private Polygon makePolygon(IntPoint startPoint)
   {
      Vector2D tv = new Vector2D(translationVector).negate();
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
            segments.add(new Segment(end1P, true, startPoint.toPoint(), false).plus(tv));
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
               segments.add(new Segment(end1P, true, prevP, false).plus(tv));
               
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
      final List<Polygon> polygons = new ArrayList<Polygon>();

      processAllPoints(new IPointProcessor()
      {
         @Override
         public void process(int x, int y)
         {
            if (getValue(x, y) == WALL)
            {
               IntPoint p = new IntPoint(x, y);
               fillPolygon(p);
            }
         }
      });

      processAllPoints(new IPointProcessor()
      {
         @Override
         public void process(int x, int y)
         {
            if (getValue(x, y) == POLYGON_BORDER)
            {
               IntPoint p = new IntPoint(x, y);
               Polygon polygon = makePolygon(p);
               if (polygon != null)
               {
                  polygons.add(polygon);
               }
            }
         }
      });

      float xFactor = this.maxXdist / countX;
      float yFactor = this.maxYdist / countY;

      List<Polygon> res = new ArrayList<Polygon>();
      for (Polygon p : polygons)
      {
         res.add(p.multiply(xFactor, yFactor));
      }

      return res;
   }

   private void putOneLineOfGel()
   {
      final List<IntPoint> newGelPoints = new ArrayList<IntPoint>();
      processAllPoints(new IPointProcessor()
      {
         @Override
         public void process(int x, int y)
         {
            IntPoint p = new IntPoint(x, y);
            if (getValue(p) == FREE)
            {
               int notFreePointsCount = 0;
               for (Direction d : Direction.values())
               {
                  IntPoint next = p.plus(d);
                  if (isValid(next) && getValue(next) != FREE)
                  {
                     notFreePointsCount++;
                  }
               }

               if (notFreePointsCount > 0)
               {
                  newGelPoints.add(p);
               }
            }
         }
      });

      for (IntPoint p : newGelPoints)
      {
         setValue(p, GEL);
      }
   }

   private void removeOneLineOfGel()
   {
      final List<IntPoint> newFreePoints = new ArrayList<IntPoint>();
      processAllPoints(new IPointProcessor()
      {
         @Override
         public void process(int x, int y)
         {
            IntPoint p = new IntPoint(x, y);
            if (getValue(p) == GEL)
            {
               int freePointsCount = 0;
               for (Direction d : Direction.values())
               {
                  IntPoint next = p.plus(d);
                  if (isValid(next) && getValue(next) == FREE)
                  {
                     freePointsCount++;
                  }
               }

               if (freePointsCount > 0)
               {
                  newFreePoints.add(p);
               }
            }
         }
      });
      
      for (IntPoint p : newFreePoints)
      {
         setValue(p, FREE);
      }
   }

   private void gelBecomeHard()
   {
      processAllPoints(new IPointProcessor()
      {
         @Override
         public void process(int x, int y)
         {
            IntPoint p = new IntPoint(x, y);
            if (getValue(p) == GEL)
            {
               setValue(p, WALL);
            }
         }
      });
   }

   private void fixErrors()
   {
      processAllPoints(new IPointProcessor()
      {
         @Override
         public void process(int x, int y)
         {
            IntPoint p = new IntPoint(x, y);
            IntPoint above = new IntPoint(x, y + 1);
            IntPoint bottom = new IntPoint(x, y - 1);
            IntPoint left = new IntPoint(x - 1, y);
            IntPoint right = new IntPoint(x + 1, y);
            
            if (isValid(above) && isValid(bottom) && isValid(left) && isValid(right))
            {
               byte v = getValue(p);
               byte vAbove = getValue(above);
               byte vBottom = getValue(bottom);
               byte vLeft = getValue(left);
               byte vRight = getValue(right);
               
               if (v == FREE && vAbove == WALL && vBottom == WALL)
               {
                  setValue(p, WALL);
               }
               else if (v == WALL && vAbove == FREE && vBottom == FREE)
               {
                  setValue(p, FREE);
               }
               else if (v == FREE && vLeft == WALL && vRight == WALL)
               {
                  setValue(p, WALL);
               }
               else if (v == WALL && vLeft == FREE && vRight == FREE)
               {
                  setValue(p, FREE);
               }
            }
         }
      });
   }

   public void removeIsolatedFreePoints()
   {
      final int linesOfGel = 3;
      for (int i = 0; i < linesOfGel; ++i)
      {
         putOneLineOfGel();
      }

      for (int i = 0; i < linesOfGel; ++i)
      {
         removeOneLineOfGel();
      }

      gelBecomeHard();
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
