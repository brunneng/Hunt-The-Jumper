package com.greenteam.huntjumper.utils;

import com.greenteam.huntjumper.Camera;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import org.newdawn.slick.Input;

import java.util.Random;

/**
 * User: GreenTea Date: 01.01.11 Time: 18:52
 */
public final class Utils
{
   public static double ERROR = 0.00002;
   public static Random rand = new Random();

   private Utils()
   {

   }

   public static boolean equals(double d1, double d2)
   {
      return Math.abs(d1 - d2) < ERROR;
   }

   public static Vector2D getPhysVectorFromBodyToCursor(Body body, Input input, Camera camera)
   {
      float mouseX = input.getMouseX();
      float mouseY = input.getMouseY();
      Point realPoint = camera.toPhys(new Point(mouseX, mouseY));
      return new Vector2D(new Point(body.getPosition()), realPoint);
   }
}
