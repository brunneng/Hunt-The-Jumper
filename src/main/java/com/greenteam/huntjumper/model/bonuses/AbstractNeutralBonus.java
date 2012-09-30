package com.greenteam.huntjumper.model.bonuses;

import com.greenteam.huntjumper.IMatch;
import com.greenteam.huntjumper.commands.Command;
import com.greenteam.huntjumper.commands.MoveCommand;
import com.greenteam.huntjumper.model.IMapObject;
import com.greenteam.huntjumper.model.Jumper;
import com.greenteam.huntjumper.model.JumperInfo;
import com.greenteam.huntjumper.parameters.GameConstants;
import com.greenteam.huntjumper.utils.Utils;
import com.greenteam.huntjumper.utils.Vector2D;
import org.newdawn.slick.Graphics;

import java.util.Arrays;
import java.util.List;

/**
 * User: GreenTea Date: 24.07.12 Time: 21:02
 */
public abstract class AbstractNeutralBonus extends AbstractPhysBonus
{
   public AbstractNeutralBonus(float acceleration)
   {
      super(acceleration);
   }

   @Override
   public List<? extends Command> update(int delta)
   {
      JumperInfo mostFar = Utils.findMostFar(this, world.getJumpers());
      Vector2D force = new Vector2D(getBody().getPosition(), mostFar.position.toVector2f());

      force.setLength(acceleration *getBody().getMass());
      return Arrays.asList(new MoveCommand(getIdentifier(), force));
   }
}
