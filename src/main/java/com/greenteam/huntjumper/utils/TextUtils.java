package com.greenteam.huntjumper.utils;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

/**
 * User: GreenTea Date: 18.02.12 Time: 22:50
 */
public final class TextUtils
{
   private TextUtils()
   {
      
   }
   
   public static void drawText(Point center, String text, Color color, Graphics g)
   {
      float w = g.getFont().getWidth(text);
      float h = g.getFont().getHeight(text);

      float x = center.getX() - w/2;
      float y = center.getY() - h/2;

      if (color != null)
      {
         g.setColor(color);
      }
      g.drawString(text, x, y);
   }
}
