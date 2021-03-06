package com.greenteam.huntjumper.utils;

import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: GreenTea Date: 01.01.11 Time: 17:40
 */
public class Point implements Cloneable, Serializable
{
   public static Point plus(Point p, Vector2D v)
   {
      Point res = new Point(p);
      return res.plus(v);
   }

   public static List<ROVector2f> toVector2f(Collection<Point> points)
   {
      List<ROVector2f> res = new ArrayList<ROVector2f>();
      for (Point p : points)
      {
         res.add(p.toVector2f());
      }

      return res;
   }
   
   private float x;
   private float y;

   public Point()
   {

   }

   public Point(float x, float y)
   {
      this.x = x;
      this.y = y;
   }

   public Point(Point other)
   {
      this.x = other.x;
      this.y = other.y;
   }

   public Point(ROVector2f other)
   {
      this.x = other.getX();
      this.y = other.getY();
   }

   public float getX()
   {
      return x;
   }

   public void setX(float x)
   {
      this.x = x;
   }

   public float getY()
   {
      return y;
   }

   public void setY(float y)
   {
      this.y = y;
   }

   public Point plus(Vector2D vector)
   {
      return new Point(x + vector.getX(), y + vector.getY());
   }

   public float distanceTo(Point other)
   {
      float dx = (x - other.x);
      float dy = (y - other.y);
      return (float)Math.sqrt(dx*dx + dy*dy);
   }

   public Vector2f toVector2f()
   {
      return new Vector2f(x, y);
   }

   @Override
   public Point clone()
   {
      return new Point(this);
   }

   @Override
   public boolean equals(Object o)
   {
      if (this == o)
      {
         return true;
      }
      if (o == null || getClass() != o.getClass())
      {
         return false;
      }

      Point point = (Point) o;

      if (!Utils.equals(point.x, x))
      {
         return false;
      }
      if (!Utils.equals(point.y, y))
      {
         return false;
      }

      return true;
   }
   
   public List<Point> inRange(List<Point> otherPoints, float range)
   {
      List<Point> res = new ArrayList<Point>();
      for (Point p : otherPoints)
      {
         if (distanceTo(p) < range)
         {
            res.add(p);
         }
      }
      return res;
   }

   public Point findNearestPoint(List<Point> otherPoints)
   {
      Point res = null;
      float minDist = Integer.MAX_VALUE;
      for (int i = 0; i < otherPoints.size(); ++i)
      {
         Point p = otherPoints.get(i);
         float dist = distanceTo(p);
         if (dist < minDist)
         {
            res = p;
            minDist = dist;
         }
      }
      return res;
   }

   @Override
   public int hashCode()
   {
      int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
      result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
      return result;
   }

   public String toString()
   {
      return "[x=" + new DecimalFormat("#.##").format(x) + "][y=" +
              new DecimalFormat("#.##").format(y) + "]";
   }
}
