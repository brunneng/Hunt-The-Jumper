package com.greenteam.huntjumper.model;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

/**
 * User: GreenTea Date: 24.08.12 Time: 22:28
 */
public interface ILightproof
{
   static final Color LIGHT_FREE_COLOR = Color.white;

   void drawLightProofBody(Graphics g);
}
