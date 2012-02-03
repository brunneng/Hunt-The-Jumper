package com.greenteam.huntjumper.map;

import com.greenteam.huntjumper.utils.Point;
import com.greenteam.huntjumper.utils.Segment;
import com.greenteam.huntjumper.utils.Vector2D;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
      v2.rotate(270 + segmentAngle/2);
      
      Point p1 = new Point(0, 0);
      Point p2 = new Point(v1.getX(), v1.getY());
      Point p3 = new Point(v2.getX(), v2.getY());

      List<Segment> segments = new ArrayList<Segment>();
      segments.add(new Segment(p1, p2));
      segments.add(new Segment(p1, p3));
      segments.add(new Segment(p2, p3));

      AvailabilityMap am = new AvailabilityMap(segments);
      Segment l1 = new Segment(segments.get(0).getRandomPoint(), segments.get(1).getRandomPoint());
      Segment l2 = new Segment(segments.get(0).getRandomPoint(), segments.get(1).getRandomPoint());
      Segment l3 = new Segment(p1, segments.get(2).getRandomPoint());
      Segment l4 = new Segment(p2, p3);
      float lineWidth = r / 20;
      am.drawFreeLine(l1, lineWidth);
      am.drawFreeLine(l2, lineWidth);
      am.drawFreeLine(l3, lineWidth);
      am.drawWall(l4, lineWidth / 5);
//      Segment lx = new Segment(segments.get(0).getPoint(0.5f), segments.get(1).getPoint(0.5f));
//      am.drawFreeLine(lx, lineWidth);
      am.splitOnPolygons();

      BufferedImage image = new BufferedImage(am.getCountX(), am.getCountY(),
              BufferedImage.TYPE_INT_BGR);
      
      for (int i = 0; i < am.getCountX(); ++i)
      {
         for (int j = 0; j < am.getCountY(); ++j)
         {
            byte value = am.getValue(i, am.getCountY() - j - 1);
            Color c = Color.WHITE;
            if (value == AvailabilityMap.WALL)
            {
               c = Color.BLACK;
            }
            else if (value == AvailabilityMap.POLYGON)
            {
               c = Color.GRAY;
            }
            else if (value == AvailabilityMap.POLYGON_BORDER)
            {
               c = Color.RED;
            }
            else if (value == AvailabilityMap.POLYGON_BORDER_EXECUTED)
            {
               c = Color.GREEN;
            }
            else if (value == AvailabilityMap.POLYGON_BORDER_CHECKPOINT)
            {
               c = Color.RED;
            }
            else if (value == AvailabilityMap.MAKE_POLYGON_START_POINT)
            {
               c = Color.ORANGE;
            }

            image.setRGB(i, j, c.getRGB());
         }
      }
      File outputfile = new File("saved.png");
      try
      {
         ImageIO.write(image, "png", outputfile);
      }
      catch (IOException e)
      {
         e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      }

//      try
//      {
//         Image image = new Image(am.getCountX(), am.getCountY());
//
//      }
//      catch (SlickException e)
//      {
//         e.printStackTrace();
//      }

      return null;
   }
   
   public static void main(String[] args)
   {
      generateMap(12);
   }

}
