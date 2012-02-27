package com.greenteam.huntjumper.utils;

import org.newdawn.slick.*;

/**
 * User: GreenTea Date: 18.02.12 Time: 22:50
 */
public final class TextUtils
{
   public static Font Arial20Font;
   public static Font Arial30Font;

   static
   {
      try
      {
         Arial20Font = new AngelCodeFont("fonts/arial20/Arial.fnt", "fonts/arial20/Arial_0.png");
         Arial30Font = new AngelCodeFont("fonts/arial30/Arial.fnt", "fonts/arial30/Arial_0.png");
      }
      catch (SlickException e)
      {
         e.printStackTrace();
      }
   }

   private TextUtils()
   {
      
   }
   
   public static void drawTextInCenter(Point center, String text, Color color, Font font,
                                       Graphics g)
   {
      Font defaultFont = g.getFont();
      if (font != null)
      {
         g.setFont(font);
      }
      
      float w = g.getFont().getWidth(text);
      float h = g.getFont().getHeight(text);

      float x = center.getX() - w/2;
      float y = center.getY() - h/2;

      if (color != null)
      {
         g.setColor(color);
      }
      g.drawString(text, x, y);
      g.setFont(defaultFont);
   }

   public static void drawText(Point pos, String text, Color color, Font font,
                                       Graphics g)
   {
      Font defaultFont = g.getFont();
      if (font != null)
      {
         g.setFont(font);
      }

      {
         g.setColor(color);
      }
      g.drawString(text, pos.getX(), pos.getY());
      g.setFont(defaultFont);
   }
}
