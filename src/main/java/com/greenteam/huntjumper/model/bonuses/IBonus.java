package com.greenteam.huntjumper.model.bonuses;

import com.greenteam.huntjumper.IGameState;
import com.greenteam.huntjumper.IMatch;
import com.greenteam.huntjumper.match.IGameObject;
import com.greenteam.huntjumper.model.IMapObject;
import com.greenteam.huntjumper.model.Jumper;
import com.greenteam.huntjumper.utils.Point;
import net.phys2d.raw.Body;

/**
 * User: GreenTea Date: 17.07.12 Time: 22:01
 */
public interface IBonus extends IGameObject, IMapObject
{
   void onBonusTaken(IMatch match, Jumper jumper, int timePassed);
   void revertTakingBonus(IMatch match, Jumper jumper);
}
