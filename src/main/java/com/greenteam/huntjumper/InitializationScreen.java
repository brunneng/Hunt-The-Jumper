package com.greenteam.huntjumper;

import com.greenteam.huntjumper.utils.Point;
import com.greenteam.huntjumper.utils.TextUtils;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

/**
 * User: GreenTea Date: 11.03.12 Time: 15:58
 */
public class InitializationScreen implements IGameObject
{
   private static InitializationScreen instance = new InitializationScreen();
   public static InitializationScreen getInstance()
   {
      return instance;
   }

   private String status;
   private Integer percent;
   
   private String text;
   private int dotsCount;

   TimeAccumulator dotsTimeAccumulator = new TimeAccumulator(1000);
   final int MAX_DOTS_COUNT = 3;
   final int MAX_PERCENT_STR_LEN = 4;

   private InitializationScreen()
   {
      setStatus("Initialization", null);
   }
   
   public void setStatus(String status, Integer percent)
   {
      this.status = status;
      this.percent = percent;
      
      text = status;
      if (percent != null)
      {
         String percentStr = percent.toString() + "%";
         for (int i = 0; i < MAX_PERCENT_STR_LEN - percentStr.length(); ++i)
         {
            percentStr += " ";
         }

         text += " " + percentStr;
      }
      else
      {
         String dots = "   ";
         dotsCount = 0;
         text += dots;
         dotsTimeAccumulator.reset();
      }
   }

   @Override
   public void update(int delta)
   {
      if (percent == null)
      {
         int newDotsCount = (dotsCount + dotsTimeAccumulator.update(delta)) % (MAX_DOTS_COUNT + 1);
         if (dotsCount != newDotsCount)
         {
            dotsCount = newDotsCount;
            String dots = "";
            for (int i = 0; i < newDotsCount; ++i)
            {
               dots += ".";
            }
            for (int i = 0; i < MAX_DOTS_COUNT - newDotsCount; ++i)
            {
               dots += " ";
            }
            text = status + dots;
         }
      }
   }

   @Override
   public void draw(Graphics g)
   {
      GameContainer gameContainer = HuntJumperGame.getInstance().getGameContainer();
      Point pos = new Point(gameContainer.getWidth()/2, gameContainer.getHeight()/2);
      
      TextUtils.drawTextInCenter(pos, text, Color.white, TextUtils.Arial30Font, g);
   }
}
