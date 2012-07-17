package com.greenteam.huntjumper.model.bonuses;

import com.greenteam.huntjumper.IGameState;
import com.greenteam.huntjumper.IMatch;
import com.greenteam.huntjumper.match.IGameObject;
import com.greenteam.huntjumper.model.Jumper;
import net.phys2d.raw.Body;

/**
 * User: GreenTea Date: 17.07.12 Time: 22:01
 */
public interface IBonus extends IGameObject
{
   void onBonusTaken(IMatch match, Jumper jumper);
}
