package com.greenteam.huntjumper.utils;

import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import org.apache.commons.lang.builder.EqualsBuilder;

import java.io.Serializable;

/**
 * User: GreenTea Date: 01.01.11 Time: 18:11
 */
public class Vector2D implements Cloneable, Serializable
{
   private final float NOT_INITIALIZED_ANGLE = 10000;

   public static Vector2D fromAngleAndLength(float angle, float length)
   {
      float angleInRadians = (float)Math.toRadians(angle);
      float x = length * (float)Math.cos(angleInRadians);
      float y = length * (float)Math.sin(angleInRadians);

      Vector2D res = new Vector2D(x, y);
      res.angle = normalizeAngle(angle);
      return res;
   }

   public static Vector2D fromRadianAngleAndLength(float angleInRadians, float length)
   {
      float x = length * (float)Math.cos(angleInRadians);
      float y = length * (float)Math.sin(angleInRadians);

      Vector2D res = new Vector2D(x, y);
      res.angle = normalizeAngle((float)Math.toDegrees(angleInRadians));
      return res;
   }

   private float x;
   private float y;
   private float angle = NOT_INITIALIZED_ANGLE;

   public Vector2D()
   {
      x = 0;
      y = 0;
   }

   public Vector2D(float x, float y)
   {
      this.x = x;
      this.y = y;
   }

   public Vector2D(Vector2D other)
   {
      this.x = other.x;
      this.y = other.y;
   }

   public Vector2D(ROVector2f other)
   {
      this.x = other.getX();
      this.y = other.getY();
   }

   public Vector2D(ROVector2f start, ROVector2f end) 
   {
      this(new Point(start), new Point(end));
   }

   public Vector2D(Point start, Point end)
   {
      this.x = end.getX() - start.getX();
      this.y = end.getY() - start.getY();
   }

   public Vector2D(Point end)
   {
      this.x = end.getX();
      this.y = end.getY();
   }

   public float getX()
   {
      return x;
   }

   public float getY()
   {
      return y;
   }

   public Vector2D setLength(float length)
   {
      Vector2D res = unit().multiply(length);
      x = res.x;
      y = res.y;
      return res;
   }

   public float length()
   {
      return (float)Math.sqrt(x*x + y*y);
   }

   public Vector2D unit()
   {
      float length = length();
      return new Vector2D(x / length, y / length);
   }

   public Vector2D multiply(float scale)
   {
      return new Vector2D(x * scale, y * scale);
   }

   public Vector2D plus(Vector2D other)
   {
      return new Vector2D(x + other.x, y + other.y);
   }

   public Vector2D plus(float dx, float dy)
   {
      return new Vector2D(x + dx, y + dy);
   }

   public Vector2D minus(Vector2D other)
   {
      return this.plus(other.negate());
   }

   public Vector2D negate()
   {
      return new Vector2D(-x, -y);
   }

   /**
    * @return angle of vector in degrees in range [0, 360)
    */
   public float angle()
   {
      if (angle < NOT_INITIALIZED_ANGLE)
      {
         return angle;
      }

      float z = (float)Math.sqrt(x*x + y*y);
      float sin = y / z;
      float asin = (float)Math.asin(sin);

      float resAngle = 0;
      if (x >= 0 && y >= 0) // 1 quarter
      {
         resAngle = asin;
      }
      else if (x < 0) // 2, 3 quarters
      {
         resAngle = (float)Math.PI - asin;
      }
      else // 4 quarter
      {
         resAngle = 2*(float)Math.PI + asin;
      }

      angle = (float)(180 / Math.PI) * resAngle;
      return angle;
   }

   /**
    * @param other another vector
    * @return angle in degrees in range [-180, 180] on which current vector should be rotated
    *    to have the same direction as <b>other</b> vector.
    */
   public float angleToVector(Vector2D other)
   {
      float a1 = angle();
      float a2 = other.angle();

      return normalizeAngle(a2 - a1);
   }

   private static float normalizeAngle(float angle)
   {
      float resAngle = angle;
      if (resAngle > 180)
      {
         resAngle -= 360;
      }
      if (resAngle < -180)
      {
         resAngle += 360;
      }
      return resAngle;
   }

   public Vector2f toVector2f()
   {
      return new Vector2f(x, y);
   }

   public Vector2D rotate(float dAngle)
   {
      float newAngle = angle() + dAngle;
      return fromAngleAndLength(newAngle, length());
   }

   @Override
   public Vector2D clone()
   {
      return new Vector2D(this);
   }

    
    public boolean equals(Object o)
    {
        if(o == null || !(o instanceof Vector2D))
            return false;
        if(o==this) return true;
        Vector2D v = (Vector2D)o;
        return new EqualsBuilder().appendSuper(
                Utils.equals(getX(),v.getX())).appendSuper(
                Utils.equals(getY(),v.getY())).isEquals();
    }

    public static Vector2D fromVector2f(ROVector2f phys)
    {
      return new Vector2D(phys.getX(), phys.getY());
    }

    public String toString()
    {
        return "[x=" + x + "][y=" + y + "]";
    }
}
