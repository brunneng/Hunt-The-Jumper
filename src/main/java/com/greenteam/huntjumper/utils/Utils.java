package com.greenteam.huntjumper.utils;

/**
 * Created by IntelliJ IDEA. User: GreenTea Date: 01.01.11 Time: 18:52 To change this template use
 * File | Settings | File Templates.
 */
public final class Utils
{
   public static double ERROR = 0.000001;

   private Utils()
   {

   }

   public static boolean equals(double d1, double d2)
   {
      return Math.abs(d1 - d2) < ERROR;
   }
}
