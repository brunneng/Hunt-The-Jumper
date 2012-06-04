package com.greenteam.huntjumper.menu;

import com.greenteam.huntjumper.AbstractGameState;
import com.greenteam.huntjumper.HuntJumperGame;
import com.greenteam.huntjumper.IGameState;
import com.greenteam.huntjumper.utils.Point;
import com.greenteam.huntjumper.utils.TextUtils;
import com.greenteam.huntjumper.utils.Utils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;

import java.util.ArrayList;
import java.util.List;

/**
 * User: GreenTea Date: 04.06.12 Time: 11:27
 */
public class ScreenMenu extends AbstractGameState
{
   private final String name;
   private List<ScreenMenu> items = new ArrayList<ScreenMenu>();
   private int selectedItemIndex = 0;
   private INextStateProvider<ScreenMenu> nextStateProvider;

   private boolean active;
   private IGameState nextState;
   private List<Rectangle> itemsRectangles = new ArrayList<Rectangle>();

   public ScreenMenu()
   {
      this("");
   }

   public ScreenMenu(String name)
   {
      this.name = name;
   }

   public ScreenMenu(String name, List<ScreenMenu> items)
   {
      this(name);
      this.items = items;
   }

   public ScreenMenu(String name, INextStateProvider<ScreenMenu> nextStateProvider)
   {
      this(name);
      this.nextStateProvider = nextStateProvider;
   }

   protected void initNextState()
   {
      if (nextState == null)
      {
         if (nextStateProvider != null)
         {
            nextState = nextStateProvider.getNextState(this);
            if (!nextState.isInitialized())
            {
               nextState.init();
            }
         }
      }
   }

   private ScreenMenu getSelectedMenuItem()
   {
      return selectedItemIndex < items.size() ? items.get(selectedItemIndex) : null;
   }

   private Rectangle getSelectedMenuItemRectangle()
   {
      return selectedItemIndex < itemsRectangles.size() ?
              itemsRectangles.get(selectedItemIndex) : null;
   }

   @Override
   public void init()
   {
      initialized = true;
   }

   @Override
   public void update(int delta) throws SlickException
   {
      ScreenMenu selectedItem = getSelectedMenuItem();
      if (selectedItem != null)
      {
         if (selectedItem.isActive())
         {
            selectedItem.update(delta);
            return;
         }
      }
      else if (active)
      {
         initNextState();
         if (nextState != null)
         {
            nextState.update(delta);
            return;
         }
      }

      boolean keyEvent = Utils.isKeyboardEnabled(Keyboard.getEventKeyState());

      int dWheel = Mouse.hasWheel() ? Mouse.getDWheel() : 0;

      if ((keyEvent && Keyboard.isKeyDown(Keyboard.KEY_DOWN)) || dWheel < 0)
      {
         selectedItemIndex = Math.min(selectedItemIndex + 1, items.size() - 1);
         Utils.consumeKeyboardEvent();
      }
      else if ((keyEvent && Keyboard.isKeyDown(Keyboard.KEY_UP)) || dWheel > 0)
      {
         selectedItemIndex = Math.max(selectedItemIndex - 1, 0);
         Utils.consumeKeyboardEvent();
      }
      else if (keyEvent && Keyboard.isKeyDown(Input.KEY_ENTER))
      {
         Utils.consumeKeyboardEvent();
         if (selectedItem != null)
         {
            selectedItem.setActive(true);
         }
      }
      else
      {
         Input input = HuntJumperGame.getInstance().getGameContainer().getInput();
         for (int i = 0; i < itemsRectangles.size(); ++i)
         {
            Rectangle rect = itemsRectangles.get(i);
            if (rect.contains(input.getMouseX(), input.getMouseY()))
            {
               selectedItemIndex = i;
               break;
            }
         }

         if (selectedItem != null && input.isMousePressed(Input.MOUSE_LEFT_BUTTON))
         {
            Rectangle rect = getSelectedMenuItemRectangle();
            if (rect.contains(input.getMouseX(), input.getMouseY()))
            {
               selectedItem.setActive(true);
            }
         }
      }
   }

   @Override
   public void render(Graphics g) throws SlickException
   {
      ScreenMenu selectedItem = getSelectedMenuItem();
      if (selectedItem != null)
      {
         if (selectedItem.isActive())
         {
            selectedItem.render(g);
            return;
         }
      }
      else if (active)
      {
         initNextState();
         if (nextState != null)
         {
            nextState.render(g);
            return;
         }
      }

      GameContainer container = HuntJumperGame.getInstance().getGameContainer();
      float centerX = container.getWidth() / 2;
      float centerY = container.getHeight() / 2;

      Font font = TextUtils.Arial30Font;
      int lineIndent = 5;
      int lineHeight = font.getHeight("T") + lineIndent;
      float currItemY = centerY - lineHeight*items.size() / 2.0f;

      itemsRectangles.clear();
      for (int i = 0; i < items.size(); ++i)
      {
         Color c = Color.white;
         if (i == selectedItemIndex)
         {
            c = Color.orange;
         }

         Point pos = new Point(centerX, currItemY);
         itemsRectangles.add(TextUtils.drawTextInCenter(pos, items.get(i).getName(), c, font, g));

         currItemY += lineHeight;
      }
   }

   public String getName()
   {
      return name;
   }

   public List<ScreenMenu> getItems()
   {
      return items;
   }

   public void setItems(List<ScreenMenu> items)
   {
      this.items = items;
   }

   public boolean isActive()
   {
      return active;
   }

   public void setActive(boolean active)
   {
      this.active = active;
   }
}
