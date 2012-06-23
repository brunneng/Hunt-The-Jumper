package com.greenteam.huntjumper.parameters;

import com.greenteam.huntjumper.utils.TextUtils;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;

/**
 * User: GreenTea Date: 13.01.12 Time: 22:45
 */
public final class ViewConstants
{
   private ViewConstants()
   {

   }

   public static final int VIEW_WIDTH = 1024;
   public static final int VIEW_HEIGHT = 768;
   public static final String GAME_NAME = "Hunt the Jumper";
   public static final Color defaultMapColor = new Color(114, 114, 114);
   public static final Color defaultGroundColor = new Color(214, 214, 214);
   public static final float DRAW_NAME_MAX_RADIUS = GameConstants.JUMPER_RADIUS*7;

   public static final Color jumperBorderColor = Color.black;

   public static final int scoresBoxDistFromLeft = 5;
   public static final int scoresBoxDistFromTop = 5;
   public static final int scoresBoxTextLeftIndent = 15;
   public static final int scoresBoxTextTopIndent = 15;
   public static final int scoresBoxLineIndent = 10;
   public static final int scoresBoxRectRadius = 4;
   public static final float scoresBoxAlpha = 0.6f;
   public static final float scoresBoxJumpersAlpha = 0.5f;
   public static final int scoresBoxRightBorder = 310;
   public static final int scoresBoxScoresPosX = 200;
   public static final int scoresBoxBackTimerPosX = 260;
   public static final int scoresBoxBackTimerStartBlinkTime = 5000;
   public static final int scoresBoxBackTimerBlinkPeriod = 250;
   public static final Font scoresBoxFont = TextUtils.Arial20Font;

   public static final int timerIndentFromTop = 10;
   public static final float timerEllipseVerticalRadius = 20f;
   public static final float timerEllipseHorizontalRadius = 70f;
   public static final float timerEllipseAlpha = 0.35f;
   public static final int timerEllipseIndentFromText = 5;
   
   public static final int arrowIndentFromBoundaries = 50;

   public static final int roleChangeEffectDuration = 1000;
   public static final int roleChangeEffectHeight = 100;
   public static final Font roleChangeEffectFont = TextUtils.Arial30Font;

   public static final Font beforeEndNotificationFont = TextUtils.Arial20Font;
   public static final int beforeEndNotificationDuration = 5000;
   public static final float beforeEndNotificationBlinksPerSec = 2.5f;

   public static final float collisionVelocityOfMaxVolume = 500f;

   public static final float CAMERA_MAX_DIST = 100;
   public static final int CAMERA_SAMPLES_TIMER_INTERVAL = 20;
   public static final int CAMERA_MAX_SAMPLES_COUNT = 100;
   public static final int CAMERA_MAX_VELOCITY_OF_JUMPER = 500;

   public static final float WINNER_BOX_INDENT_FACTOR = 1.4f;
   public static final int WINNER_BOX_RECTANGLE_CORNER_RADIUS = 4;
   public static final float WINNER_BOX_RECTANGLE_ALPHA = 0.75f;
   public static final Color WINNER_BOX_COLOR = Color.white;
   public static final Font WINNER_BOX_FONT = TextUtils.Arial30Font;
   public static final Color WINNER_BOX_FONT_BACK_COLOR = new Color(160, 160, 160);
   public static final Color WINNER_BOX_FONT_FRONT_COLOR = Color.black;
   
   public static final int COLLISIONS_PARTICLES_MAX_COUNT = 5;
   public static final int COLLISIONS_PARTICLES_MAX_DEVIATION = 200;
   public static final float COLLISIONS_PARTICLES_VELOCITY_FACTOR = 0.75f;

   public static final Font INIT_SCREEN_FONT = TextUtils.Arial30Font;
   public static final int INIT_SCREEN_MAX_DOTS_COUNT = 3;
   public static final int INIT_SCREEN_MAX_PERCENT_STR_LEN = 4;
   public static final int INIT_SCREEN_DOT_ADD_TIME = 1000;

   public static int PREVIEW_IMAGE_WIDTH = 250;
   public static int PREVIEW_IMAGE_HEIGHT = 250;
   public static final Color PREVIEW_IMAGE_BORDER_COLOR = new Color(73, 73, 73);

   public static final int MAX_FADE_TIME = 400;
   public static final int MIN_FADE_LENGTH = 5;
   public static final float START_FADE_ALPHA = 0.8f;
   public static final int FADE_POSITIONS_TIME_INTERVAL = 15;

   public static final Color MENU_SELECTED_COLOR = Color.orange;
}
