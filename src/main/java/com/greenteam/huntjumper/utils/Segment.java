package com.greenteam.huntjumper.utils;

import java.util.List;

/**
 * User: GreenTea Date: 29.01.12 Time: 15:23
 */
public class Segment
{
   private Point end1;
   private Point end2;
   private boolean include1;
   private boolean include2;

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
      this.include1 = include1;
      this.include2 = include2;

      // some strange bugs if this values are not true.
      this.include1 = true;
      this.include2 = true;
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
      return new Segment(new Point(end1).plus(vector), include1,
              new Point(end2).plus(vector), include2);
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

      float rXstart = Math.min(x1, x2);
      boolean rXincludeStart = Utils.equals(rXstart, x1) ? include1 : include2;
      float rXend = Math.max(x1, x2);
      boolean rXincludeEnd = Utils.equals(rXend, x1) ? include1 : include2;
      xRange = new Range(rXstart, rXincludeStart, rXend, rXincludeEnd);

      float rYstart = Math.min(y1, y2);
      boolean rYincludeStart = Utils.equals(rYstart, y1) ? include1 : include2;
      float rYend = Math.max(y1, y2);
      boolean rYincludeEnd = Utils.equals(rYend, y1) ? include1 : include2;
      yRange = new Range(rYstart, rYincludeStart, rYend, rYincludeEnd);

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
      
      boolean aNil = Utils.equals(a, 0);
      boolean bNil = Utils.equals(b, 0);

      float res = -1;
      A: if (!aNil && !bNil)
      {
         float x = (c - b*p.getY()) / a;
         float y = (c - a*p.getX()) / b;
         
         Point p1 = new Point(p.getX(), y);
         Point p2 = new Point(x, p.getY());
         
         float pToP1Len = p.distanceTo(p1);
         float pToP2Len = p.distanceTo(p2);
         if (Utils.equals(pToP1Len, 0) || Utils.equals(pToP2Len, 0))
         {
            res = 0;
            break A;
         }
         
         float p1ToP2Len = p1.distanceTo(p2);
         float l = pToP1Len * pToP2Len / p1ToP2Len;
         
         float pToEnd1Len = p.distanceTo(getEnd1());
         float l1 = (float)Math.sqrt(pToEnd1Len*pToEnd1Len - l*l);
         Vector2D end1ToEnd2 = new Vector2D(getEnd1(), getEnd2()).setLength(l1);
         Point target = new Point(getEnd1()).plus(end1ToEnd2);

         float pToTargetLen = p.distanceTo(target);
         if (Utils.equals(pToTargetLen, l) &&
                 xRange.contains(target.getX()) && yRange.contains(target.getY()))
         {
            res = l;
         }
      }
      else if (aNil && !bNil)
      {
         if (xRange.contains(p.getX()))
         {
            float y = c / b;
            res = Math.abs(p.getY() - y);
         }
      }
      else if (!aNil && bNil)
      {
         if (yRange.contains(p.getY()))
         {
            float x = c / a;
            res = Math.abs(p.getX() - x);
         }
      }

      
      if (res < 0)
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

}
