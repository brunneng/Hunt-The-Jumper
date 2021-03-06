package com.greenteam.huntjumper.commands;

import com.greenteam.huntjumper.IMatch;
import com.greenteam.huntjumper.match.MapObjectId;
import com.greenteam.huntjumper.model.IMapObject;

/**
 * User: GreenTea Date: 23.09.12 Time: 12:57
 */
public interface ICommandExecutionContext
{
   int getCurrentGameTime();
   <T extends IMapObject> T getMapObject(MapObjectId identifier);
   void removeMapObject(MapObjectId identifier);
   void addMapObject(IMapObject mapObject);
   IMatch getMatch();
}
