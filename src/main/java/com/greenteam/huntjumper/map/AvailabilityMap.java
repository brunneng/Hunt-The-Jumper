package com.greenteam.huntjumper.map;

import com.greenteam.huntjumper.match.InitializationScreen;
import com.greenteam.huntjumper.utils.*;
import com.greenteam.huntjumper.utils.Point;
import com.greenteam.huntjumper.utils.Polygon;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * User: GreenTea Date: 29.01.12 Time: 20:39
 */
public class AvailabilityMap
{

   public static final double GEL_WIDTH_FACTOR = 0.004;
   public static final float HALF_CELL = 0.5f;
   public static final float MAKE_POLYGON_MIN_DISTANCE = 1.0f;

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

   private final float maxXdist;
   private final float maxYdist;
   public final int countX;
   public final int countY;
   private Vector2D translationVector;
   private byte[][] map;

   public AvailabilityMap(File mapFile) throws IOException
   {
      CompressedMap compressedMap = Utils.loadMap(mapFile);
      countX = compressedMap.getWidth();
      countY = compressedMap.getWidth();
      maxXdist = countX;
      maxYdist = countY;

      translationVector = new Vector2D(maxXdist / 2, maxYdist / 2);

      map = new byte[countY][countX];

      int lineCounter = 0;
      int lineNum = 0;
      boolean white = true;
      int[] whiteBlackLines = compressedMap.getWhiteBlackLines();

      for (int x = 0; x < countX; ++x)
      {
         for (int y = 0; y < countY; ++y)
         {
            if (!white)
            {
               setValue(x, y, WALL);
            }

            lineCounter++;
            if (lineCounter >= whiteBlackLines[lineNum])
            {
               lineCounter = 0;
               white = !white;
               lineNum++;
            }
         }

         if (x % 10 == 0 || x == countX-1)
         {
            Integer successPresent = 1 + (int)(100*(float)x / countX);
            InitializationScreen.getInstance().setStatus("Preparing map ", successPresent);
         }
      }
   }

   public AvailabilityMap(List<Polygon> polygons)
   {
      this(Polygon.getAllSegments(polygons));
   }
   
   public AvailabilityMap(Collection<Segment> segments)
   {
      float[] minXYmaxXY = findMinXYMaxXY(segments);
      float minX = minXYmaxXY[0];
      float minY = minXYmaxXY[1];
      float maxX = minXYmaxXY[2];
      float maxY = minXYmaxXY[3];

      List<Segment> tSegments = new ArrayList<Segment>(segments.size());
      Vector2D tv = new Vector2D(-minX + HALF_CELL, -minY + HALF_CELL);
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
         float y = yIndex + HALF_CELL;
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
               Point p = new Point(x + HALF_CELL, y + HALF_CELL);
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
               Point p = new Point(x + HALF_CELL, y + HALF_CELL);
               float dist = tSegment.distanceTo(p);
               if (dist < radius)
               {
                  setValue(x, y, WALL);
               }
            }
         }
      });
   }

   public boolean isValid(int x, int y)
   {
      return x >= 0 && x < countX && y >= 0 && y < countY;
   }

   public boolean isValid(IntPoint p)
   {
      return p.x >= 0 && p.x < countX && p.y >= 0 && p.y < countY;
   }

   public boolean isFree(IntPoint p)
   {
      return map[p.y][p.x] == FREE;
   }

   public boolean isFree(int x, int y)
   {
      return map[y][x] == FREE;
   }
   
   private void fillPolygon(IntPoint startPoint)
   {
      List<IntPoint> wavePoints = new ArrayList<IntPoint>();
      wavePoints.add(startPoint);

      while (wavePoints.size() > 0)
      {
         List<IntPoint> newWavePoints = new ArrayList<IntPoint>(wavePoints.size());

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
                     if (!isValid(nearPoint) || isFree(nearPoint))
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
         if (!isValid(next) || isFree(next))
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
            for (List<Direction> directions : Direction.directionsWithDiagonals)
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
      Vector2D tv = translationVector.negate();
      final float minDistToPrev = MAKE_POLYGON_MIN_DISTANCE;
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
            float dist = s.distanceTo(mostFarPrevP);
            if (dist > minDistToPrev)
            {
               int i = prevPList.indexOf(mostFarPrevP);
               IntPoint prev = prevList.get(i);

               segments.add(new Segment(end1P, true, mostFarPrevP, false).plus(tv));
               
               if (getValue(end1) != MAKE_POLYGON_START_POINT)
               {
                  setValue(end1, POLYGON_BORDER_CHECKPOINT);
               }
               
               end1 = prev;
               end1P = mostFarPrevP;

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

   private void updateStatus(InitializationScreen initScreen,
                             int polygonsPrepared, int polygonsCreated)
   {
      if (initScreen != null)
      {
         InitializationScreen.getInstance().setStatus("Prepare physics polygons: [ " +
                 polygonsPrepared + " / " + polygonsCreated + " ]");
      }
   }

   public List<Polygon> splitOnPolygons()
   {
      final InitializationScreen initScreen = InitializationScreen.getInstance();
      final List<Polygon> polygons = new ArrayList<Polygon>();

      final int[] polygonsCounter = new int[2];
      processAllPoints(new IPointProcessor()
      {
         @Override
         public void process(int x, int y)
         {
            if (getValue(x, y) == WALL)
            {
               IntPoint p = new IntPoint(x, y);
               fillPolygon(p);
               polygonsCounter[0]++;
               updateStatus(initScreen, polygonsCounter[0], polygonsCounter[1]);
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
                  polygonsCounter[1]++;
                  updateStatus(initScreen, polygonsCounter[0], polygonsCounter[1]);
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

   private Collection<IntPoint> putOneLineOfGel(Collection<IntPoint> oldGelPoints)
   {
      final Set<IntPoint> newGelPoints = new HashSet<IntPoint>();
      if (oldGelPoints == null)
      {
         processAllPoints(new IPointProcessor()
         {
            @Override
            public void process(int x, int y)
            {
               IntPoint p = new IntPoint(x, y);
               byte value = getValue(p);
               if (value == WALL || value == GEL)
               {
                  for (Direction d : Direction.values)
                  {
                     IntPoint next = p.plus(d);
                     if (isValid(next) && getValue(next) == FREE)
                     {
                        newGelPoints.add(next);
                     }
                  }
               }
            }
         });
      }
      else
      {
         for (IntPoint p : oldGelPoints)
         {
            byte value = getValue(p);
            if (value == WALL || value == GEL)
            {
               for (Direction d : Direction.values)
               {
                  IntPoint next = p.plus(d);
                  if (isValid(next) && getValue(next) == FREE)
                  {
                     newGelPoints.add(next);
                  }
               }
            }
         }
      }

      for (IntPoint p : newGelPoints)
      {
         setValue(p, GEL);
      }

      return newGelPoints;
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
               for (Direction d : Direction.values)
               {
                  IntPoint next = p.plus(d);
                  if (isValid(next) && getValue(next) == FREE)
                  {
                     newFreePoints.add(p);
                     break;
                  }
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
      final int linesOfGel = (int)(Math.max(countX, countY) * GEL_WIDTH_FACTOR);

      Collection<IntPoint> gelPoints = null;
      for (int i = 0; i < linesOfGel; ++i)
      {
         gelPoints = putOneLineOfGel(gelPoints);
      }

      for (int i = 0; i < linesOfGel; ++i)
      {
         removeOneLineOfGel();
      }

      gelBecomeHard();
   }
   
   public void saveToFile(String fileName)
   {
      List<Integer> whiteBlackLines = new ArrayList<Integer>();
      boolean currentLineWhite = true;
      int lineLength = 0;
      for (int x = 0; x < getCountX(); ++x)
      {
         for (int y = 0; y < getCountY(); ++y)
         {
            byte value = getValue(x, getCountY() - y - 1);
            boolean white = value == AvailabilityMap.FREE;
            if ((currentLineWhite && white) || (!currentLineWhite && !white))
            {
               lineLength++;
            }
            else
            {
               whiteBlackLines.add(lineLength);
               lineLength = 1;
               currentLineWhite = white;
            }
         }
      }
      whiteBlackLines.add(lineLength);
      CompressedMap compressedMap = new CompressedMap(getCountX(), getCountY(), whiteBlackLines);
      try (FileOutputStream fos = new FileOutputStream(fileName))
      {
         ObjectOutputStream oos = new ObjectOutputStream(fos);
         oos.writeObject(compressedMap);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }

      // image
      BufferedImage image = new BufferedImage(getCountX(), getCountY(),
              BufferedImage.TYPE_INT_BGR);
      for (int x = 0; x < getCountX(); ++x)
      {
         for (int y = 0; y < getCountY(); ++y)
         {
            byte value = getValue(x, getCountY() - y - 1);
            Color c = Color.WHITE;
            if (value != AvailabilityMap.FREE)
            {
               c = Color.BLACK;
            }

            image.setRGB(x, y, c.getRGB());
         }
      }

      fileName = fileName + ".gif";
      String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1);
      File outputFile = new File(fileName);
      try
      {
         ImageIO.write(image, fileExtension, outputFile);
      }
      catch (IOException e)
      {
         e.printStackTrace();
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

//   public int getCountOfFreeNearPoints(int x, int y)
//   {
//      int res = 0;
//      List<Direction> values = Direction.values;
//      for (int i = 0, valuesSize = values.size(); i < valuesSize; i++)
//      {
//         Direction d = values.get(i);
//
//         int nx = x + d.dx;
//         int ny = y + d.dy;
//
//         if (isValid(nx, ny) && isFree(nx, ny))
//         {
//            res++;
//         }
//      }
//      return res;
//   }

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

   public Vector2D getTranslationVector()
   {
      return translationVector;
   }
}
