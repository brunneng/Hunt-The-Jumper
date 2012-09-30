package com.greenteam.huntjumper.model.bonuses;

import com.greenteam.huntjumper.commands.Command;
import com.greenteam.huntjumper.commands.MoveCommand;
import com.greenteam.huntjumper.model.JumperInfo;
import com.greenteam.huntjumper.utils.Utils;
import com.greenteam.huntjumper.utils.Vector2D;

import java.util.Arrays;
import java.util.List;

/**
 * User: GreenTea Date: 31.07.12 Time: 22:17
 */
public abstract class AbstractNegativeBonus extends AbstractPhysBonus
{
   public AbstractNegativeBonus(float acceleration)
   {
      super(acceleration);
   }

   @Override
   public List<? extends Command> update(int delta)
   {
      JumperInfo nearest = Utils.findNearest(this, world.getJumpers());
      Vector2D force = new Vector2D(getBody().getPosition(), nearest.getPosition().toVector2f());

      force.setLength(acceleration * getBody().getMass());
      return Arrays.asList(new MoveCommand(getIdentifier(), force));
   }
}
