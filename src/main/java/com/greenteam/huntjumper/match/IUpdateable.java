package com.greenteam.huntjumper.match;

import com.greenteam.huntjumper.commands.Command;

import java.util.List;

/**
 * User: GreenTea Date: 09.03.12 Time: 13:45
 */
public interface IUpdateable
{
   List<? extends Command> update(int delta);
}
