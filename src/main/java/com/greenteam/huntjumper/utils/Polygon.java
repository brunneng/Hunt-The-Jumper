package com.greenteam.huntjumper.utils;

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
}
