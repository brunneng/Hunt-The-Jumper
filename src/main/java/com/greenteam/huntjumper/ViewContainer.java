package com.greenteam.huntjumper;

import com.greenteam.huntjumper.utils.ViewConstants;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

/**
 * Created by IntelliJ IDEA. User: GreenTea Date: 13.01.12 Time: 22:37 To change this template use
 * File | Settings | File Templates.
 */
public class ViewContainer extends AppGameContainer
{
   public ViewContainer(HuntJumperGame game) throws SlickException
   {
      super(game, ViewConstants.VIEW_WIDTH, ViewConstants.VIEW_HEIGHT, false);
   }

   public static void main(String[] args) throws SlickException
   {
      new ViewContainer(new HuntJumperGame()).start();
   }

}
