package com.greenteam.huntjumper.model.bonuses;

import com.greenteam.huntjumper.match.IGameObject;
import com.greenteam.huntjumper.model.Jumper;

/**
 * User: GreenTea Date: 19.07.12 Time: 22:49
 */
public interface IJumperBonusEffect extends IGameObject
{
   int getDuration();
   void onStartEffect(Jumper jumper);
   void onEndEffect(Jumper jumper);
}
