package com.greenteam.huntjumper.utils;

import java.util.List;

/**
 * User: GreenTea Date: 29.01.12 Time: 15:23
 */
public class Segment
{
   public static Point findIntersection(Segment s1, boolean s1IsLine, Segment s2, boolean s2IsLine)
   {
      s1.initLineEquation();
      s2.initLineEquation();

      double a1 = s1.a;
      double b1 = s1.b;
      double c1 = s1.c;
      double a2 = s2.a;
      double b2 = s2.b;
      double c2 = s2.c;

      double d = a1*b2 - a2*b1;

      if (Utils.equals(d, 0)) // lines are parallel
      {
         return null;
      }

      double dx = c1*b2 - c2*b1;
      double x = dx / d;

      double dy = a1*c2 - a2*c1;
      double y = dy / d;

      Point res = null;
      if ((s1IsLine || (s1.xRange.contains((float)x) && s1.yRange.contains((float)y))) &&
          (s2IsLine || (s2.xRange.contains((float)x) && s2.yRange.contains((float)y))))
      {
         res = new Point((float)x, (float)y);
      }

      return res;
   }

   private Point end1;
   private Point end2;

   private boolean needFindLineEquation = true;
   private double a;
   private double b;
   private double c;
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
      return new Segment(end1.plus(vector), end2.plus(vector));
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

      double x1 = end1.getX();
      double x2 = end2.getX();
      double y1 = end1.getY();
      double y2 = end2.getY();

      xRange = new Range((float)Math.min(x1, x2), (float)Math.max(x1, x2));
      yRange = new Range((float)Math.min(y1, y2), (float)Math.max(y1, y2));

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
         double d = x2 - x1;
         double da = y1 - y2;
         double dc = x2*y1 - x1*y2;
         a = da / d;
         c = dc / d;
      }

      needFindLineEquation = false;
   }

   public Point intersectionWith(Segment other)
   {
      return findIntersection(this, false, other, false);
   }
   
   public Point findMostFarPoint(List<Point> points)
   {
      double maxDist = Integer.MIN_VALUE;
      Point res = null;
      
      for (Point p : points)
      {
         double dist = distanceTo(p);
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
      Point pointOnLine = p.plus(pv);

      double res = 0;
      if (xRange.contains(pointOnLine.getX()) && yRange.contains(pointOnLine.getY()))
      {
         res = pv.length();
      }
      else
      {
         res = Math.min(p.distanceTo(getEnd1()), p.distanceTo(getEnd2()));
      }

      return (float)res;
   }
   
   public Vector2D perpendicularToLine(Point p)
   {
      initLineEquation();

      double a2 = end2.getX() - end1.getX();
      double b2 = end2.getY() - end1.getY();
      double c2 = p.getX()*a2 + p.getY()*b2;
      
      double d = a*b2 - a2*b;
      double dx = c*b2 - c2*b;
      double dy = a*c2 - a2*c;
      
      double x = dx / d;
      double y = dy / d;
      return new Vector2D((float)x - p.getX(), (float)y - p.getY());
   }

   public Point getPoint(double partOfLenPercent)
   {
      Vector2D v = new Vector2D(end1, end2);
      return end1.plus(v.setLength((float)partOfLenPercent*v.length()));
   }

   public Point getRandomPoint()
   {
      Vector2D v = new Vector2D(end1, end2);
      return end1.plus(v.setLength(Utils.rand.nextFloat()*v.length()));
   }

   public double length()
   {
      return end1.distanceTo(end2);
   }

}
