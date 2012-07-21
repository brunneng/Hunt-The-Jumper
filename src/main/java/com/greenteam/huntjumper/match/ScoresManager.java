package com.greenteam.huntjumper.match;

import com.greenteam.huntjumper.model.Jumper;
import com.greenteam.huntjumper.model.JumperRole;
import com.greenteam.huntjumper.parameters.GameConstants;
import com.greenteam.huntjumper.utils.*;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.RoundedRectangle;

import java.util.*;

import static com.greenteam.huntjumper.parameters.GameConstants.*;
import static com.greenteam.huntjumper.parameters.ViewConstants.*;

/**
 * User: GreenTea Date: 20.02.12 Time: 23:05
 */
public class ScoresManager implements IGameObject
{
   private List<Jumper> jumpers;

   private float[] scores;
   private TimeAccumulator hunterForEveryoneTimeAccumulator = new TimeAccumulator(1000);
   private TimeAccumulator[] escapingTimeAccumulators;

   private TimeAccumulator minutesAccumulator = new TimeAccumulator(60000);
   private float scoresMultiplier = 1;

   private final int scoresBoxHeight;
   private List<Jumper> winners;

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
      int height = SCORES_BOX_TEXT_TOP_INDENT;
      for (int i = 0; i < jumpers.size(); ++i)
      {
         Jumper j = jumpers.get(i);
         height += SCORES_BOX_FONT.getHeight(j.getPlayerName()) + SCORES_BOX_LINE_INDENT;
      }
      height += SCORES_BOX_TEXT_TOP_INDENT - SCORES_BOX_LINE_INDENT;
      scoresBoxHeight = height;
   }

   public void update(int dt)
   {
      scoresMultiplier += minutesAccumulator.update(dt) * SCORES_GROWTH_MULTIPLIER_PER_MINUTE;

      for (int i = 0; i < jumpers.size(); ++i)
      {
         Jumper j = jumpers.get(i);
         if (j.getJumperRole().equals(JumperRole.Escaping))
         {
            int cycles = escapingTimeAccumulators[i].update(dt);
            scores[i] += cycles * scoresMultiplier * SCORES_FOR_ESCAPING_PER_SEC;
         }
         else if (j.getJumperRole().equals(JumperRole.HuntingForEveryone))
         {
            int cycles = hunterForEveryoneTimeAccumulator.update(dt);
            scores[i] += cycles * scoresMultiplier * SCORES_FOR_HUNTING_FOR_EVERYONE_PER_SEC;
         }

         if (scores[i] < 0)
         {
            scores[i] = 0;
         }
      }
   }

   public void draw(Graphics g)
   {
      int currentShift = SCORES_BOX_DIST_FROM_TOP + SCORES_BOX_TEXT_TOP_INDENT;
      final int indent = 10;

      Color boxColor = new Color(1, 1, 1, SCORES_BOX_ALPHA);
      g.setColor(boxColor);
      g.fill(new RoundedRectangle(SCORES_BOX_DIST_FROM_LEFT, SCORES_BOX_DIST_FROM_TOP,
              SCORES_BOX_RIGHT_BORDER, scoresBoxHeight, SCORES_BOX_RECT_RADIUS));
      
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
         Point bodyPos = new Point(SCORES_BOX_DIST_FROM_LEFT + SCORES_BOX_TEXT_LEFT_INDENT, currentShift +
                 SCORES_BOX_FONT.getHeight(j.getPlayerName())*0.6f);
         j.drawBody(g, bodyPos, SCORES_BOX_JUMPERS_ALPHA);
         
         Point namePos = new Point(
                 SCORES_BOX_DIST_FROM_LEFT + SCORES_BOX_TEXT_LEFT_INDENT + 4*jumperRadius, currentShift);
         TextUtils.drawText(namePos, j.getPlayerName(), Color.gray, SCORES_BOX_FONT, g);
         namePos = namePos.plus(new Vector2D(1, 0));
         TextUtils.drawText(namePos, j.getPlayerName(), Color.black, SCORES_BOX_FONT, g);

         int score = (int)scores[jumpers.indexOf(j)];
         Point scoresPos = new Point(SCORES_BOX_SCORES_POS_X, currentShift);
         TextUtils.drawText(scoresPos, "" + score, Color.gray, SCORES_BOX_FONT, g);
         scoresPos = scoresPos.plus(new Vector2D(1, 0));
         TextUtils.drawText(scoresPos, "" + score, Color.black, SCORES_BOX_FONT, g);
         
         if (j.getJumperRole().equals(JumperRole.Escaping) && winners == null)
         {
            int timeLeft = GameConstants.TIME_TO_BECOME_SUPER_HUNTER -
                    j.getTimeInCurrentRole();

            boolean show = true;
            if (timeLeft < SCORES_BOX_BACK_TIMER_START_BLINK_TIME)
            {
               show = timeLeft / SCORES_BOX_BACK_TIMER_BLINK_PERIOD % 2 == 0;
            }

            if (show)
            {
               String timeStr = Utils.getTimeString(timeLeft);
               Point backTimerPos = new Point(SCORES_BOX_BACK_TIMER_POS_X, currentShift);
               TextUtils.drawText(backTimerPos, timeStr, Color.gray, SCORES_BOX_FONT, g);
               backTimerPos = backTimerPos.plus(new Vector2D(1, 0));
               TextUtils.drawText(backTimerPos, timeStr, Color.black, SCORES_BOX_FONT, g);
            }
         }
         else if (winners != null)
         {
            if (winners.contains(j))
            {
               String timeStr = "Win!";
               Point backTimerPos = new Point(SCORES_BOX_BACK_TIMER_POS_X, currentShift);
               TextUtils.drawText(backTimerPos, timeStr, Color.gray, SCORES_BOX_FONT, g);
               backTimerPos = backTimerPos.plus(new Vector2D(1, 0));

               Random r = Utils.rand;
               Color c = new Color(r.nextFloat(), r.nextFloat(), r.nextFloat());
               TextUtils.drawText(backTimerPos, timeStr, c, SCORES_BOX_FONT, g);
            }
         }

         currentShift += SCORES_BOX_FONT.getHeight(j.getPlayerName()) + indent;
      }
   }

   public void signalCoinTaken(Jumper jumper)
   {
      scores[jumpers.indexOf(jumper)] += GameConstants.COIN_SCORES;
   }
   
   public float getScores(Jumper jumper)
   {
      return scores[jumpers.indexOf(jumper)];
   }

   public float getScoresMultiplier()
   {
      return scoresMultiplier;
   }

   public List<Jumper> calcWinners()
   {
      this.winners = new ArrayList<Jumper>();
      int maxScores = 0;
      for (Jumper j : jumpers)
      {
         int scores = (int)getScores(j);
         
         if (scores > maxScores)
         {
            maxScores = scores;
            winners.clear();
            winners.add(j);
         }
         else if (scores == maxScores)
         {
            winners.add(j);
         }
      }

      return winners;
   }
}
