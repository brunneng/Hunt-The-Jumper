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
   
   public float distanceTo(Point p)
   {
      initLineEquation();
      
      boolean aNil = Utils.equals(a, 0);
      boolean bNil = Utils.equals(b, 0);

      float res = -1;
      if (!aNil && !bNil)
      {
         float x = (c - b*p.getY()) / a;
         float y = (c - a*p.getX()) / b;
         
         Point p1 = new Point(p.getX(), y);
         Point p2 = new Point(x, p.getY());
         
         float pToP1Len = p.distanceTo(p1);
         float pToP2Len = p.distanceTo(p2);
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
