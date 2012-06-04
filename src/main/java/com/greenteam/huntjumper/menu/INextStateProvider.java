package com.greenteam.huntjumper.menu;

import com.greenteam.huntjumper.IGameState;

/**
 * User: GreenTea Date: 04.06.12 Time: 11:45
 */
public interface INextStateProvider<T>
{
   IGameState getNextState(T parent);
}
