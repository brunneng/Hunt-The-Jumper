package com.greenteam.huntjumper;

import com.greenteam.huntjumper.model.Jumper;
import com.greenteam.huntjumper.model.JumperRole;
import com.greenteam.huntjumper.utils.GameConstants;
import com.greenteam.huntjumper.utils.Point;
import com.greenteam.huntjumper.utils.TextUtils;
import com.greenteam.huntjumper.utils.Vector2D;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.RoundedRectangle;

import static com.greenteam.huntjumper.utils.ViewConstants.*;

import static com.greenteam.huntjumper.utils.GameConstants.SCORES_GROWTH_MULTIPLIER_PER_MINUTE;
import static com.greenteam.huntjumper.utils.GameConstants.SCORES_FOR_ESCAPING_PER_SEC;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * User: GreenTea Date: 20.02.12 Time: 23:05
 */
public class ScoresManager implements IVisibleObject
{
   private List<Jumper> jumpers;

   private float[] scores;
   private TimeAccumulator[] escapingTimeAccumulators;

   private TimeAccumulator minutesAccumulator = new TimeAccumulator(60000);
   private float scoresMultiplier = 1;

   private final int scoresBoxHeight;

   public ScoresManager(List<Jumper> jumpers)
   {
      this.jumpers = jumpers;
      int size = jumpers.size();
      scores = new float[size];
      escapingTimeAccumulators = new TimeAccumulator[size];
      for (int i = 0; i < size; ++i)
      {
         escapingTimeAccumulators[i] = new TimeAccumulator(1000);
      }

      // visualization
      int height = scoresBoxTextTopIndent;
      for (int i = 0; i < jumpers.size(); ++i)
      {
         Jumper j = jumpers.get(i);
         height += scoresBoxFont.getHeight(j.getPlayerName()) + scoresBoxLineIndent;
      }
      height += scoresBoxTextTopIndent - scoresBoxLineIndent;
      scoresBoxHeight = height;
   }

   public void updateScores(int dt)
   {
      scoresMultiplier += minutesAccumulator.cycles(dt) * SCORES_GROWTH_MULTIPLIER_PER_MINUTE;

      for (int i = 0; i < jumpers.size(); ++i)
      {
         Jumper j = jumpers.get(i);
         if (j.getJumperRole().equals(JumperRole.Escaping))
         {
            int cycles = escapingTimeAccumulators[i].cycles(dt);
            scores[i] += cycles * scoresMultiplier * SCORES_FOR_ESCAPING_PER_SEC;
         }
      }
   }
   
   public void draw(Graphics g)
   {
      int currentShift = scoresBoxDistFromTop + scoresBoxTextTopIndent;
      final int indent = 10;

      Color boxColor = new Color(1, 1, 1, scoresBoxAlpha);
      g.setColor(boxColor);
      g.fill(new RoundedRectangle(scoresBoxDistFromLeft, scoresBoxDistFromTop,
              scoresBoxRightBorder, scoresBoxHeight, scoresBoxRectRadius));
      
      List<Jumper> sortedJumpers = new ArrayList<Jumper>(jumpers);
      Collections.sort(sortedJumpers, new Comparator<Jumper>()
      {
         @Override
         public int compare(Jumper o1, Jumper o2)
         {
            Float s1 = scores[jumpers.indexOf(o1)];
            Float s2 = scores[jumpers.indexOf(o2)];
            int res = -s1.compareTo(s2);
            if (res == 0)
            {
               res = o1.getPlayerName().compareTo(o2.getPlayerName());
            }
            return res;
         }
      });

      float jumperRadius = GameConstants.JUMPER_RADIUS;
      for (Jumper j : sortedJumpers)
      {
         Point bodyPos = new Point(scoresBoxDistFromLeft + scoresBoxTextLeftIndent, currentShift +
                 scoresBoxFont.getHeight(j.getPlayerName())*0.6f);
         j.drawBody(g, bodyPos, scoresBoxJumpersAlpha);
         
         Point namePos = new Point(
                 scoresBoxDistFromLeft + scoresBoxTextLeftIndent + 4*jumperRadius, currentShift);
         TextUtils.drawText(namePos, j.getPlayerName(), Color.gray, scoresBoxFont, g);
         namePos = namePos.plus(new Vector2D(1, 0));
         TextUtils.drawText(namePos, j.getPlayerName(), Color.black, scoresBoxFont, g);

         int score = (int)scores[jumpers.indexOf(j)];
         Point scoresPos = new Point(scoresBoxScoresPosX, currentShift);
         TextUtils.drawText(scoresPos, "" + score, Color.gray, scoresBoxFont, g);
         scoresPos = scoresPos.plus(new Vector2D(1, 0));
         TextUtils.drawText(scoresPos, "" + score, Color.black, scoresBoxFont, g);

         currentShift += scoresBoxFont.getHeight(j.getPlayerName()) + indent;
      }
   }
   
   public float getScores(Jumper jumper)
   {
      return scores[jumpers.indexOf(jumper)];
   }

   public float getScoresMultiplier()
   {
      return scoresMultiplier;
   }
}
