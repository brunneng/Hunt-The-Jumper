package com.greenteam.huntjumper.match;

import com.greenteam.huntjumper.AbstractGameState;
import com.greenteam.huntjumper.IMatch;

/**
 * User: GreenTea Date: 17.07.12 Time: 22:22
 */
public abstract class AbstractMatchState extends AbstractGameState implements IMatch
{
   protected ScoresManager scoresManager;

   public ScoresManager getScoresManager()
   {
      return scoresManager;
   }
}
