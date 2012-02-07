package com.greenteam.huntjumper.utils;

import java.util.List;

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
      this(end1, true, end2, true);
   }

   public Segment(Point end1, boolean include1, Point end2, boolean include2)
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
   
   public Segment plus(Vector2D vector)
   {
      return new Segment(new Point(end1).plus(vector),
              new Point(end2).plus(vector));
   }

   public Segment multiply(float xFactor, float yFactor)
   {
      Point p1 = new Point(end1.getX()*xFactor, end1.getY()*yFactor);
      Point p2 = new Point(end2.getX()*xFactor, end2.getY()*yFactor);
      return new Segment(p1, p2);
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

   public Point intersectionWith(Segment other)
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
   
   public Point findMostFarPoint(List<Point> points)
   {
      float maxDist = Integer.MIN_VALUE;
      Point res = null;
      
      for (Point p : points)
      {
         float dist = distanceTo(p);
         if (dist > maxDist)
         {
            maxDist = dist;
            res = p;
         }
      }
      return res;
   }
   
   public float distanceTo(Point p)
   {
      initLineEquation();
      Vector2D pv = perpendicularToLine(p);
      Point pointOnLine = new Point(p).plus(pv);

      float res = 0;
      if (xRange.contains(pointOnLine.getX()) && yRange.contains(pointOnLine.getY()))
      {
         res = pv.length();
      }
      else
      {
         res = Math.min(p.distanceTo(getEnd1()), p.distanceTo(getEnd2()));
      }

      return res;
   }
   
   public Vector2D perpendicularToLine(Point p)
   {
      initLineEquation();

      float a2 = end2.getX() - end1.getX();
      float b2 = end2.getY() - end1.getY();
      float c2 = p.getX()*a2 + p.getY()*b2;
      
      float d = a*b2 - a2*b;
      float dx = c*b2 - c2*b;
      float dy = a*c2 - a2*c;
      
      float x = dx / d;
      float y = dy / d;
      return new Vector2D(x - p.getX(), y - p.getY());
   }

   public Point getPoint(float partOfLenPercent)
   {
      Vector2D v = new Vector2D(end1, end2);
      return new Point(end1).plus(v.setLength(partOfLenPercent*v.length()));
   }

   public Point getRandomPoint()
   {
      Vector2D v = new Vector2D(end1, end2);
      return new Point(end1).plus(v.setLength(Utils.rand.nextFloat()*v.length()));
   }

   public float length()
   {
      return end1.distanceTo(end2);
   }

}
