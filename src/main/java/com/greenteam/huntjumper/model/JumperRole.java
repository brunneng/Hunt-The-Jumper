package com.greenteam.huntjumper.model;

import org.newdawn.slick.Color;

/**
 * User: GreenTea Date: 14.01.12 Time: 21:19
 */
public enum JumperRole
{
   Escaping(new Color(51, 255, 15)),
   Hunting(new Color(255, 151, 151)),
   EscapingFromHunter(new Color(171, 255, 159)),
   HuntingForEveryone(new Color(255, 0, 0));

   private Color roleColor;

   private JumperRole(Color roleColor)
   {
      this.roleColor = roleColor;
   }

   public Color getRoleColor()
   {
      return roleColor;
   }
}
