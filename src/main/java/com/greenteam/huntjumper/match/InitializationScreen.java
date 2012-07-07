package com.greenteam.huntjumper.match;

import com.greenteam.huntjumper.HuntJumperGame;
import com.greenteam.huntjumper.parameters.ViewConstants;
import com.greenteam.huntjumper.utils.Point;
import com.greenteam.huntjumper.utils.TextUtils;
import com.greenteam.huntjumper.utils.Utils;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
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
   private int percentPartLength = 0;
   
   private String text;
   private int dotsCount;

   TimeAccumulator dotsTimeAccumulator = new TimeAccumulator(ViewConstants.INIT_SCREEN_DOT_ADD_TIME);

   private InitializationScreen()
   {
      setStatus("Initialization", null);
   }

   public void setStatus(String status)
   {
      setStatus(status, null);
   }

   public void setStatus(String status, Integer percent)
   {
      this.status = status;
      this.percent = percent;
      
      text = status;
      if (percent != null)
      {
         String percentStr = percent.toString() + "%";
         text += " " + percentStr;
         percentPartLength = ViewConstants.INIT_SCREEN_FONT.getWidth(Utils.createString('_',
                 ViewConstants.INIT_SCREEN_MAX_PERCENT_STR_LEN));
      }
      else
      {
         String dots = Utils.createString(' ', ViewConstants.INIT_SCREEN_MAX_DOTS_COUNT);
         dotsCount = 0;
         text += dots;
         dotsTimeAccumulator.reset();
         percentPartLength = 0;
      }
   }

   @Override
   public void update(int delta)
   {
      if (percent == null)
      {
         int newDotsCount = (dotsCount + dotsTimeAccumulator.update(delta)) % (
                 ViewConstants.INIT_SCREEN_MAX_DOTS_COUNT + 1);
         if (dotsCount != newDotsCount)
         {
            dotsCount = newDotsCount;
            String dots = Utils.createString('.', newDotsCount);
            text = status + dots;
         }
      }
   }

   @Override
   public void draw(Graphics g)
   {
      GameContainer gameContainer = HuntJumperGame.getInstance().getGameContainer();

      Font font = ViewConstants.INIT_SCREEN_FONT;

      Point pos = new Point(gameContainer.getWidth()/2 -
              (font.getWidth(status) + percentPartLength)/2, gameContainer.getHeight()/2);
      TextUtils.drawText(pos, text, Color.white, font, g);
   }
}
