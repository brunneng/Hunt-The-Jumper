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
   public static final Color DEFAULT_MAP_COLOR = new Color(84, 84, 84);
   public static final Color DEFAULT_GROUND_COLOR = new Color(199, 199, 199);
   public static final float DRAW_NAME_MAX_RADIUS = GameConstants.JUMPER_RADIUS*7;

   public static final Color JUMPER_BORDER_COLOR = Color.black;

   public static final int SCORES_BOX_DIST_FROM_LEFT = 5;
   public static final int SCORES_BOX_DIST_FROM_TOP = 5;
   public static final int SCORES_BOX_TEXT_LEFT_INDENT = 15;
   public static final int SCORES_BOX_TEXT_TOP_INDENT = 15;
   public static final int SCORES_BOX_LINE_INDENT = 10;
   public static final int SCORES_BOX_RECT_RADIUS = 4;
   public static final float SCORES_BOX_ALPHA = 0.6f;
   public static final float SCORES_BOX_JUMPERS_ALPHA = 0.5f;
   public static final int SCORES_BOX_RIGHT_BORDER = 310;
   public static final int SCORES_BOX_SCORES_POS_X = 200;
   public static final int SCORES_BOX_BACK_TIMER_POS_X = 260;
   public static final int SCORES_BOX_BACK_TIMER_START_BLINK_TIME = 5000;
   public static final int SCORES_BOX_BACK_TIMER_BLINK_PERIOD = 250;
   public static final Font SCORES_BOX_FONT = TextUtils.Arial20Font;

   public static final int TIMER_INDENT_FROM_TOP = 10;
   public static final float TIMER_ELLIPSE_VERTICAL_RADIUS = 20f;
   public static final float TIMER_ELLIPSE_HORIZONTAL_RADIUS = 70f;
   public static final float TIMER_ELLIPSE_ALPHA = 0.35f;
   public static final int TIMER_ELLIPSE_INDENT_FROM_TEXT = 5;
   
   public static final int ARROW_INDENT_FROM_BOUNDARIES = 50;

   public static final int ROLE_CHANGE_EFFECT_DURATION = 1000;
   public static final int ROLE_CHANGE_EFFECT_HEIGHT = 100;
   public static final Font ROLE_CHANGE_EFFECT_FONT = TextUtils.Arial30Font;

   public static final Font BEFORE_END_NOTIFICATION_FONT = TextUtils.Arial20Font;
   public static final int BEFORE_END_NOTIFICATION_DURATION = 5000;
   public static final float BEFORE_END_NOTIFICATION_BLINKS_PER_SEC = 2.5f;

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

   public static final float COIN_SPHERE_RECT_WIDTH = 9f;
   public static final int TAKE_COIN_EFFECT_DURATION = 1000;
   public static final Color TAKE_COIN_EFFECT_COLOR = Color.white;
   public static final int TAKE_COIN_EFFECT_HEIGHT = 75;
   public static final Font TAKE_COIN_EFFECT_FONT = TextUtils.Arial20Font;

   public static final float BONUS_TIME_PERCENT_TO_START_HIDE = 0.2f;
   public static final float ACC_BONUS_FADE_ANGLE_LENGTH = 120f;
   public static final float ACC_BONUS_FADE_DIST_FROM_JUMPER_FACTOR = 0.5f;
   public static final int ACC_BONUS_FADE_SEGMENTS_COUNT = 10;

   public static final int ACC_BONUS_PARTICLES_COUNT = 15;
   public static final float ACC_BONUS_PARTICLE_ANGLE_LENGTH = 180f;
   public static final Color ACC_BONUS_PARTICLE_COLOR = Color.gray;
   public static final float ACC_BONUS_SPLASH_PARTICLE_RADIUS = 1f;
   public static final float ACC_BONUS_SPLASH_VELOCITY_FACTOR = 10f;
   public static final int ACC_BONUS_SPLASH_DURATION = 1000;

   public static final float GRAVITY_BONUS_DIST_BETWEEN_RINGS = 4.4f;
   public static final float GRAVITY_BONUS_RING_WIDTH = 1.2f;
   public static final int GRAVITY_BONUS_TIME_2_RINGS = 1000;
   public static final float GRAVITY_BONUS_VIEW_RADIUS = 14f;
   public static final Color GRAVITY_BONUS_RING_COLOR = Color.black;
   public static final float GRAVITY_BONUS_MOVE_TO_JUMPER_TIME = 500f;

   public static final int INELASTIC_BONUS_WAVE_POINTS_COUNT = 16;
   public static final float INELASTIC_BONUS_MAX_WAVE_LENGTH = 3f;
   public static final float INELASTIC_BONUS_WAVE_AVERAGE_MOVE_SPEED = 2f;
   public static final float INELASTIC_BONUS_EFFECT_MOVE_TO_JUMPER = 500f;
   public static final float INELASTIC_BONUS_BASE_RADIUS_FACTOR = 1.4f;
   public static final float INELASTIC_BONUS_SECOND_LINE_WIDTH = 2f;

   public static final float JUMPER_LIGHT_MAX_RADIUS = 300f;
   public static final float COIN_LIGHT_MAX_RADIUS = 20f;
   public static final float PHYS_BONUS_MAX_RADIUS = 30f;
}
