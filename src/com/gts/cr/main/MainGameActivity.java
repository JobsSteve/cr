package com.gts.cr.main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import org.anddev.andengine.audio.music.Music;
import org.anddev.andengine.audio.music.MusicFactory;
import org.anddev.andengine.audio.sound.Sound;
import org.anddev.andengine.audio.sound.SoundFactory;
import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.IEntity;
import org.anddev.andengine.entity.modifier.IEntityModifier;
import org.anddev.andengine.entity.modifier.MoveYModifier;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.CameraScene;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.extension.physics.box2d.util.Vector2Pool;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.buffer.BufferObjectManager;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.util.modifier.IModifier;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.gts.cr.main.R;
import com.gts.cr.models.Barrier;
import com.gts.cr.models.Car;


public class MainGameActivity extends BaseGameActivity implements IOnSceneTouchListener {
	private float camera_width;
	private float camera_height;
	private float scale_ratio_x;
	private float scale_ratio_y;
	
	private float xModifier;
	private float xOffset;
	
	private static final float GALAXY_NEXUS_WIDTH = 720.0f;
	private static final float GALAXY_NEXUS_HEIGHT = 1184.0f;
	
	private Vibrator vibrator;
	
	public static final int GAME_STATE_PAUSED = 1;
	public static final int GAME_STATE_ACTIVE = 2;
	public static final int GAME_STATE_GAMEOVER = 3;
	
	public static final int TRACK_LENGTH = 1500;
	public int gameState;
	
	public static final int CAR_STATE_ACCELERATING = 4;
	public static final int CAR_STATE_STOPPED = 5;
	public static final int CAR_STATE_DECELERATING = 6;
	public static final int CAR_STATE_CRASHED = 7;
	public static final int CAR_STATE_NITROUS = 8;
	public int carState = CAR_STATE_STOPPED;
	
	private Camera mCamera;
	private Scene mMainScene;
	
	private Music acceleratingSound;
	private Sound crashSound;
	private Sound trafficLightSound;
	private boolean accelerationSoundStarted;
	private boolean trafficLightSoundStarted;
	
	private ArrayList<BitmapTextureAtlas> mCarTextureAtlasList;
	private ArrayList<BitmapTextureAtlas> mLeftStripTextureAtlasList;
	private ArrayList<BitmapTextureAtlas> mRightStripTextureAtlasList;
	private ArrayList<BitmapTextureAtlas> mPositionsTextureAtlasList;
	private BitmapTextureAtlas mRedLightTextureAtlas;
	private BitmapTextureAtlas mYellowLightTextureAtlas;
	private BitmapTextureAtlas mGreenLightTextureAtlas;
	private BitmapTextureAtlas mBackgroundTextureAtlas;
	private BitmapTextureAtlas mSpeedTextureAtlas;
	private BitmapTextureAtlas mTimeTextureAtlas;
	private BitmapTextureAtlas mLapTextureAtlas;
	private BitmapTextureAtlas mTrafficConeTextureAtlas;
	private BitmapTextureAtlas mSandbagTextureAtlas;
	private BitmapTextureAtlas mNitrousTextureAtlas;
	private BitmapTextureAtlas mPoliceTextureAtlas;//Police barrier
	private BitmapTextureAtlas mSpeedoTextureAtlas;
	private BitmapTextureAtlas mTimePanelTextureAtlas;
	private BitmapTextureAtlas mLapCounterTextureAtlas;
	
	private Font mSpeedFont;
	private ChangeableText speed;
	private int speedValue;
	private int fpsValue;
	
	private int acceleration_value = 1;
	
	private float initialAngle;
	
	private Font mTimeFont;
	private ChangeableText time;
	private int timeValue;
	private int mTimeCount;
	
	private int nitrousCount;
	private int initial_nitrous_time;
	
	private Font mLapFont;
	private ChangeableText lapCount;
	private boolean entered_secondlap;
	private boolean entered_thirdlap;
	
	private PhysicsWorld mPhysicsWorld;
	
	private CameraScene mPositionScene;
	
	private ArrayList<TextureRegion> mCarTextureRegionList;
	private ArrayList<TextureRegion> mRightStripTextureRegionList;
	private ArrayList<TextureRegion> mLeftStripTextureRegionList;
	private ArrayList<TextureRegion> mPositionsTextureRegionList;
	private TextureRegion mRedLightTextureRegion;
	private TextureRegion mYellowLightTextureRegion;
	private TextureRegion mGreenLightTextureRegion;
	private TiledTextureRegion mRoadTextureRegion;
	private TextureRegion mSandbagTextureRegion;
	private TextureRegion mTrafficConeTextureRegion;
	private TextureRegion mPoliceTextureRegion;
	private TextureRegion mNitrousTextureRegion;
	private TextureRegion mSpeedoTextureRegion;
	private TextureRegion mTimePanelTextureRegion;
	private TextureRegion mLapCounterTextureRegion;
	
	private AnimatedSprite mRoad;
	private Sprite mTrafficLight;
	private Sprite mSpeedometer;
	private Sprite mTimePanel;
	private Sprite mLapCounter;
	
	Car playerCar;
	ArrayList<Car> opponents;
	private float resetX;
	private float resetY;
	
	ArrayList<Sprite> leftStrips;
	ArrayList<Sprite> rightStrips;
	int leftStripCount;
	int rightStripCount;
	public static final int STRIP_MOVING = 11;
	public static final int STRIP_STOPPED = 12;
	public static final float STRIP_SPEED_12 = 12.0f;
	public static final float STRIP_SPEED_20 = 20.0f;
	
	private LinkedList<Barrier> barriersToBeAdded;
	private LinkedList<Barrier> barrierLL;
	Vector2 barrierVelocity;
	
	boolean acceleration_started = false;
	float distance_traveled;
	
	double azimuth;
	double pitch;
	double roll;
	
	static int sensorAccuracy;
	
	public static SensorManager sensorManager;
    SensorEventListener sensorListener = new SensorEventListener() {
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			sensorAccuracy = accuracy;
		}
		public void onSensorChanged(SensorEvent event) {
			azimuth = event.values[0];
			pitch = event.values[1];
			//roll is what we need
			roll = event.values[2];
			if ( gameState == GAME_STATE_ACTIVE && carState != CAR_STATE_STOPPED ) {
				updatePlayerCar(roll);
			}
			
			//Log.i("Orientation values", "Azimuth: "+azimuth+", Pitch: "+pitch+", Roll: "+roll);
			
		}
		    	
    };
    /**
     * Update threewheeler position based on gyroscope roll
     * @param roll
     */
    private void updatePlayerCar(double roll) {
    	Body playerBody = (Body)playerCar.sprite.getUserData();
    	float newXPosition = (float)roll * xModifier;
    	newXPosition = (-newXPosition + xOffset);
    	if ( newXPosition < (mCamera.getMaxX()*0.2f)/32 ) {
    		newXPosition = (mCamera.getMaxX() * 0.2f)/32 + (mCamera.getWidth()/(25*32));
    	}
    	else if ( newXPosition > (mCamera.getMaxX()*0.8f)/32 ) {
    		newXPosition = (mCamera.getMaxX() * 0.8f)/32 - (mCamera.getWidth()/(25*32));
    	}
/*    	Vector2 turningVector = Vector2Pool.obtain(newXPosition, 0.0f);
    	playerBody.setLinearVelocity(turningVector);
    	Vector2Pool.recycle(turningVector);*/
    	Vector2 transformVector = Vector2Pool.obtain(newXPosition, playerBody.getPosition().y);
    	playerBody.setTransform(transformVector, playerBody.getAngle());
    	Vector2Pool.recycle(transformVector);
    }
	public IUpdateHandler removeBarriersUpdateHandler = new IUpdateHandler() {

        public void reset() {
        }


        public void onUpdate(float pSecondsElapsed) {

            Iterator<Barrier> barriers = barrierLL.iterator();
            Barrier _barrier;
            while (barriers.hasNext()) {
            	_barrier = barriers.next();
            	if ( !_barrier.components.isEmpty() && _barrier.type != Barrier.BARRIER_TYPE_NITROUS ) {
            		Body barrierBody = (Body)_barrier.components.get(0).getUserData();
                	barrierBody.setLinearVelocity(barrierVelocity);
            	}
            	
                if ( !_barrier.components.isEmpty() && _barrier.components.get(0).getY() >= mCamera.getMaxY() ) {
                    removeBarrier(_barrier, barriers);
                }
                else if ( _barrier.type == Barrier.BARRIER_TYPE_NITROUS 
                		&& _barrier.components.get(0).collidesWith(playerCar.sprite)
                		&& !_barrier.components.isEmpty() ) {
                	Log.i("Nitrous Collission", String.valueOf( _barrier.components.get(0).collidesWith(playerCar.sprite)));
                	carState = CAR_STATE_NITROUS;
                	initial_nitrous_time = timeValue;
                	removeBarrier(_barrier, barriers);
                }
            }
            barrierLL.addAll(barriersToBeAdded);
            barriersToBeAdded.clear();
        }
    };

	public void onLoadComplete() {
		loadGyroscope();
	}
	@SuppressWarnings("deprecation")
	private void loadGyroscope() {
		sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
    	sensorManager.registerListener(sensorListener, 
    			sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
    			SensorManager.SENSOR_DELAY_GAME);
		
	}
	private void setupCars() {
		final FixtureDef playerFixtureDef = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);
		final FixtureDef opponentFixtureDef = PhysicsFactory.createFixtureDef(1, 0.5f, 1.0f);
		/*Animated car*/
		if ( camera_width < 360 ) {
			playerCar.setSprite(new Sprite(mCamera.getWidth() * 0.25f, 
		       		mCamera.getMaxY() - (mCamera.getHeight()/3), mCarTextureRegionList.get(Common.selectedCar - 1)));
			playerCar.sprite.setScaleX(0.32f);
	        playerCar.sprite.setScaleY(0.32f);
		}
		else {
			playerCar.setSprite(new Sprite(mCamera.getWidth()* 0.1f, 
		       		mCamera.getMaxY() - (mCamera.getHeight()/3), mCarTextureRegionList.get((Common.selectedCar * 5)-5)));
			playerCar.sprite.setScaleX(0.32f * scale_ratio_x);
	        playerCar.sprite.setScaleY(0.32f * scale_ratio_y);
		}
        
        resetX = playerCar.sprite.getX();
        resetY = playerCar.sprite.getY();
		//playerCar.setSprite(new Sprite(mCamera.getCenterX(), mCamera.getCenterY(), mCarTextureRegionList.get(Common.selectedCar-1)));
        playerCar.sprite.setRotation(270.0f);
        Body body = PhysicsFactory.createBoxBody(this.mPhysicsWorld, playerCar.sprite, BodyType.DynamicBody, playerFixtureDef);
        body.setUserData(playerCar);
        initialAngle = body.getAngle();
        mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(playerCar.sprite, body, true, true));
        playerCar.sprite.setUserData(body);
        mMainScene.attachChild(playerCar.sprite);
        carState = CAR_STATE_STOPPED;
        
        int count = 1;
        Random random = new Random();
        for ( Car opponent : opponents ) {
        	if ( camera_width < 360  ) {
        		opponent.setSprite(new Sprite(playerCar.sprite.getInitialX() + (mCamera.getWidth() * 0.05f * count) , 
            			playerCar.sprite.getInitialY(),mCarTextureRegionList.get(generateRandomInteger(0, 4, random))));
        		opponent.sprite.setScaleX(0.32f);
            	opponent.sprite.setScaleY(0.32f);
        	}
        	else {
        		opponent.setSprite(new Sprite(playerCar.sprite.getInitialX() + (mCamera.getWidth() * 0.075f * count * scale_ratio_x) , 
            			playerCar.sprite.getInitialY(),mCarTextureRegionList.get(generateRandomInteger(0, 24, random))));
        		opponent.sprite.setScaleX(0.32f * scale_ratio_x);
            	opponent.sprite.setScaleY(0.32f * scale_ratio_y);
        	}
        	
        	
        	opponent.sprite.setRotation(270.0f);
        	Body opponentBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, opponent.sprite, BodyType.DynamicBody, opponentFixtureDef);
        	opponentBody.setUserData(opponent);
            mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(opponent.sprite, opponentBody, true, true));
            opponent.sprite.setUserData(opponentBody);
            opponent.initialAngle = opponentBody.getAngle();
        	mMainScene.attachChild(opponent.sprite);
        	count += 1;
        }
        /*End car section*/
		
	}
	@SuppressWarnings("deprecation")

	public Engine onLoadEngine() {
		final Display display = getWindowManager().getDefaultDisplay();
		camera_width = display.getWidth();
		camera_height = display.getHeight();
		mCamera = new Camera(0, 0, camera_width, camera_height);
		Log.i("Camera_width", String.valueOf(camera_width));
		Log.i("Camera_Height", String.valueOf(camera_height));
		
		scale_ratio_x = camera_width / GALAXY_NEXUS_WIDTH;
		scale_ratio_y = camera_height / GALAXY_NEXUS_HEIGHT;
		xOffset = mCamera.getWidth() * 0.3f / 32;
    	if ( camera_width < 360 ) {
    		xModifier = mCamera.getWidth()/(75 * 32);
    	}
    	else {
    		xModifier = mCamera.getWidth()/(60 * 32);
    	}
		return new Engine(new EngineOptions(true, ScreenOrientation.PORTRAIT,
				new RatioResolutionPolicy(camera_width, camera_height), mCamera).setNeedsSound(true).setNeedsMusic(true));
	}

	public void onLoadResources() {
		vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		
		mCarTextureAtlasList = new ArrayList<BitmapTextureAtlas>();
		mCarTextureRegionList = new ArrayList<TextureRegion>();
		mLeftStripTextureAtlasList = new ArrayList<BitmapTextureAtlas>();
		mRightStripTextureAtlasList = new ArrayList<BitmapTextureAtlas>();
		mPositionsTextureAtlasList = new ArrayList<BitmapTextureAtlas>();
		
		mLeftStripTextureRegionList = new ArrayList<TextureRegion>();
		mRightStripTextureRegionList = new ArrayList<TextureRegion>();
		mPositionsTextureRegionList = new ArrayList<TextureRegion>();
		if ( camera_width < 360 ) {
			for ( int i = 0; i < 5; i++ ) {
				mCarTextureAtlasList.add(new BitmapTextureAtlas(256, 128,TextureOptions.BILINEAR_PREMULTIPLYALPHA) );
			}
			for ( int i = 0; i < 3; i++ ) {
				mRightStripTextureAtlasList.add(new BitmapTextureAtlas(512, 2048,TextureOptions.BILINEAR_PREMULTIPLYALPHA));
				mLeftStripTextureAtlasList.add(new BitmapTextureAtlas(512, 2048,TextureOptions.BILINEAR_PREMULTIPLYALPHA));
			}
			mRedLightTextureAtlas = new BitmapTextureAtlas(512, 128, TextureOptions.DEFAULT);
			mYellowLightTextureAtlas = new BitmapTextureAtlas(512, 128, TextureOptions.DEFAULT);
			mGreenLightTextureAtlas = new BitmapTextureAtlas(512, 128, TextureOptions.DEFAULT);;
			
			mBackgroundTextureAtlas = new BitmapTextureAtlas(512,1024, TextureOptions.DEFAULT);
			mSpeedTextureAtlas = new BitmapTextureAtlas(256,256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
			mTimeTextureAtlas = new BitmapTextureAtlas(256,256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
			mLapTextureAtlas = new BitmapTextureAtlas(256,256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
			
			//Barriers
			mTrafficConeTextureAtlas = new BitmapTextureAtlas(32,32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
			mSandbagTextureAtlas = new BitmapTextureAtlas(32,32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
			mPoliceTextureAtlas = new BitmapTextureAtlas(128,128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
			mNitrousTextureAtlas = new BitmapTextureAtlas(32,32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
			
			mSpeedoTextureAtlas = new BitmapTextureAtlas(128,128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
			mTimePanelTextureAtlas = new BitmapTextureAtlas(256,128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
			mLapCounterTextureAtlas = new BitmapTextureAtlas(128,128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
			
			BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("lowres_gfx/");
		}
		else {
			for ( int i = 0; i < 25; i++ ) {
				mCarTextureAtlasList.add(new BitmapTextureAtlas(512, 256,TextureOptions.BILINEAR_PREMULTIPLYALPHA) );
			}
			for ( int i = 0; i < 5; i++ ) {
				mRightStripTextureAtlasList.add(new BitmapTextureAtlas(512, 2048,TextureOptions.BILINEAR_PREMULTIPLYALPHA));
				mLeftStripTextureAtlasList.add(new BitmapTextureAtlas(512, 2048,TextureOptions.BILINEAR_PREMULTIPLYALPHA));
			}
			mRedLightTextureAtlas = new BitmapTextureAtlas(512, 256, TextureOptions.DEFAULT);
			mYellowLightTextureAtlas = new BitmapTextureAtlas(512, 256, TextureOptions.DEFAULT);
			mGreenLightTextureAtlas = new BitmapTextureAtlas(512, 256, TextureOptions.DEFAULT);;
			
			mBackgroundTextureAtlas = new BitmapTextureAtlas(1024,2048, TextureOptions.DEFAULT);
			mSpeedTextureAtlas = new BitmapTextureAtlas(256,256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
			mTimeTextureAtlas = new BitmapTextureAtlas(256,256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
			mLapTextureAtlas = new BitmapTextureAtlas(256,256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
			
			//Barriers
			mTrafficConeTextureAtlas = new BitmapTextureAtlas(32,32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
			mSandbagTextureAtlas = new BitmapTextureAtlas(32,32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
			mPoliceTextureAtlas = new BitmapTextureAtlas(128,128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
			mNitrousTextureAtlas = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
			
			mSpeedoTextureAtlas = new BitmapTextureAtlas(256,256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
			mTimePanelTextureAtlas = new BitmapTextureAtlas(512,256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
			mLapCounterTextureAtlas = new BitmapTextureAtlas(256,256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
			
			BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		}
		
		for ( int i = 0; i < 4; i++ ) {
			mPositionsTextureAtlasList.add(new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA));
		}
		int textureNo = 0;
		if ( camera_width < 360  ) {
			for ( int i = 1; i <= 5; i++ ) {
				mCarTextureRegionList.add(BitmapTextureAtlasTextureRegionFactory
						.createFromAsset(mCarTextureAtlasList.get(i - 1), this, "car0"+i+".png", 0, 0));
			}
			for ( int i = 1; i <= 3; i++ ) {
				if ( i < 3 ) {
					mLeftStripTextureRegionList.add(BitmapTextureAtlasTextureRegionFactory
							.createFromAsset(mLeftStripTextureAtlasList.get(i-1), this, "left_strip_"+i+".png", 0, 0));
						mRightStripTextureRegionList.add(BitmapTextureAtlasTextureRegionFactory
								.createFromAsset(mRightStripTextureAtlasList.get(i-1), this, "right_strip_"+i+".png", 0, 0));
				}
				else {
					mLeftStripTextureRegionList.add(BitmapTextureAtlasTextureRegionFactory
							.createFromAsset(mLeftStripTextureAtlasList.get(i-1), this, "left_strip_"+(i + 2)+".png", 0, 0));
						mRightStripTextureRegionList.add(BitmapTextureAtlasTextureRegionFactory
								.createFromAsset(mRightStripTextureAtlasList.get(i-1), this, "right_strip_"+(i + 2)+".png", 0, 0));
				}
				
			}
		}
		else {
			for ( int i = 1; i <= 5; i++ ) {
				mCarTextureRegionList.add(BitmapTextureAtlasTextureRegionFactory
					.createFromAsset(mCarTextureAtlasList.get(textureNo), this, "car0"+i+".png", 0, 0));
				textureNo++;
				for ( int j = 1; j <= 4; j++ ) {
					mCarTextureRegionList.add(BitmapTextureAtlasTextureRegionFactory
							.createFromAsset(mCarTextureAtlasList.get(textureNo), this, "car0"+i+"_"+j+".png", 0, 0));
					textureNo++;
				}
			}
			for ( int i = 1; i <= 5; i++ ) {
				mLeftStripTextureRegionList.add(BitmapTextureAtlasTextureRegionFactory
					.createFromAsset(mLeftStripTextureAtlasList.get(i-1), this, "left_strip_"+i+".png", 0, 0));
				mRightStripTextureRegionList.add(BitmapTextureAtlasTextureRegionFactory
						.createFromAsset(mRightStripTextureAtlasList.get(i-1), this, "right_strip_"+i+".png", 0, 0));
			}
		}
		
		for ( int i = 1; i <= 4; i++ ) {
			mPositionsTextureRegionList.add(BitmapTextureAtlasTextureRegionFactory
						.createFromAsset(mPositionsTextureAtlasList.get(i-1), this, "position_"+i+".png", 0, 0));
		}
		mRedLightTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mRedLightTextureAtlas, this, "light01.png", 0, 0);
		mYellowLightTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mYellowLightTextureAtlas, this, "light02.png", 0, 0);
		mGreenLightTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mGreenLightTextureAtlas, this, "light03.png", 0, 0);
		
		mRoadTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(mBackgroundTextureAtlas, this, "clean_map.png", 0, 0, 2, 2);
		
		mSpeedFont = new Font(mSpeedTextureAtlas, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 
				40, true, Color.BLACK);
		mTimeFont = new Font(mTimeTextureAtlas, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 
				40, true, Color.BLACK);
		mLapFont = new Font(mLapTextureAtlas, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 
				60, true, Color.WHITE);
		mSpeedoTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mSpeedoTextureAtlas, this, "speedometer.png", 0, 0);
		mTimePanelTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mTimePanelTextureAtlas, this, "time_panel.png", 0, 0);
		mLapCounterTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mLapCounterTextureAtlas, this, "lap_counter.png", 0, 0);
		//Barriers
		mTrafficConeTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mTrafficConeTextureAtlas, this, "traffic_cone.png", 0, 0);
		mSandbagTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mSandbagTextureAtlas, this, "sandbag.png", 0, 0);
		mPoliceTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mPoliceTextureAtlas, this, "roadblock_1.png", 0, 0);
		mNitrousTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mNitrousTextureAtlas, this, "nitrous.png", 0, 0);
		
		for ( BitmapTextureAtlas atlas: mCarTextureAtlasList ) {
			mEngine.getTextureManager().loadTexture(atlas);
		}
		for ( BitmapTextureAtlas atlas: mLeftStripTextureAtlasList ) {
			mEngine.getTextureManager().loadTexture(atlas);
		}
		for ( BitmapTextureAtlas atlas: mRightStripTextureAtlasList ) {
			mEngine.getTextureManager().loadTexture(atlas);
		}
		for ( BitmapTextureAtlas atlas: mPositionsTextureAtlasList ) {
			mEngine.getTextureManager().loadTexture(atlas);
		}
		mEngine.getTextureManager().loadTextures(mBackgroundTextureAtlas,mSpeedTextureAtlas, mTimeTextureAtlas,
				mRedLightTextureAtlas, mYellowLightTextureAtlas, mGreenLightTextureAtlas, mSandbagTextureAtlas,
				mTrafficConeTextureAtlas, mPoliceTextureAtlas, mNitrousTextureAtlas, mSpeedoTextureAtlas, 
				mTimePanelTextureAtlas, mLapCounterTextureAtlas, mLapTextureAtlas);
		mEngine.getFontManager().loadFonts(mSpeedFont, mTimeFont, mLapFont);
		
		/**Loading sound*/
		SoundFactory.setAssetBasePath("mfx/");
		MusicFactory.setAssetBasePath("mfx/");

		try {
		   acceleratingSound = MusicFactory.createMusicFromAsset(mEngine.getMusicManager(), this, "moving_sound.mp3");
		   crashSound = SoundFactory.createSoundFromAsset(mEngine.getSoundManager(), this, "brake_sound.mp3");
		   trafficLightSound = SoundFactory.createSoundFromAsset(mEngine.getSoundManager(), this, "traffic_light_sound.mp3");
		   acceleratingSound.setLooping(true);
		   
		} catch (IllegalStateException e) {
		    e.printStackTrace();
		} catch (Exception e) {
		    e.printStackTrace();
		}
		playerCar = new Car();
		switch ( Common.selectedCar ) {
		case 1:
			playerCar.setTopSpeed(240);
			break;
		case 2:
			playerCar.setTopSpeed(160);
			break;
		case 3:
			playerCar.setTopSpeed(160);
			break;
		case 4:
			playerCar.setTopSpeed(270);
			break;
		case 5:
			playerCar.setTopSpeed(190);
			break;
		default:
			playerCar.setTopSpeed(250);
			break;
		}
		playerCar.setId(1);
		
		opponents = new ArrayList<Car>();
		opponents.add(new Car(2, playerCar.getTopSpeed() - 20, 1));
		opponents.add(new Car(3, playerCar.getTopSpeed() - 50, 1));
		opponents.add(new Car(4, playerCar.getTopSpeed() - 10, 1));
		
		
	}


	public Scene onLoadScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

        mMainScene = new Scene();
        this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, 0), false);
        
        gameState = GAME_STATE_ACTIVE;
        /*Animated Background Road*/
        mRoad = new AnimatedSprite(mCamera.getMinX(), mCamera.getMinY(), mRoadTextureRegion);
        float widthRatio = mCamera.getWidth()/mRoad.getWidth();
        float heightRatio = mCamera.getHeight()/mRoad.getHeight();
        float offsetX = (( mRoad.getWidth() * widthRatio )  - mRoad.getWidth()) / 2;
        float offsetY = (( mRoad.getHeight() * heightRatio ) - mRoad.getHeight()) / 2;
        mRoad.setScaleX(widthRatio);
        mRoad.setScaleY(heightRatio);
        mRoad.setPosition(offsetX, offsetY);
        fpsValue = 100;
        //mRoad.animate((long)fpsValue, true);
        mMainScene.attachChild(mRoad);
        /*End road section*/
        /* Initialize side strips */
        leftStrips = new ArrayList<Sprite>();
        rightStrips = new ArrayList<Sprite>();
        if ( camera_width < 360  ) {
        	for ( int i = 0; i < 3; i ++) {
            	if ( i == 0 ) {
            		leftStrips.add(new Sprite(mCamera.getMinX(), 
                			 mCamera.getHeight() - mLeftStripTextureRegionList.get(i).getHeight(), mLeftStripTextureRegionList.get(i) ));
                	leftStrips.get(i).setWidth(mCamera.getWidth() * 0.2f);
                	mMainScene.attachChild(leftStrips.get(i));
                	rightStrips.add(new Sprite(mCamera.getWidth() - (mCamera.getWidth() * 0.2f), 
                			 mCamera.getHeight() - mRightStripTextureRegionList.get(i).getHeight(), mRightStripTextureRegionList.get(i) ));
                	rightStrips.get(i).setWidth(mCamera.getWidth() * 0.2f);
                	mMainScene.attachChild(rightStrips.get(i));
            	}
            	else if ( i < 3 ) {
            		leftStrips.add(new Sprite(mCamera.getMinX(), 
                			leftStrips.get(i-1).getY() - leftStrips.get(i-1).getHeight(), mLeftStripTextureRegionList.get(i) ));
                	leftStrips.get(i).setWidth(mCamera.getWidth() * 0.2f);
                	mMainScene.attachChild(leftStrips.get(i));
                	rightStrips.add(new Sprite(mCamera.getWidth() - (mCamera.getWidth() * 0.2f), 
                			rightStrips.get(i-1).getY() - rightStrips.get(i-1).getHeight(), mRightStripTextureRegionList.get(i) ));
                	rightStrips.get(i).setWidth(mCamera.getWidth() * 0.2f);
                	mMainScene.attachChild(rightStrips.get(i));
            	}
            	
            }
        }
        else {
        	for ( int i = 0; i < 5; i ++) {
            	if ( i == 0 ) {
            		leftStrips.add(new Sprite(mCamera.getMinX(), 
                			 mCamera.getHeight() - mLeftStripTextureRegionList.get(i).getHeight(), mLeftStripTextureRegionList.get(i) ));
                	leftStrips.get(i).setWidth(mCamera.getWidth() * 0.2f);
                	mMainScene.attachChild(leftStrips.get(i));
                	rightStrips.add(new Sprite(mCamera.getWidth() - (mCamera.getWidth() * 0.2f), 
                			 mCamera.getHeight() - mRightStripTextureRegionList.get(i).getHeight(), mRightStripTextureRegionList.get(i) ));
                	rightStrips.get(i).setWidth(mCamera.getWidth() * 0.2f);
                	mMainScene.attachChild(rightStrips.get(i));
            	}
            	else if ( i < 5 ) {
            		leftStrips.add(new Sprite(mCamera.getMinX(), 
                			leftStrips.get(i-1).getY() - leftStrips.get(i-1).getHeight(), mLeftStripTextureRegionList.get(i) ));
                	leftStrips.get(i).setWidth(mCamera.getWidth() * 0.2f);
                	mMainScene.attachChild(leftStrips.get(i));
                	rightStrips.add(new Sprite(mCamera.getWidth() - (mCamera.getWidth() * 0.2f), 
                			rightStrips.get(i-1).getY() - rightStrips.get(i-1).getHeight(), mRightStripTextureRegionList.get(i) ));
                	rightStrips.get(i).setWidth(mCamera.getWidth() * 0.2f);
                	mMainScene.attachChild(rightStrips.get(i));
            	}
            	
            }
        }
        
        /* End side strips initialization */
        /*Contact Listener for physics*/
        
		mPhysicsWorld.setContactListener(new ContactListener() {
			
			public void preSolve(Contact contact, Manifold oldManifold) {
				
			}
			
			public void postSolve(Contact contact, ContactImpulse impulse) {
				
			}
			
			public void endContact(Contact contact) {
				
			}
			
			public void beginContact(Contact contact) {
				
				if ( contact.getFixtureA().getBody().getUserData() != null && 
						contact.getFixtureB().getBody().getUserData() != null  ) {
					final Car carA = (Car)contact.getFixtureA().getBody().getUserData();
					final Car carB = (Car)contact.getFixtureB().getBody().getUserData();
					Body carABody = contact.getFixtureA().getBody();
					Body carBBody = contact.getFixtureB().getBody();
					if ( carBBody.getLinearVelocity().y != 0.0f || carABody.getLinearVelocity().y != 0.0f ) {
						Vector2 linearZero = Vector2Pool.obtain(0,0);
						carBBody.setLinearVelocity(linearZero);
						carABody.setLinearVelocity(linearZero);
						Vector2Pool.recycle(linearZero);
					}
					//Log.i("Car A", carA.toString());
					//Log.i("Car B", carB.toString());
					if ( carA.getId() == 1 || carB.getId() == 1 ) {
						carState = CAR_STATE_CRASHED;
						vibrator.vibrate(100);
						if ( carA.getId() == 1 ) {
							if ( opponents.get(carB.getId() - 2).currentSpeed < 30 ) {
								opponents.get(carB.getId() - 2).currentSpeed = 0;
							}
							else opponents.get(carB.getId() - 2).currentSpeed -= 30;
							
						}
						else {
							if ( opponents.get(carA.getId() - 2).currentSpeed < 30 ) {
								opponents.get(carA.getId() - 2).currentSpeed = 0;
							}
							else opponents.get(carA.getId() - 2).currentSpeed -= 30;
						}
					}
				}
				//Contact with wall
				else if ( contact.getFixtureA().getBody().getUserData() != null ) {
					final Car car = (Car)contact.getFixtureA().getBody().getUserData();
					if ( car.getId() == 1 ) {
						carState = CAR_STATE_CRASHED;
						vibrator.vibrate(100);
						if ( contact.getFixtureB().getBody().getLinearVelocity().y != 0 ) {
							Vector2 linearZero = Vector2Pool.obtain(0,0);
							contact.getFixtureA().getBody().setLinearVelocity(linearZero);
							Vector2Pool.recycle(linearZero);
						}
					}
					else {
						if ( opponents.get(car.getId() - 2).currentSpeed < 30 ) {
							opponents.get(car.getId() - 2).currentSpeed = 0;
						}
						else opponents.get(car.getId() - 2).currentSpeed -= 30;
					}
					
				}
				else if ( contact.getFixtureB().getBody().getUserData() != null ) {
					final Car car = (Car)contact.getFixtureB().getBody().getUserData();
					if ( car.getId() == 1 ) {
						carState = CAR_STATE_CRASHED;
						vibrator.vibrate(100);
						if ( contact.getFixtureA().getBody().getLinearVelocity().y != 0 ) {
							Vector2 linearZero = Vector2Pool.obtain(0,0);
							contact.getFixtureB().getBody().setLinearVelocity(linearZero);
							Vector2Pool.recycle(linearZero);
						}
					}
					else {
						if ( opponents.get(car.getId() - 2).currentSpeed < 30 ) {
							opponents.get(car.getId() - 2).currentSpeed = 0;
						}
						else opponents.get(car.getId() - 2).currentSpeed -= 30;
					}
				}
				
				
			}
			
		});
        /**Initialize physics objects */
        final Shape left = new Rectangle(camera_width * 0.2f, 0, 0, camera_height);
        final Shape right = new Rectangle(camera_width * 0.8f, 0, 0, camera_height);
        
        final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
        PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyType.StaticBody, wallFixtureDef);
        PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyType.StaticBody, wallFixtureDef);
        
        mMainScene.attachChild(left);
        mMainScene.attachChild(right);
        mMainScene.registerUpdateHandler(mPhysicsWorld);
        
        /*Cars and handlers*/
		setupCars();
        createSpeedHandler();
        createTimeHandler();
        /*End Cars*/

        /* Barrier Section */
        if ( camera_width < 360 ) {
        	barrierVelocity = Vector2Pool.obtain(0, 5.0f);
        }
        else {
        	barrierVelocity = Vector2Pool.obtain(0, 10.0f);
        }
        
        barriersToBeAdded = new LinkedList<Barrier>();
        barrierLL = new LinkedList<Barrier>();
        createBarrierSpawnTimerHandler();
        mMainScene.registerUpdateHandler(removeBarriersUpdateHandler);
        /* End Barrier Section */
        
        /*Score and Time*/
        speed = new ChangeableText(0, 0, mSpeedFont,String.valueOf(0), 10);
        speed.setPosition(mCamera.getWidth() - speed.getWidth() - (mCamera.getWidth() * 0.2f), mCamera.getHeight() * 0.07f);
        speed.setScaleX(scale_ratio_x);
        speed.setScaleY(scale_ratio_y);
        
        // + mCamera.getWidth() * 0.15f
        time = new ChangeableText(0, 0, mTimeFont,String.valueOf(0), 10);
        time.setPosition(mCamera.getMinX() + mCamera.getWidth() * 0.15f, mCamera.getHeight() * 0.07f);
        time.setScaleX(scale_ratio_x);
        time.setScale(scale_ratio_y);
        
        lapCount = new ChangeableText(0, 0, mLapFont,String.valueOf(0), 10);
        lapCount.setPosition(mCamera.getMinX() + lapCount.getWidth(), mCamera.getMaxY() - (mCamera.getHeight() * 0.2f));
        lapCount.setText("1");
        lapCount.setScaleX(scale_ratio_x);
        lapCount.setScaleY(scale_ratio_y);
        
        mSpeedometer = new Sprite(speed.getX() - (mSpeedoTextureRegion.getWidth() * 0.38f), 
        		speed.getY() - (mSpeedoTextureRegion.getHeight() * 0.35f), mSpeedoTextureRegion);
        if ( camera_width > 360 ) {
        	mSpeedometer.setScaleX(0.8f * scale_ratio_x);
            mSpeedometer.setScaleY(0.8f * scale_ratio_y);
        }
        else {
        	mSpeedometer.setScaleX(0.5f);
            mSpeedometer.setScaleY(0.5f);
        }
        
        mMainScene.attachChild(mSpeedometer);
        mMainScene.attachChild(speed);
        mTimePanel = new Sprite(time.getX() - (mTimePanelTextureRegion.getWidth() * 0.32f * scale_ratio_x), 
        		time.getY() - (mTimePanelTextureRegion.getHeight() * 0.32f * scale_ratio_y), mTimePanelTextureRegion);
        if ( camera_width > 360 ) {
        	mTimePanel.setScaleX(0.75f * scale_ratio_x);
            mTimePanel.setScaleY(0.75f * scale_ratio_y);
        }
        else {
        	mTimePanel.setScaleX(0.5f);
            mTimePanel.setScaleY(0.5f);
        }
        
        mMainScene.attachChild(mTimePanel);
        mMainScene.attachChild(time);
        
        mLapCounter = new Sprite(lapCount.getX() - (mLapCounterTextureRegion.getWidth() * 0.32f), 
        		lapCount.getY() - (mLapCounterTextureRegion.getHeight() * 0.32f), mLapCounterTextureRegion);
        if ( camera_width > 360 ) {
        	mLapCounter.setScaleX(0.5f * scale_ratio_x);
            mLapCounter.setScaleY(0.5f * scale_ratio_y);
        }
        else {
        	mLapCounter.setScaleX(0.3f);
            mLapCounter.setScaleY(0.3f);
        }
        
        
        mMainScene.attachChild(mLapCounter);
        mMainScene.attachChild(lapCount);
        
        mPositionScene = new CameraScene(mCamera);
        
        mMainScene.setOnSceneTouchListener(this);
        gameState = GAME_STATE_ACTIVE;
        
		return mMainScene;
	}
	private void createBarrierSpawnTimerHandler() {
		TimerHandler spriteTimerHandler;
		Random random = new Random();
	    float mEffectSpawnDelay = (float)generateRandomInteger(6, 8, random);
	    //float mEffectSpawnDelay = 1.5f;
	    spriteTimerHandler = new TimerHandler(mEffectSpawnDelay, true,
	    		new ITimerCallback() {

			        public void onTimePassed(TimerHandler pTimerHandler) {
			        	if ( carState != CAR_STATE_STOPPED )
			        		addBarrier();
			        }
	    		});

	    getEngine().registerUpdateHandler(spriteTimerHandler);
	}
	private void addBarrier() {
		Random random = new Random();
		int barrierPositionId = generateRandomInteger(Barrier.BARRIER_POSITION_LEFT, Barrier.BARRIER_POSITION_RIGHT, random);
		int barrierTypeId = generateRandomInteger(Barrier.BARRIER_TYPE_SANDBAG, Barrier.BARRIER_TYPE_NITROUS, random);
		Barrier barrier = generateBarrier(barrierTypeId, barrierPositionId);
		for ( Sprite barrierComponent: barrier.components ) {
			mMainScene.attachChild(barrierComponent);
		}
		//Log.i("Barrier added", String.valueOf(barrier.position));
		barriersToBeAdded.add(barrier);
		
	}
	/**
	 * @param pBarrierType
	 * @param pBarrierPosition
	 * @return
	 */
	private Barrier generateBarrier(int pBarrierType, int pBarrierPosition) {
		Barrier barrier = new Barrier();
		barrier.position = pBarrierPosition;
		barrier.components = new ArrayList<Sprite>();
		final FixtureDef barrierFixtureDef = PhysicsFactory.createFixtureDef(0.5f, 0.5f, 1.0f);
		float xPosition = 0.0f;
		float yPosition = 0.0f;
		switch ( pBarrierType ) {
		
		case Barrier.BARRIER_TYPE_SANDBAG:
			if ( pBarrierPosition == Barrier.BARRIER_POSITION_LEFT ) {
				xPosition = camera_width * 0.2f;
			}
			else if ( pBarrierPosition == Barrier.BARRIER_POSITION_RIGHT ) {
				xPosition = camera_width/2;
			}
			
			yPosition = mSandbagTextureRegion.getHeight();
			for ( int i = 1; i <= 1; i++ ) {
				Sprite barrierComponent = new Sprite(xPosition +(10* i), yPosition, mSandbagTextureRegion );
				if ( camera_width >= 360 ) {
					barrierComponent.setScaleX(2.0f * scale_ratio_x);
					barrierComponent.setScaleY(2.0f * scale_ratio_y);
				}
				else {
					barrierComponent.setScaleX(0.75f);
					barrierComponent.setScaleY(0.75f);
				}
				Body barrierBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, barrierComponent, BodyType.DynamicBody, barrierFixtureDef);
	            mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(barrierComponent, barrierBody, true, true));
	            barrierBody.setLinearVelocity(barrierVelocity);
	            barrierBody.setAngularDamping(10);
	            //barrierBody.setUserData("barrier");
	            barrierComponent.setUserData(barrierBody);
				barrier.components.add(barrierComponent);
				
			}
			barrier.type = Barrier.BARRIER_TYPE_SANDBAG;
			break;
		case Barrier.BARRIER_TYPE_CONES:
			
			if ( pBarrierPosition == Barrier.BARRIER_POSITION_LEFT ) {
				xPosition = camera_width * 0.2f;
			}
			else if ( pBarrierPosition == Barrier.BARRIER_POSITION_RIGHT ) {
				xPosition = camera_width/2;
			}
			
			yPosition = mTrafficConeTextureRegion.getHeight();
			for ( int i = 1; i <= 1; i++ ) {
				Sprite barrierComponent = new Sprite(xPosition +(10* i), yPosition, mTrafficConeTextureRegion );
				if ( camera_width >= 360 ) {
					barrierComponent.setScaleX(2.0f * scale_ratio_x);
					barrierComponent.setScaleY(2.0f * scale_ratio_y);
				}
				else {
					barrierComponent.setScaleX(0.75f);
					barrierComponent.setScaleY(0.75f);
				}
				Body barrierBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, barrierComponent, BodyType.DynamicBody, barrierFixtureDef);
	            mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(barrierComponent, barrierBody, true, true));
	            barrierBody.setLinearVelocity(barrierVelocity);
	            barrierBody.setAngularDamping(10);
	            //barrierBody.setUserData("barrier");
	            barrierComponent.setUserData(barrierBody);
				barrier.components.add(barrierComponent);
			}
			barrier.type = Barrier.BARRIER_TYPE_CONES;
			break;
		case Barrier.BARRIER_TYPE_POLICE:
			
			if ( pBarrierPosition == Barrier.BARRIER_POSITION_LEFT ) {
				xPosition = camera_width * 0.2f;
			}
			else if ( pBarrierPosition == Barrier.BARRIER_POSITION_RIGHT ) {
				xPosition = camera_width/2;
			}
			
			yPosition = mPoliceTextureRegion.getHeight();
			for ( int i = 1; i <= 1; i++ ) {
				Sprite barrierComponent = new Sprite(xPosition +(10* i), yPosition, mPoliceTextureRegion );
				barrierComponent.setScaleX(scale_ratio_x);
				barrierComponent.setScaleY(scale_ratio_y);
				
				Body barrierBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, barrierComponent, BodyType.DynamicBody, barrierFixtureDef);
	            mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(barrierComponent, barrierBody, true, true));
	            barrierBody.setLinearVelocity(barrierVelocity);
	            barrierBody.setAngularDamping(10);
	            //barrierBody.setUserData("barrier");
	            barrierComponent.setUserData(barrierBody);
	            
				barrier.components.add(barrierComponent);
			}
			barrier.type = Barrier.BARRIER_TYPE_POLICE;
			break;
		case Barrier.BARRIER_TYPE_NITROUS:
			if ( nitrousCount < 3 ) {
				if ( pBarrierPosition == Barrier.BARRIER_POSITION_LEFT ) {
					xPosition = camera_width * 0.2f;
				}
				else if ( pBarrierPosition == Barrier.BARRIER_POSITION_RIGHT ) {
					xPosition = camera_width/2;
				}
				
				yPosition = mNitrousTextureRegion.getHeight();
				for ( int i = 1; i <= 1; i++ ) {
					Sprite barrierComponent = new Sprite(xPosition +(10* i), yPosition, mNitrousTextureRegion );
					if ( camera_width >= 360 ) {
						barrierComponent.setScaleX(2.0f * scale_ratio_x);
						barrierComponent.setScaleY(2.0f * scale_ratio_y);
					}
					else {
						barrierComponent.setScaleX(0.75f);
						barrierComponent.setScaleY(0.75f);
					}
					MoveYModifier moveNitrousModifier = new MoveYModifier(10.0f, barrierComponent.getY(), mCamera.getHeight() + 100.0f);
					barrierComponent.registerEntityModifier(moveNitrousModifier);
					barrier.components.add(barrierComponent);
				}
				barrier.type = Barrier.BARRIER_TYPE_NITROUS;
				nitrousCount += 1;
			}
			else {
				// Generate a cone barrier if we've reached nitrous cap
				if ( pBarrierPosition == Barrier.BARRIER_POSITION_LEFT ) {
					xPosition = camera_width * 0.2f;
				}
				else if ( pBarrierPosition == Barrier.BARRIER_POSITION_RIGHT ) {
					xPosition = camera_width/2;
				}
				
				yPosition = mTrafficConeTextureRegion.getHeight();
				for ( int i = 1; i <= 1; i++ ) {
					Sprite barrierComponent = new Sprite(xPosition +(10* i), yPosition, mTrafficConeTextureRegion );
					if ( camera_width >= 360 ) {
						barrierComponent.setScaleX(2.0f * scale_ratio_x);
						barrierComponent.setScaleY(2.0f * scale_ratio_y);
					}
					else {
						barrierComponent.setScaleX(0.75f);
						barrierComponent.setScaleY(0.75f);
					}
					Body barrierBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, barrierComponent, BodyType.DynamicBody, barrierFixtureDef);
		            mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(barrierComponent, barrierBody, true, true));
		            barrierBody.setLinearVelocity(barrierVelocity);
		            barrierBody.setAngularDamping(10);
		            //barrierBody.setUserData("barrier");
		            barrierComponent.setUserData(barrierBody);
					barrier.components.add(barrierComponent);
				}
				barrier.type = Barrier.BARRIER_TYPE_CONES;
			}
			break;
			
			
		}
		return barrier;
	}
	public void removeBarrier(final Barrier barrier, Iterator<Barrier> it) {
    	runOnUpdateThread(new Runnable() {
    		public void run() {
    			for ( Sprite sprite: barrier.components) {
    				mMainScene.detachChild(sprite);
    			}
    		}
    	});
    	it.remove();
    }
	private void createTimeHandler() {
		TimerHandler timeTimerHandler;
    	float lSpeedDelay = 0.01f;
    	timeTimerHandler = new TimerHandler(lSpeedDelay, true, 
    			new ITimerCallback() {
    				public void onTimePassed(TimerHandler pTimerHandler) {
    					if ( carState != CAR_STATE_STOPPED ) {
    						updateTime();
    						updateDistance();
    					}
    				}
    			});
    	getEngine().registerUpdateHandler(timeTimerHandler);
	}
	private void updateTime() {
		timeValue++;
		time.setText(Common.formatIntoMMSS(timeValue));
	}
	private void updateDistance() {
		//converting form km/h to m/cs
		if ( distance_traveled < TRACK_LENGTH ) {
			distance_traveled = distance_traveled + ((float)speedValue/360);
			for ( Car opponent: opponents ) {
				opponent.distance_traveled += ((float)opponent.currentSpeed/360);
			}
			if ( distance_traveled > TRACK_LENGTH/3 && entered_secondlap == false ) {
				entered_secondlap = true;
				lapCount.setText("2");
			}
			if ( distance_traveled > (2 * TRACK_LENGTH)/3 && entered_thirdlap == false ) {
				entered_thirdlap = true;
				lapCount.setText("3");
			}
			Log.i("Distance", String.valueOf(distance_traveled));
		}
		else {
			int opponent_infront_count = 0;
			for ( Car opponent: opponents ) {
				if ( opponent.distance_traveled > distance_traveled ) {
					opponent_infront_count++;
				}
			}
			Common.position = opponent_infront_count + 1;
			stopGame();
		}
		
	}
	private void stopGame() {
		gameState = GAME_STATE_GAMEOVER;
		Common.user_score = timeValue;
		acceleratingSound.stop();
        final int posX = (int) (mCamera.getWidth() / 2 - mPositionsTextureRegionList.get(0)
            .getWidth() / 2);
        final int posY = (int) (mCamera.getHeight() / 2 - mPositionsTextureRegionList.get(0)
            .getHeight() / 2);
        final Sprite positionSprite = new Sprite(posX, posY, mPositionsTextureRegionList.get(Common.position-1));
        mPositionScene.attachChild(positionSprite);
        mPositionScene.setBackgroundEnabled(false);
        mMainScene.setChildScene(mPositionScene);
		mEngine.stop();

	}
	private void createSpeedHandler() {
		TimerHandler speedTimerHandler;
    	float lSpeedDelay = 0.1f;
    	speedTimerHandler = new TimerHandler(lSpeedDelay, true, 
    			new ITimerCallback() {
    				public void onTimePassed(TimerHandler pTimerHandler) {
    					mTimeCount++;
    					
    					//Log.i("Time", String.valueOf(mTimeCount));
    					if ( 0 < mTimeCount && mTimeCount <= 50) {
    						carState = CAR_STATE_STOPPED;
    						float xPosition = mCamera.getCenterX() - (mGreenLightTextureRegion.getWidth()/2);
    						float yPosition = mCamera.getCenterY() - (mGreenLightTextureRegion.getHeight()/2);
    						if ( !trafficLightSoundStarted ) {
    							trafficLightSound.play();
    							trafficLightSoundStarted = true;
    						}
    						
    						if ( mTimeCount == 20 ) {
    								mTrafficLight = new Sprite(xPosition, yPosition, 
    										mRedLightTextureRegion);
    								mMainScene.attachChild(mTrafficLight);
    						}
    						else if ( mTimeCount == 30  ) {
    								mMainScene.detachChild(mTrafficLight);
    								mTrafficLight = new Sprite(xPosition, yPosition, 
    										mYellowLightTextureRegion);
        							mMainScene.attachChild(mTrafficLight);
    							}
    						else if ( mTimeCount == 40  ) {
    								mMainScene.detachChild(mTrafficLight);
    								mTrafficLight = new Sprite(xPosition, yPosition, 
    										mGreenLightTextureRegion);
        							mMainScene.attachChild(mTrafficLight);
    							}
    						else if ( mTimeCount == 50  ) {
    								mMainScene.detachChild(mTrafficLight);
    								carState = CAR_STATE_ACCELERATING;
    								if ( camera_width < 360 ) {
    									initStripMovement(STRIP_SPEED_20);
    								}
    								else initStripMovement(STRIP_SPEED_12);
    							}
    					}
    					if ( 50 <= mTimeCount ) {
    						updateSpeed();
    						updateOpponents();
    						correctCarAngles();
    						handleStripMovement();

    						
    					}
    				}
    			});
    	getEngine().registerUpdateHandler(speedTimerHandler);
		
	}
	private void handleStripMovement() {
		if ( speedValue < 1 ) {
			for ( Sprite leftStripSprite: leftStrips ) {
				if ( (Integer)leftStripSprite.getUserData() == STRIP_MOVING ) {
					leftStripSprite.clearEntityModifiers();
					leftStripSprite.setUserData(STRIP_STOPPED);
				}
			}
			for ( Sprite rightStripSprite: rightStrips ) {
				if ( (Integer)rightStripSprite.getUserData() == STRIP_MOVING ) {
					rightStripSprite.clearEntityModifiers();
					rightStripSprite.setUserData(STRIP_STOPPED);
				}
			}
		}
		else {
			if ( (Integer)leftStrips.get(0).getUserData() == STRIP_STOPPED ) {
				if ( camera_width < 360 ) {
					initStripMovement(STRIP_SPEED_20);
				}
				else initStripMovement(STRIP_SPEED_12);
			}
		}
		
	}
	private void initStripMovement(float stripSpeed) {
		int i = 1;
		int j = 1;
		for ( Sprite leftStripSprite: leftStrips ) {
			MoveYModifier moveStripModifier = new MoveYModifier(stripSpeed * i, 
					leftStripSprite.getY(),mCamera.getMaxY() + 10, new IEntityModifier.IEntityModifierListener() {
						
						@Override
						public void onModifierStarted(IModifier<IEntity> pIModifier, IEntity pIEntity) {
							
						}
						
						@Override
						public void onModifierFinished(IModifier<IEntity> pIModifier, IEntity pIEntity) {
							pIModifier.reset();
							
						}
					});
			leftStripSprite.registerEntityModifier(moveStripModifier.deepCopy());
			
			leftStripSprite.setUserData(STRIP_MOVING);
			
			
			i = i + 1;
		}
		for ( Sprite rightStripSprite: rightStrips ) {
			MoveYModifier moveStripModifier = new MoveYModifier(stripSpeed * j, 
					rightStripSprite.getY(),mCamera.getMaxY() + 10, new IEntityModifier.IEntityModifierListener() {
						
						@Override
						public void onModifierStarted(IModifier<IEntity> pIModifier, IEntity pIEntity) {
							
						}
						
						@Override
						public void onModifierFinished(IModifier<IEntity> pIModifier, IEntity pIEntity) {
							pIModifier.reset();
							
						}
					});
			rightStripSprite.registerEntityModifier(moveStripModifier.deepCopy());
			rightStripSprite.setUserData(STRIP_MOVING);
			
			
			j = j + 1;
		}
	}
	private void correctCarAngles() {
		Body playerBody = (Body)playerCar.sprite.getUserData();
		if ( playerBody.getAngle() != initialAngle ) {
			playerBody.setAngularVelocity(initialAngle - playerBody.getAngle());
		}
		else {
			playerBody.setAngularVelocity(0);
		}
		if ( playerCar.sprite.getY() > mCamera.getHeight() || playerCar.sprite.getY() < mCamera.getMinY() ) {
			Vector2 resetVector = Vector2Pool.obtain(resetX/32, resetY/32);
			Vector2 linearZero = Vector2Pool.obtain(0,0);
			playerBody.setTransform(resetVector, initialAngle);
			playerBody.setLinearVelocity(linearZero);
			Vector2Pool.recycle(linearZero);
			Vector2Pool.recycle(resetVector);
		}
		for ( Car opponent: opponents ) {
			Body opponentBody = (Body)opponent.sprite.getUserData();
			if ( opponentBody.getAngle() != opponent.initialAngle ) {
				opponentBody.setAngularVelocity(opponent.initialAngle - opponentBody.getAngle());
			}
			else {
				opponentBody.setAngularVelocity(0);
			}
		}
	}
	private void updateSpeed() {
    	if ( carState == CAR_STATE_ACCELERATING ) {
    		if ( !accelerationSoundStarted ) {
    			acceleratingSound.play();
    			accelerationSoundStarted = true;
    		}
    		
    		if ( !(speedValue >= playerCar.getTopSpeed()) ) {
    			speedValue += acceleration_value;
    			if ( speedValue % 10 == 0 ) {
    				fpsValue -= 3;
    				acceleration_started = false;
    			}
    			if (!acceleration_started) {
    				mRoad.animate((long)fpsValue, true);
    				acceleration_started = true;
    			}
    		}
    	}
    	else if ( carState == CAR_STATE_DECELERATING ) {
    		acceleratingSound.pause();
    		accelerationSoundStarted = false;
    		crashSound.play();
    		if ( !(speedValue <= 0) ) {
    			speedValue -= 2;
    			if ( speedValue % 10 == 0 ) {
    				fpsValue += 3;
    				acceleration_started = true;
    			}
    			if ( acceleration_started ) {
    				mRoad.animate((long)fpsValue, true);
    				acceleration_started = false;
    			}
    			
    		}
    		else {
    			mRoad.stopAnimation();
    		}
    			
    	}
    	else if ( carState == CAR_STATE_CRASHED ) {
    		acceleratingSound.pause();
    		accelerationSoundStarted = false;
    		crashSound.play();
    		if ( speedValue < 30 ) {
    			speedValue = 0;
    			fpsValue += 50;
    		}
    		else {
    			speedValue -= 30;
    			fpsValue += 20;
    		}
    		mRoad.animate((long)fpsValue, true);
    		carState = CAR_STATE_ACCELERATING;
    	}
    	else if ( carState == CAR_STATE_STOPPED ) {
    		mRoad.stopAnimation();
    	}
    	else if ( carState == CAR_STATE_NITROUS ) {
    		if ( timeValue < (initial_nitrous_time + 300) ) {
    			acceleration_value = 4;
    			speedValue += acceleration_value;
    		}
    		else {
    			acceleration_value = 1;
    			carState = CAR_STATE_ACCELERATING;
    		}
    		
    	}
    	speed.setText(String.valueOf(speedValue));
    }
	private void updateOpponents() {
		Random random = new Random();
		int carId = generateRandomInteger(2, opponents.size() + 1, random);
		for ( Car opponent: opponents ) {
			//Log.i("Collision", String.valueOf(opponent.sprite.collidesWith(playerCar.sprite)));
			if ( opponent.currentSpeed < opponent.getTopSpeed() ) {
				opponent.currentSpeed += opponent.getAccelerationSpeed();
			}
			if ( opponent.currentSpeed != speedValue ) {
				Vector2 overtakeForce = Vector2Pool.obtain(0, (-(opponent.currentSpeed - speedValue))/10);
				Body oppBody = (Body)opponent.sprite.getUserData();
				oppBody.setLinearVelocity(overtakeForce);
				Vector2Pool.recycle(overtakeForce);
			}
			else {
				if ( opponent.getId() == carId ) {
					int xOrY = generateRandomInteger(1, 2, random);
					if ( xOrY == 1 ) {
						Vector2 moveAroundVector = Vector2Pool.obtain(generateRandomInteger(-3, 3, random), 0);
						Body oppBody = (Body)opponent.sprite.getUserData();
						oppBody.setLinearVelocity(moveAroundVector);
						Vector2Pool.recycle(moveAroundVector);
					}
					else if ( xOrY == 2 ) {
						Vector2 moveAroundVector = Vector2Pool.obtain(0, generateRandomInteger(-3, 3, random));
						Body oppBody = (Body)opponent.sprite.getUserData();
						oppBody.setLinearVelocity(moveAroundVector);
						Vector2Pool.recycle(moveAroundVector);
					}
					
				}
				
				
			}
			
			
			//Log.i("Opponent Speed", String.valueOf(opponent.currentSpeed));
				
			
		}
	}
	

	public boolean onSceneTouchEvent(Scene arg0, TouchEvent event) {
		if ( event.getAction() == android.view.MotionEvent.ACTION_DOWN ) {
			carState = CAR_STATE_DECELERATING;
		}
		else if ( event.getAction() == android.view.MotionEvent.ACTION_UP ) {
			carState = CAR_STATE_ACCELERATING;
		}
		
		return true;
	}
	
	private static int generateRandomInteger(int aStart, int aEnd, Random aRandom){
	    if ( aStart > aEnd ) {
	      throw new IllegalArgumentException("Start cannot exceed End.");
	    }
	    //get the range, casting to long to avoid overflow problems
	    long range = (long)aEnd - (long)aStart + 1;
	    // compute a fraction of the range, 0 <= frac < range
	    long fraction = (long)(range * aRandom.nextDouble());
	    int randomNumber =  (int)(fraction + aStart);    
	    return randomNumber;
	  }
	
	@Override
	public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
		if ( pKeyCode == KeyEvent.KEYCODE_BACK && gameState == GAME_STATE_GAMEOVER ) {
			BufferObjectManager.getActiveInstance().clear();
			Intent userDetailsIntent =  new Intent(this, UserDetailsActivity.class);
			startActivity(userDetailsIntent);
			this.finish();
		}
		else if ( pKeyCode == KeyEvent.KEYCODE_BACK ) {
			Common.user_score = timeValue;
			BufferObjectManager.getActiveInstance().clear();
			mEngine.stop();
			this.finish();
		}
		return super.onKeyDown(pKeyCode, pEvent);
	}
}
