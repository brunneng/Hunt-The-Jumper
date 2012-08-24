package com.greenteam.huntjumper.shaders;

/**
 * User: GreenTea Date: 11.08.12 Time: 16:31
 */
public enum Shader
{
   COIN("shaders/bonuses/coin/coin.frag"),
   LIGHT("shaders/light/light.frag");

   private final String pathToVertexShader;
   private final String pathToPixelShader;

   private Shader(String pathToVertexShader, String pathToPixelShader)
   {
      this.pathToVertexShader = pathToVertexShader;
      this.pathToPixelShader = pathToPixelShader;
   }

   private Shader(String pathToPixelShader)
   {
      this.pathToVertexShader = "shaders/pass.vert";
      this.pathToPixelShader = pathToPixelShader;
   }

   public String getPathToVertexShader()
   {
      return pathToVertexShader;
   }

   public String getPathToPixelShader()
   {
      return pathToPixelShader;
   }
}
