package com.greenteam.huntjumper.model;

import com.greenteam.huntjumper.model.IMapObject;
import org.newdawn.slick.Color;

/**
 * User: GreenTea Date: 27.08.12 Time: 23:52
 */
public interface ILightSource extends IMapObject
{
   Color getLightColor();
   float getLightCircle();
   float getLightMaxRadius();
}
