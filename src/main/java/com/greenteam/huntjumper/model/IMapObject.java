package com.greenteam.huntjumper.model;

import com.greenteam.huntjumper.match.MapObjectId;
import com.greenteam.huntjumper.utils.Point;

/**
 * User: GreenTea Date: 31.07.12 Time: 22:33
 */
public interface IMapObject
{
   MapObjectId getIdentifier();
   Point getPosition();
}
