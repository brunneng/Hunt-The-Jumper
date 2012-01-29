package com.greenteam.huntjumper.utils;

import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import org.apache.commons.lang.builder.EqualsBuilder;

/**
 * User: GreenTea Date: 01.01.11 Time: 18:11
 */
public class  Vector2D implements Cloneable
{
   public static Vector2D plus(Vector2D v1, Vector2D v2)
   {
      return new Vector2D(v1).plus(v2);
   }

   public static Vector2D minus(Vector2D v1, Vector2D v2)
   {
      return new Vector2D(v1).minus(v2);
   }

   public static Vector2D multiply(Vector2D v, float scale)
   {
      return new Vector2D(v).multiply(scale);
   }

   public static Vector2D rotate(Vector2D v, float angle)
   {
      return new Vector2D(v).rotate(angle);
   }

   public static Vector2D fromAngleAndLength(float angle, float length)
   {
      float angleInRadians = (float)Math.toRadians(angle);
      float x = length * (float)Math.cos(angleInRadians);
      float y = length * (float)Math.sin(angleInRadians);

      return new Vector2D(x, y);
   }

   public static Vector2D fromRadianAngleAndLength(float angleInRadians, float length)
   {
      float x = length * (float)Math.cos(angleInRadians);
      float y = length * (float)Math.sin(angleInRadians);

      return new Vector2D(x, y);
   }

   private float x;
   private float y;

   public Vector2D()
   {

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

   public Vector2D setLength(float length)
   {
      return unit().multiply(length);
   }

   public float length()
   {
      return (float)Math.sqrt(x*x + y*y);
   }

   public Vector2D unit()
   {
      float length = length();
      x /= length;
      y /= length;

      return this;
   }

   public Vector2D multiply(float scale)
   {
      x *= scale;
      y *= scale;

      return this;
   }

   public Vector2D plus(Vector2D other)
   {
      x += other.x;
      y += other.y;

      return this;
   }

   public Vector2D minus(Vector2D other)
   {
      x -= other.x;
      y -= other.y;

      return this;
   }

   public Vector2D negate()
   {
      x = -x;
      y = -y;
      
      return new Vector2D(this);
   }

   /**
    * @return angle of vector in degrees in range [0, 360)
    */
   public float angle()
   {
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

      return (float)(180 / Math.PI) * resAngle;
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

      float resAngle = a2 - a1;
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
      Vector2D rotated = fromAngleAndLength(newAngle, length());

      x = rotated.x;
      y = rotated.y;
      return this;
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
