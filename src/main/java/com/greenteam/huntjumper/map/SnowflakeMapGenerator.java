package com.greenteam.huntjumper.map;

import com.greenteam.huntjumper.utils.*;
import com.greenteam.huntjumper.utils.Point;
import com.greenteam.huntjumper.utils.Polygon;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
   
   private static void drawHorizontalLines(AvailabilityMap am, Segment s1, Segment s2, 
                                           float minWidth, float maxWidth, float totalWidth,
                                           float maxAngleAv, float maxAngleDeviation,
                                           int minChangeDirectionCount)
   {
      Random rand = Utils.rand;
      float dw = maxWidth - minWidth;

      while (totalWidth > 0)
      {
         float w = minWidth + rand.nextFloat()*dw;
         totalWidth -= w;

         Segment l0 = new Segment(s1.getRandomPoint(), s2.getRandomPoint());
         float subSegmentLength = (float)l0.length() / minChangeDirectionCount;
         float maxLen = 1.5f*(float)l0.length();
         float currLen = 0;
         Point start = l0.getEnd1();

         float angleChangeAv = rand.nextFloat()*maxAngleAv;
         Vector2D currVector = new Vector2D(l0.getEnd1(), l0.getEnd2()).setLength(subSegmentLength);
         Segment currSegment = null;


         while (currSegment == null || currLen < maxLen)
         {
            currSegment = new Segment(start, new Point(start).plus(currVector));
            currLen += subSegmentLength;
            am.drawFreeLine(currSegment, w);

            float angleChangeDeviation = rand.nextFloat()*maxAngleDeviation;
            float angleChange = angleChangeAv + angleChangeDeviation;
            currVector = currVector.rotate(angleChange);
            start = currSegment.getEnd2();
         }
      }
      
   }
   

   public static void generateMap(int segmentsCount, String imageFileName) throws IOException
   {
      validateArgs(segmentsCount);

      float segmentAngle = 360.0f / segmentsCount;

      final float r = 400;
      Vector2D v1 = new Vector2D(r, 0);
      Vector2D v2 = new Vector2D(r, 0);

      Vector2D vertical = new Vector2D(r, 0).rotate(270);
      v1 = v1.rotate(270 - segmentAngle/2);
      v2 = v2.rotate(270 + segmentAngle/2);
      
      Point p1 = new Point(0, 0);
      Point p2 = new Point(v1.getX(), v1.getY());
      Point p3 = new Point(v2.getX(), v2.getY());

      List<Segment> segments = new ArrayList<Segment>();
      segments.add(new Segment(p1, p2));
      segments.add(new Segment(p1, p3));
      segments.add(new Segment(p2, p3));

      AvailabilityMap am = new AvailabilityMap(segments);
      Segment l4 = new Segment(p2, p3);

      drawHorizontalLines(am, segments.get(0), segments.get(1), r / 40, r / 30, r / 5, 10f, 3f, 3);
      am.drawFreeLine(new Segment(p1, p1), r / 20);
      am.drawWall(l4, r / 100);

      List<Polygon> polygons = am.splitOnPolygons();
      List<Polygon> allPolygons = new ArrayList<Polygon>();

      for (int i = 0; i < segmentsCount; ++i)
      {
         float rotationAngle = segmentAngle*i;
         for (Polygon p : polygons)
         {
            Polygon next = p.rotate(p1, rotationAngle);
            if (i % 2 == 1)
            {
               Vector2D rotatedV = vertical.rotate(rotationAngle);
               Segment line = new Segment(p1, new Point(p1).plus(rotatedV));
               next = next.turnOverLine(line);
            }
            allPolygons.add(next.multiply(5, 5));
         }
      }

      am = new AvailabilityMap(allPolygons);
      am.removeIsolatedFreePoints();
      am.splitOnPolygons();
      am.saveToFile(imageFileName);

//      BufferedImage image = new BufferedImage(am.getCountX(), am.getCountY(),
//              BufferedImage.TYPE_INT_BGR);
//
//      for (int i = 0; i < am.getCountX(); ++i)
//      {
//         for (int j = 0; j < am.getCountY(); ++j)
//         {
//            byte value = am.getValue(i, am.getCountY() - j - 1);
//            Color c = Color.WHITE;
//            if (value == AvailabilityMap.WALL)
//            {
//               c = Color.BLACK;
//            }
//            else if (value == AvailabilityMap.POLYGON)
//            {
//               c = Color.GRAY;
//            }
//            else if (value == AvailabilityMap.POLYGON_BORDER)
//            {
//               c = Color.PINK;
//            }
//            else if (value == AvailabilityMap.POLYGON_BORDER_EXECUTED)
//            {
//               c = Color.GREEN;
//            }
//            else if (value == AvailabilityMap.POLYGON_BORDER_CHECKPOINT)
//            {
//               c = Color.RED;
//            }
//            else if (value == AvailabilityMap.MAKE_POLYGON_START_POINT)
//            {
//               c = Color.MAGENTA;
//            }
//            else if (value == AvailabilityMap.GEL)
//            {
//               c = Color.YELLOW;
//            }
//
//            image.setRGB(i, j, c.getRGB());
//         }
//      }
//      File outputfile = new File("saved.png");
//      try
//      {
//         ImageIO.write(image, "png", outputfile);
//      }
//      catch (IOException e)
//      {
//         e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//      }
   }
   
   public static void main(String[] args) throws IOException
   {
      generateMap(12, "saved.png");
   }

}
