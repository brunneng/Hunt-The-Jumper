package com.greenteam.huntjumper.utils;

import com.greenteam.huntjumper.Camera;
import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.shapes.Polygon;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Input;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * User: GreenTea Date: 01.01.11 Time: 18:52
 */
public final class Utils
{
   public static double ERROR = 0.0005;
   public static Random rand = new Random();

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

   public static Vector2D getPhysVectorToCursor(Body body, Point cursor, Camera camera)
   {
      Point physPoint = camera != null ?
              camera.toPhys(new Point(cursor.getX(), cursor.getY())) : cursor;
      return new Vector2D(new Point(body.getPosition()), physPoint);
   }

   public static org.newdawn.slick.geom.Polygon toViewPolygon(StaticBody b)
   {
      Polygon p = (Polygon)b.getShape();

      ROVector2f[] vertices = p.getVertices();
      float[] viewVertices = new float[vertices.length * 2];
      for (int i = 0; i < vertices.length; ++i)
      {
         ROVector2f v = vertices[i];
         Point viewPoint = Camera.instance().toView(v);
         viewVertices[2*i] = viewPoint.getX();
         viewVertices[2*i + 1] = viewPoint.getY();
      }

      return new org.newdawn.slick.geom.Polygon(
              viewVertices);
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
   
   public static String getTimeString(int timeInMilliseconds)
   {
      int minutes = timeInMilliseconds / 60000;
      int seconds = (timeInMilliseconds / 1000) % 60;

      Font font = TextUtils.Arial30Font;
      String secondsStr = (seconds < 10 ? "0" : "") + seconds;

      return minutes + " : " + secondsStr;
   }
}
