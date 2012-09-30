package com.greenteam.huntjumper.contoller;

import com.greenteam.huntjumper.commands.Command;
import com.greenteam.huntjumper.model.Jumper;
import org.newdawn.slick.GameContainer;

import java.util.List;

/**
 * Jumper controller
 */
public interface IJumperController
{
   List<? extends Command> update(Jumper jumper, int delta);
}
