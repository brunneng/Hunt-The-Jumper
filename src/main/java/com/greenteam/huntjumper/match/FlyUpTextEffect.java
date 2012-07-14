package com.greenteam.huntjumper.match;

import com.greenteam.huntjumper.effects.Effect;
import com.greenteam.huntjumper.model.Jumper;
import com.greenteam.huntjumper.parameters.GameConstants;
import com.greenteam.huntjumper.utils.Point;
import com.greenteam.huntjumper.utils.TextUtils;
import com.greenteam.huntjumper.utils.Utils;
import com.greenteam.huntjumper.utils.Vector2D;
import net.phys2d.math.ROVector2f;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;

/**
 * User: GreenTea Date: 14.07.12 Time: 13:11
 */
public class FlyUpTextEffect extends Effect
{
   public static interface IGetPositionCallback
   {
      ROVector2f getPosition();
   }

   private IGetPositionCallback callback;
   private String text;
   private int duration;
   private Color textColor;
   private Font font;
   private float flyHeight;

   public FlyUpTextEffect(IGetPositionCallback callback,
                          String text, int duration, Color textColor, Font font,
                          float flyHeight)
   {
      this.callback = callback;
      this.text = text;
      this.duration = duration;
      this.textColor = textColor;
      this.font = font;
      this.flyHeight = flyHeight;
   }

   public FlyUpTextEffect(final Jumper j,
                          String text, int duration, Color textColor, Font font,
                          float flyHeight)
   {
      this(new IGetPositionCallback()
      {
         @Override
         public ROVector2f getPosition()
         {
            return j.getBody().getPosition();
         }
      }, text, duration, textColor, font, flyHeight);
   }

   @Override
   public int getDuration()
   {
      return duration;
   }

   @Override
   public void draw(Graphics g)
   {
      float ep = getExecutionPercent();

      Point pos = Camera.getCamera().toView(callback.getPosition());
      pos = pos.plus(new Vector2D(0, -(GameConstants.JUMPER_RADIUS*3 + flyHeight*ep)));

      Color c = new Color(0, 0, 0, 1 - ep);
      TextUtils.drawTextInCenter(pos, text, c, font, g);

      c = Utils.toColorWithAlpha(textColor, 1 - ep);
      pos = pos.plus(new Vector2D(1, 1));
      TextUtils.drawTextInCenter(pos, text, c, font, g);
   }
}
