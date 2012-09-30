package com.greenteam.huntjumper.utils;

import com.greenteam.huntjumper.model.IMapObject;
import net.phys2d.raw.Body;
import org.newdawn.slick.Color;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * User: GreenTea Date: 01.01.11 Time: 18:52
 */
public final class Utils
{
   public static double ERROR = 0.0005;
   public static Random rand = new Random();

   private static long lastConsumeKeyboardEventTime;
   private static boolean prevKeyboardEventState;

   private Utils()
   {

   }

   public static boolean equals(double d1, double d2)
   {
      return Math.abs(d1 - d2) < ERROR;
   }

   public static boolean equals(float f1, float f2)
   {
      return Math.abs(f1 - f2) < ERROR;
   }

   public static List<Point> getRotationPoints(Point center, float radius, float startAngle,
                                               int pointsCount)
   {
      float angleStep = 360.0f / pointsCount;

      Vector2D v = new Vector2D(center, new Point(center.getX() + radius, center.getY()));
      v = v.rotate(startAngle);
      
      List<Point> res = new ArrayList<Point>();
      res.add(center.plus(v));
      for (int i = 1; i < pointsCount; ++i)
      {
         v = v.rotate(angleStep);
         res.add(center.plus(v));
      }

      return res;
   }
   
   public static Color randomColor()
   {
      return new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
   }

   public static boolean isBright(Color color)
   {
      int total = color.getRed() + color.getGreen() + color.getBlue() / 2;
      return total > 254;
   }
   
   public static String getTimeString(long timeInMilliseconds)
   {
      long minutes = timeInMilliseconds / 60000;
      long seconds = (timeInMilliseconds / 1000) % 60;

      String secondsStr = (seconds < 10 ? "0" : "") + seconds;
      return minutes + " : " + secondsStr;
   }

   public static Color toColorWithAlpha(Color color, float alpha)
   {
      Color res = new Color(color);
      res.a = alpha;
      return res;
   }
   
   public static float average(Collection<Float> values)
   {
      float sum = 0;
      for (float value : values)
      {
         sum += value;
      }

      return sum / values.size();
   }
   
   public static String createString(char ch, int len)
   {
      StringBuilder sb = new StringBuilder(len);
      for (int i = 0; i < len; ++i)
      {
         sb.append(ch);
      }
      return sb.toString();
   }

   public static void consumeKeyboardEvent()
   {
      lastConsumeKeyboardEventTime = System.currentTimeMillis();
   }

   public static boolean isKeyboardEnabled(boolean keyEventState)
   {
      boolean res = !prevKeyboardEventState && keyEventState ||
              System.currentTimeMillis() - lastConsumeKeyboardEventTime > 200;
      prevKeyboardEventState = keyEventState;
      return res;
   }

   public static <T extends IMapObject> T findNearest(IMapObject fromObject,
                                                      Collection<T> otherObjects)
   {
      float minDist = Float.MAX_VALUE;
      Point pos = fromObject.getPosition();
      T res = null;
      for (T mo : otherObjects)
      {
         float dist = pos.distanceTo(mo.getPosition());
         if (dist < minDist)
         {
            res = mo;
            minDist = dist;
         }
      }

      return res;
   }

   public static <T extends IMapObject> T findMostFar(IMapObject fromObject,
                                        Collection<T> otherObjects)
   {
      float maxDist = Integer.MIN_VALUE;
      Point pos = fromObject.getPosition();
      T res = null;
      for (T mo : otherObjects)
      {
         float dist = pos.distanceTo(mo.getPosition());
         if (dist > maxDist)
         {
            res = mo;
            maxDist = dist;
         }
      }

      return res;
   }

   public static <T> T getUserDataOfClass(Body body, Class<T> clazz)
   {
      Object userData = body.getUserData();
      if (userData != null)
      {
         if (!clazz.isAssignableFrom(userData.getClass()))
         {
            userData = null;
         }
      }

      return (T)userData;
   }

   public static <T> List<T> add(List<T> target, List<T> source)
   {
      List<T> res = target;
      if (source != null && source.size() > 0)
      {
         if (target == null)
         {
            res = new ArrayList<>();
         }
         else
         {
            res = new ArrayList<>(target);
         }

         res.addAll(source);
      }

      return res;
   }

}
