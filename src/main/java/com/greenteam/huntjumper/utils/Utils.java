package com.greenteam.huntjumper.utils;

import com.greenteam.huntjumper.Camera;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import org.newdawn.slick.Input;

import java.util.Random;

/**
 * Created by IntelliJ IDEA. User: GreenTea Date: 01.01.11 Time: 18:52 To change this template use
 * File | Settings | File Templates.
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

   public static Vector2D mouseVector(Input input, Body body, Camera camera)
   {
      float mouseX = input.getMouseX();
      float mouseY = input.getMouseY();
      Point realPoint = camera.toPhys(new Vector2f(mouseX, mouseY));
      return new Vector2D((realPoint.getX() - body.getPosition().getX()),
              (realPoint.getY() - body.getPosition().getY())).unit();

   }
}
