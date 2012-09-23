package com.greenteam.huntjumper.model;

import com.greenteam.huntjumper.match.MapObjectId;
import com.greenteam.huntjumper.utils.Point;
import net.phys2d.raw.Body;

/**
 * User: GreenTea Date: 31.07.12 Time: 22:33
 */
public interface IMapObject
{
   MapObjectId getIdentifier();
   Point getPosition();
   Body getBody();
}
