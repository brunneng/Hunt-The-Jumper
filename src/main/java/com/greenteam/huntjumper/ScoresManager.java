package com.greenteam.huntjumper;

import com.greenteam.huntjumper.model.Jumper;
import com.greenteam.huntjumper.model.JumperRole;

import static com.greenteam.huntjumper.utils.GameConstants.SCORES_GROWTH_MULTIPLIER_PER_MINUTE;
import static com.greenteam.huntjumper.utils.GameConstants.SCORES_FOR_ESCAPING_PER_SEC;

import java.util.List;

/**
 * User: GreenTea Date: 20.02.12 Time: 23:05
 */
public class ScoresManager
{
   private List<Jumper> jumpers;
   
   private float[] scores;
   private TimeAccumulator[] escapingTimeAccumulators;

   private TimeAccumulator minutesAccumulator = new TimeAccumulator(60000);
   private float scoresMultiplier = 1;

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
   
   public float getScores(Jumper jumper)
   {
      return scores[jumpers.indexOf(jumper)];
   }

   public float getScoresMultiplier()
   {
      return scoresMultiplier;
   }
}
