package com.greenteam.huntjumper.manager;

import com.google.common.base.Predicate;
import com.greenteam.huntjumper.IVisibleObject;
import com.greenteam.huntjumper.model.Jumper;
import com.greenteam.huntjumper.parameters.GameConstants;
import com.greenteam.huntjumper.utils.Point;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Collections2.filter;
import static com.greenteam.huntjumper.Camera.*;
import static com.greenteam.huntjumper.model.JumperRole.*;
import static com.greenteam.huntjumper.utils.Vector2D.fromAngleAndLength;

/**
 * Calculates position and draws direction arrows
 * @author BlindButcher
 */

public class ArrowManager implements IVisibleObject {
   private List<Jumper> jumpers;
   private Jumper local;
   private Image arrowImage;

   public ArrowManager(List<Jumper> jumpers)
   {
      this.jumpers = jumpers;
      for (Jumper j: jumpers)
      {
         if (j.locallyControlled())
         {
            local = j;
         }
      }

      try
      {
         arrowImage = new Image("images/arrow.png", Color.white);
      }
      catch (SlickException e) {
      }

   }

   public void draw(Graphics g)
   {
      if (Hunting == local.getJumperRole())
      {
         drawArrowTo(filter(jumpers, new Predicate<Jumper>()
         {
            public boolean apply(Jumper jumper)
            {
               return jumper.getJumperRole() == Escaping &&
                       !instance().contains(jumper.getBody().getPosition());
            }
         }), g);
      }
      if (Escaping == local.getJumperRole())
      {
         drawArrowTo(filter(jumpers, new Predicate<Jumper>()
         {
            public boolean apply(Jumper jumper)
            {
               return jumper.getJumperRole() == Hunting &&
                       !instance().contains(jumper.getBody().getPosition());
            }
         }), g);
      }
   }

   private void drawArrowTo(Collection<Jumper> jumpers, Graphics g)
   {
      for (Jumper j: jumpers)
      {
         float angle = local.vectorTo(j).angle();
         Point positionOnScreen = instance().toView(local.getBody().getPosition())
                 .plus(fromAngleAndLength(angle, instance().getViewWidth() / 2 - GameConstants.CAMERA_MAX_DIST));
         arrowImage.setRotation(angle - 90);
         arrowImage.drawCentered(positionOnScreen.getX(), positionOnScreen.getY());
      }
   }

}
