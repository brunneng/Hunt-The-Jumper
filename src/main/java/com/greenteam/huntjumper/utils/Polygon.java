package com.greenteam.huntjumper.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * User: GreenTea Date: 02.02.12 Time: 19:35
 */
public class Polygon
{
   private List<Segment> segments;

   public Polygon(List<Segment> segments)
   {
      this.segments = segments;
   }

   public List<Segment> getSegments()
   {
      return segments;
   }
   
   public Polygon rotate(Point center, float angle)
   {
      List<Segment> res = new ArrayList<Segment>();
      for (Segment s : segments)
      {
         Vector2D v1 = new Vector2D(center, s.getEnd1());
         Vector2D v2 = new Vector2D(center, s.getEnd2());
         v1.rotate(angle);
         v2.rotate(angle);

         Segment ts = new Segment(new Point(center).plus(v1), new Point(center).plus(v2));
         res.add(ts);
      }

      return new Polygon(res);
   }

   public Polygon turnOverLine(Segment line)
   {
      List<Segment> res = new ArrayList<Segment>();
      for (Segment s : segments)
      {
         Vector2D p1 = line.perpendicularToLine(s.getEnd1()).multiply(2);
         Vector2D p2 = line.perpendicularToLine(s.getEnd2()).multiply(2);
         Segment ts = new Segment(new Point(s.getEnd1()).plus(p1), new Point(s.getEnd2()).plus(p2));
         res.add(ts);
      }
      
      return new Polygon(res);
   }
}
