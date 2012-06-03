package com.greenteam.huntjumper;

/**
 * User: GreenTea Date: 03.06.12 Time: 16:38
 */
public abstract class AbstractGameState implements IGameState
{
   protected boolean initialized = false;

   @Override
   public boolean isInitialized()
   {
      return initialized;
   }
}
