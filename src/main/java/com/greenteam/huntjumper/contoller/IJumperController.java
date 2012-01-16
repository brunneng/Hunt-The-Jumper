package com.greenteam.huntjumper.contoller;

import com.greenteam.huntjumper.model.Jumper;
import org.newdawn.slick.GameContainer;

/**
 * Jumper controller
 */

public interface IJumperController
{
   void update(Jumper jumper, int delta);
}
