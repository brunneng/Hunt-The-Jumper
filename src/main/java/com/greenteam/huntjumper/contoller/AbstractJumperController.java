package com.greenteam.huntjumper.contoller;

import com.greenteam.huntjumper.commands.Command;
import com.greenteam.huntjumper.commands.MoveCommand;
import com.greenteam.huntjumper.model.Jumper;
import com.greenteam.huntjumper.model.parameters.IParametersUser;
import com.greenteam.huntjumper.model.parameters.Parameter;
import com.greenteam.huntjumper.model.parameters.ParameterType;
import com.greenteam.huntjumper.model.parameters.ParametersHolder;
import com.greenteam.huntjumper.utils.Point;
import com.greenteam.huntjumper.utils.Vector2D;
import net.phys2d.raw.Body;

import java.util.Arrays;
import java.util.List;

import static com.greenteam.huntjumper.parameters.GameConstants.*;
import static com.greenteam.huntjumper.utils.Vector2D.fromVector2f;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.String.format;

/**
 * User: GreenTea Date: 22.01.12 Time: 12:07
 */
public abstract class AbstractJumperController implements IJumperController, IParametersUser
{
   private int accumulatedImpulseTime;
   public List<Point> lastShortestPath = null;

   public float getAccumulatedImpulseTime()
   {
      return accumulatedImpulseTime;
   }

   private void incrementImpulseTime(int delta)
   {
      accumulatedImpulseTime += delta;

      if (accumulatedImpulseTime > MAX_IMPULSE_ACCUMULATION_TIME)
      {
         accumulatedImpulseTime = MAX_IMPULSE_ACCUMULATION_TIME;
      }
   }

   protected void resetImpulse(int delta)
   {
      accumulatedImpulseTime = delta;
   }

   protected abstract Move makeMove(Jumper jumper, int delta);

   @Override
   public void prepareParameters(ParametersHolder parametersHolder)
   {
      parametersHolder.addParameter(new Parameter<>(
              ParameterType.ACCELERATION_COMMON_FACTOR,
              DEFAULT_JUMPER_ACCELERATION_COMMON_FACTOR));
      parametersHolder.addParameter(new Parameter<>(
              ParameterType.ACCELERATION_ANGLE_COEF_FACTOR,
              DEFAULT_JUMPER_ACCELERATION_ANGLE_COEF_FACTOR));
      parametersHolder.addParameter(new Parameter<>(
              ParameterType.ACCELERATION_SPEED_COEF_FACTOR,
              DEFAULT_JUMPER_ACCELERATION_SPEED_COEF_FACTOR));
   }

   public List<? extends Command> update(Jumper jumper, int delta)
   {
      Move move = makeMove(jumper, delta);
      Vector2D forceDirection = new Vector2D(move.forceDirection);

      if (move.accumulating)
      {
         incrementImpulseTime(delta);
         return null;
      }

      final Body body = jumper.getBody();

      float scale = (Float)jumper.getParameterValue(ParameterType.ACCELERATION_COMMON_FACTOR)*
              DEFAULT_FORCE_SCALE * accumulatedImpulseTime;
      Vector2D velocity = fromVector2f(body.getVelocity());

      float angleCoef = 1f;
      float speedCoef = 1f;
      if (velocity.length() > 0 && forceDirection.length() > 0)
      {
         angleCoef = 1 + 0.5f*(abs(velocity.angleToVector(forceDirection)) / 180f);
         angleCoef = (Float)jumper.getParameterValue(ParameterType.ACCELERATION_ANGLE_COEF_FACTOR)*
                 angleCoef*angleCoef;

         speedCoef = 1 + Math.min(velocity.length() / SPEED_DIVISOR, 1f);
         speedCoef = (Float)jumper.getParameterValue(ParameterType.ACCELERATION_SPEED_COEF_FACTOR)*
                 speedCoef*speedCoef;
      }
      scale *= angleCoef*speedCoef;

      resetImpulse(delta);

      forceDirection.setLength(scale);

      return Arrays.asList(new MoveCommand(jumper.getIdentifier(), forceDirection));
   }
}
