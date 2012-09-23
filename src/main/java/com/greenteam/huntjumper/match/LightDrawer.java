package com.greenteam.huntjumper.match;

import com.greenteam.huntjumper.model.ILightSource;
import com.greenteam.huntjumper.model.ILightproof;
import com.greenteam.huntjumper.shaders.Shader;
import com.greenteam.huntjumper.shaders.ShadersSystem;
import com.greenteam.huntjumper.utils.Point;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.shader.ShaderProgram;

import java.util.ArrayList;
import java.util.List;

import static com.greenteam.huntjumper.parameters.ViewConstants.VIEW_HEIGHT;
import static com.greenteam.huntjumper.parameters.ViewConstants.VIEW_WIDTH;

/**
 * User: GreenTea Date: 23.09.12 Time: 13:23
 */
public class LightDrawer implements IVisibleObject
{
   private static ShaderProgram ligthProgram;
   private static void initLightShader()
   {
      if (ligthProgram != null || !ShadersSystem.getInstance().isReady())
      {
         return;
      }

      ligthProgram = ShadersSystem.getInstance().getProgram(Shader.LIGHT);
   }

   private List<ILightproof> lightproofObjects = new ArrayList<>();
   private List<ILightSource> lightSources = new ArrayList<>();
   protected boolean needUpdateLightPassibility = true;
   private Image lightPassability;

   private void tryUpdateLightPassibility() throws SlickException
   {
      if (lightPassability == null)
      {
         lightPassability = new Image(VIEW_WIDTH, VIEW_HEIGHT);
      }

      if (needUpdateLightPassibility)
      {
         Graphics passGraphics = lightPassability.getGraphics();
         passGraphics.setAntiAlias(false);

         passGraphics.setColor(ILightproof.LIGHT_FREE_COLOR);
         passGraphics.fillRect(0, 0, VIEW_WIDTH, VIEW_HEIGHT);

         for (ILightproof lightproof : lightproofObjects)
         {
            lightproof.drawLightProofBody(passGraphics);
         }
         passGraphics.flush();
         needUpdateLightPassibility = false;
      }
   }

   @Override
   public void draw(Graphics g)
   {
      ShadersSystem shadersSystem = ShadersSystem.getInstance();

      g.setAntiAlias(false);

      try
      {
         tryUpdateLightPassibility();
      }
      catch (SlickException e)
      {
         e.printStackTrace();
      }

      initLightShader();

      for (ILightSource lightSource : lightSources)
      {
         Point viewPos = Camera.getCamera().toView(lightSource.getPosition());
         if (!Camera.getCamera().inViewScreenWithReserve(viewPos))
         {
            continue;
         }

         ligthProgram.bind();
         shadersSystem.setPosition(ligthProgram, viewPos);
         shadersSystem.setResolution(ligthProgram, VIEW_WIDTH, VIEW_WIDTH);
         shadersSystem.setColor(ligthProgram, lightSource.getLightColor());

         ligthProgram.setUniform1f("lightCircle", lightSource.getLightCircle());

         final float lightRadius = lightSource.getLightMaxRadius();
         ligthProgram.setUniform1f("lightMaxDist", lightRadius);

         lightPassability.bind();
         ligthProgram.setUniform1i("passability", 0);

         g.fillRect(viewPos.getX() - lightRadius, viewPos.getY() - lightRadius,
                 2*lightRadius, 2*lightRadius);
      }

      ShaderProgram.unbind();
      g.setAntiAlias(true);
   }

   public List<ILightproof> getLightproofObjects()
   {
      return lightproofObjects;
   }

   public void setLightproofObjects(List<ILightproof> lightproofObjects)
   {
      this.lightproofObjects = lightproofObjects;
   }

   public List<ILightSource> getLightSources()
   {
      return lightSources;
   }

   public void setNeedUpdateLightPassibility(boolean needUpdateLightPassibility)
   {
      this.needUpdateLightPassibility = needUpdateLightPassibility;
   }

   public void setLightSources(List<ILightSource> lightSources)
   {
      this.lightSources = lightSources;
   }
}
