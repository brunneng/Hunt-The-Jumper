package com.greenteam.huntjumper.utils;

import org.newdawn.slick.Color;
import org.newdawn.slick.ShapeFill;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

/**
 * User: GreenTea Date: 22.01.12 Time: 16:15
 */
public class CircleGradient implements ShapeFill
{
   private Vector2f none = new Vector2f(0,0);
   float startPercent;
   Color startColor;
   float endPercent;
   Color endColor;
   float radius;

   public CircleGradient(float startPercent, Color startColor,
                          float endPercent, Color endColor,
                          float radius)
   {
      this.startPercent = startPercent;
      this.startColor = startColor;
      this.endPercent = endPercent;
      this.endColor = endColor;
      this.radius = radius;
   }

   @Override
   public Color colorAt(Shape shape, float x, float y)
   {
      return colorAt(x - shape.getCenterX(), y - shape.getCenterY());
   }

   public Color colorAt(float x, float y)
   {
      float percent = new Vector2D(x, y).length() / radius;

      if (percent < startPercent)
      {
         return startColor;
      }

      if (percent > endPercent)
      {
         return endColor;
      }

//      System.out.println(percent);
      float u = (percent - startPercent) / (endPercent - startPercent);
      float v = 1f - u;

      Color col = new Color(1,1,1,1);
      col.r = (u * endColor.r) + (v * startColor.r);
      col.b = (u * endColor.b) + (v * startColor.b);
      col.g = (u * endColor.g) + (v * startColor.g);
      col.a = (u * endColor.a) + (v * startColor.a);

      return col;
   }

   @Override
   public Vector2f getOffsetAt(Shape shape, float x, float y)
   {
      return none;
   }
}
