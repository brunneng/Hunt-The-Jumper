package com.greenteam.huntjumper;

import com.greenteam.huntjumper.match.ScoresManager;
import com.greenteam.huntjumper.model.Jumper;

/**
 * User: GreenTea Date: 17.07.12 Time: 22:21
 */
public interface IMatch
{
   Jumper getMyJumper();
   ScoresManager getScoresManager();
}
