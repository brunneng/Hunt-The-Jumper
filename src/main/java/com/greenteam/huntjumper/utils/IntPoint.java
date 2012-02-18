package com.greenteam.huntjumper.utils;

/**
 * User: GreenTea Date: 02.02.12 Time: 19:46
 */
public class IntPoint
{
   public final int x;
   public final int y;

   public IntPoint(int x, int y)
   {
      this.x = x;
      this.y = y;
   }
   
   public IntPoint plus(Direction d)
   {
      return new IntPoint(x + d.dx, y + d.dy);
   }

   public int distanceToInCells(IntPoint other)
   {
      return Math.abs(x - other.x) + Math.abs(y - other.y);
   }

   public double distanceTo(IntPoint other)
   {
      int dx = Math.abs(x - other.x);
      int dy = Math.abs(y - other.y);
      return Math.sqrt(dx*dx + dy*dy);
   }

   public int squareDistanceTo(IntPoint other)
   {
      int dx = x - other.x;
      int dy = y - other.y;
      return dx*dx + dy*dy;
   }
   
   public Point toPoint()
   {
      return new Point(x + 0.5f, y + 0.5f);
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

      IntPoint intPoint = (IntPoint) o;

      if (x != intPoint.x)
      {
         return false;
      }
      if (y != intPoint.y)
      {
         return false;
      }

      return true;
   }

   @Override
   public int hashCode()
   {
      int result = x;
      result = 31 * result + y;
      return result;
   }

   @Override
   public String toString()
   {
      return "[" + x + "," + y + ']';
   }
}
