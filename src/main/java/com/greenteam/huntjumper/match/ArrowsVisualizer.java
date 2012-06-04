package com.greenteam.huntjumper.match;

import com.google.common.base.Predicate;
import com.greenteam.huntjumper.model.Jumper;
import com.greenteam.huntjumper.parameters.ViewConstants;
import com.greenteam.huntjumper.utils.Point;
import com.greenteam.huntjumper.utils.Segment;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Collections2.filter;

/**
 * Calculates position and draws direction arrows
 *
 * @author BlindButcher
 */

public class ArrowsVisualizer implements IVisibleObject
{
   private static Image arrowImage;

   private static void init()
   {
      if (arrowImage != null)
      {
         return;
      }

      try
      {
         arrowImage = new Image("images/arrow.png", Color.white);
      }
      catch (SlickException e)
      {
      }
   }

   private List<Jumper> jumpers;
   private Jumper myJumper;
   List<Segment> boundaries;

   public ArrowsVisualizer(Jumper myJumper, List<Jumper> jumpers)
   {
      this.myJumper = myJumper;
      this.jumpers = jumpers;
   }

   public void draw(Graphics g)
   {
      init();
      drawArrowTo(filter(jumpers, new Predicate<Jumper>()
      {
         public boolean apply(Jumper jumper)
         {
            return !jumper.getJumperRole().equals(myJumper.getJumperRole()) &&
                    !Camera.getCamera().contains(jumper.getBody().getPosition());
         }
      }), g);
   }

   private void drawArrowTo(Collection<Jumper> jumpers, Graphics g)
   {
      Camera c = Camera.getCamera();
      if (boundaries == null)
      {
         final int indent = ViewConstants.arrowIndentFromBoundaries;
         Point p1 = new Point(indent, indent);
         Point p2 = new Point(c.getViewWidth() - indent, indent);
         Point p3 = new Point(c.getViewWidth() - indent, c.getViewHeight() - indent);
         Point p4 = new Point(indent, c.getViewHeight() - indent);

         boundaries = new ArrayList<Segment>(4);
         boundaries.add(new Segment(p1, p2));
         boundaries.add(new Segment(p2, p3));
         boundaries.add(new Segment(p3, p4));
         boundaries.add(new Segment(p4, p1));
      }

      for (Jumper j : jumpers)
      {
         Segment lineToJumper = new Segment(
                 c.toView(myJumper.getBody().getPosition()), 
                 c.toView(j.getBody().getPosition()));
         
         for (Segment b : boundaries)
         {
            Point intersection = lineToJumper.intersectionWith(b);
            if (intersection != null)
            {
               float angle = myJumper.vectorTo(j).angle();
               arrowImage.setRotation(angle - 90);
               arrowImage.drawCentered(intersection.getX(), intersection.getY());
               int w = arrowImage.getWidth();
               int h = arrowImage.getHeight();
               g.drawImage(arrowImage, intersection.getX() - w/2, intersection.getY() - h/2,
                       j.getJumperRole().getRoleColor());
               break;
            }
         }
      }
   }

}
