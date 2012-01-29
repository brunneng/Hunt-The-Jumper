package com.greenteam.huntjumper.utils;

/**
 * User: GreenTea Date: 29.01.12 Time: 15:23
 */
public class Segment
{
   private Point end1;
   private Point end2;

   private boolean needFindLineEquation = true;
   private float a;
   private float b;
   private float c;
   private Range xRange;
   private Range yRange;

   public Segment(Point end1, Point end2)
   {
      this.end1 = end1;
      this.end2 = end2;
   }

   public Point getEnd1()
   {
      return end1;
   }

   public void setEnd1(Point end1)
   {
      this.end1 = end1;
   }

   public Point getEnd2()
   {
      return end2;
   }

   public void setEnd2(Point end2)
   {
      this.end2 = end2;
   }

   public float getA()
   {
      return a;
   }

   public float getB()
   {
      return b;
   }

   public float getC()
   {
      return c;
   }

   public Range getXRange()
   {
      return xRange;
   }

   public Range getYRange()
   {
      return yRange;
   }

   private void initLineEquation()
   {
      if (!needFindLineEquation)
      {
         return;
      }

      float x1 = end1.getX();
      float x2 = end2.getX();
      float y1 = end1.getY();
      float y2 = end2.getY();

      xRange = new Range(Math.min(x1, x2), Math.max(x1, x2));
      yRange = new Range(Math.min(y1, y2), Math.max(y1, y2));

      boolean xEq = Utils.equals(x1, x2);
      boolean yEq = Utils.equals(y1, y2);
      if (xEq && yEq)
      {
         a = 1;
         b = -1;
         c = 0;
      }
      else if (xEq)
      {
         a = 1;
         b = 0;
         c = x1;
      }
      else if (yEq)
      {
         a = 0;
         b = 1;
         c = y1;
      }
      else
      {
         b = 1;
         float d = x2 - x1;
         float da = y1 - y2;
         float dc = x2*y1 - x1*y2;
         a = da / d;
         c = dc / d;
      }

      needFindLineEquation = false;
   }

   public Point findIntersection(Segment other)
   {
      initLineEquation();
      other.initLineEquation();

      float a1 = a;
      float b1 = b;
      float c1 = c;
      float a2 = other.a;
      float b2 = other.b;
      float c2 = other.c;

      float d = a1*b2 - a2*b1;
      if (Utils.equals(d, 0)) // lines are parallel
      {
         return null;
      }

      float dx = c1*b2 - c2*b1;
      float x = dx / d;
      
      float dy = a1*c2 - a2*c1;
      float y = dy / d;
      
      Point res = null;
      if (xRange.contains(x) && other.xRange.contains(x) &&
          yRange.contains(y) && other.yRange.contains(y))
      {
         res = new Point(x, y);
      }

      return res;
   }
}
