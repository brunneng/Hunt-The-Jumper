package com.greenteam.huntjumper;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

/**
 * User: GreenTea Date: 03.06.12 Time: 16:32
 */
public interface IGameState
{
   void init();
   boolean isInitialized();
   void update(int delta) throws SlickException;
   void render(Graphics g) throws SlickException;
}
