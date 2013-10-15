package org.bombexpert.game;

import java.io.IOException;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.math.MathUtils;
import org.bombexpert.utils.LevelManager;
import org.bombexpert.utils.Timer;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.opengl.GLES20;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

/**
 * 游戏基本类
 * 
 * @author leaf
 * 
 */
public abstract class MainGameActivity extends SimpleBaseGameActivity {
	protected static final int BASE_LENGTH = 64;
	protected static final int SCREEN_WIDTH = 5 * BASE_LENGTH;
	protected static final int SCREEN_HEIGHT = 3 * BASE_LENGTH;
	protected static final int CAR_SIZE = 16;
	protected static final int BOMB_SIZE = 16;
	protected static final int SWITCHER_SIZE = 16;
	
	private static final String CAR = "CAR";
	private static final String BOMB = "BOMB";
	private static final String SWEITCHER = "SWEITCHER";

	protected Camera mCamera;

	protected Font mTimeFont;

	private Scene mScene;

	private BitmapTextureAtlas mCarTextureAtlas;
	private TiledTextureRegion mCarTextureRegion;

	private BitmapTextureAtlas mBombTextureAtlas;
	private ITextureRegion mBombTextureRegion;

	private BitmapTextureAtlas mSwitcherTextureAtlas;
	private TiledTextureRegion mSwitcherTextureRegion;

	private BitmapTextureAtlas mRacetrackTexture;
	private ITextureRegion mRacetrackStraightTextureRegion;
	private ITextureRegion mRacetrackCurveTextureRegion;

	private BitmapTextureAtlas mOnScreenControlTexture;
	private ITextureRegion mOnScreenControlBaseTextureRegion;
	private ITextureRegion mOnScreenControlKnobTextureRegion;

	private Music mBaseMusic;
	private Sound mBombSound;

	private Timer mTimer;
	private Text mTimeText;

	private PhysicsWorld mPhysicsWorld;

	protected Body mCarBody;
	private TiledSprite mCar;
	
	protected TiledSprite mSwitcher;

	protected boolean isGameRunning = false;

	@Override
	public EngineOptions onCreateEngineOptions() {
		this.mCamera = new Camera(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

		final EngineOptions engineOptions = new EngineOptions(true,
				ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(
						SCREEN_WIDTH, SCREEN_HEIGHT), this.mCamera);
		engineOptions.getAudioOptions().setNeedsSound(true);
		engineOptions.getAudioOptions().setNeedsMusic(true);
		return engineOptions;
	}

	@Override
	protected void onCreateResources() {
		// 加载字体
		FontFactory.setAssetBasePath("font/");

		this.mTimeFont = FontFactory.createFromAsset(this.getFontManager(),
				this.getTextureManager(), 512, 512, TextureOptions.BILINEAR,
				this.getAssets(), "LCD.ttf", 10, true, Color.WHITE);

		this.mTimeFont.load();

		// 加载图片
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		// 车
		this.mCarTextureAtlas = new BitmapTextureAtlas(getTextureManager(),
				128, 16, TextureOptions.BILINEAR);
		this.mCarTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.mCarTextureAtlas, this, "cars.png",
						0, 0, 6, 1);
		this.mCarTextureAtlas.load();

		// 赛道
		this.mRacetrackTexture = new BitmapTextureAtlas(
				this.getTextureManager(), 128, 256,
				TextureOptions.REPEATING_NEAREST);
		this.mRacetrackStraightTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mRacetrackTexture, this,
						"racetrack_straight.png", 0, 0);
		this.mRacetrackCurveTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mRacetrackTexture, this,
						"racetrack_curve.png", 0, 128);
		this.mRacetrackTexture.load();

		// 控制键
		this.mOnScreenControlTexture = new BitmapTextureAtlas(
				this.getTextureManager(), 128, 64, TextureOptions.BILINEAR);
		this.mOnScreenControlBaseTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mOnScreenControlTexture, this,
						"onscreen_control_base.png", 0, 0);
		this.mOnScreenControlKnobTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mOnScreenControlTexture, this,
						"onscreen_control_knob.png", 64, 0);
		this.mOnScreenControlTexture.load();

		// 炸弹
		this.mBombTextureAtlas = new BitmapTextureAtlas(
				this.getTextureManager(), 32, 32, TextureOptions.BILINEAR);
		this.mBombTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mBombTextureAtlas, this, "bomb.png", 0, 0);
		this.mBombTextureAtlas.load();

		// 开关
		this.mSwitcherTextureAtlas = new BitmapTextureAtlas(
				this.getTextureManager(), 64, 32, TextureOptions.BILINEAR);
		this.mSwitcherTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.mSwitcherTextureAtlas, this,
						"face_tiled.png", 0, 0, 2, 1);
		this.mSwitcherTextureAtlas.load();

		// 加载声音
		SoundFactory.setAssetBasePath("mfx/");
		MusicFactory.setAssetBasePath("mfx/");
		try {
			this.mBaseMusic = MusicFactory.createMusicFromAsset(
					this.getMusicManager(), this, "base.wav");
			this.mBombSound = SoundFactory.createSoundFromAsset(
					this.getSoundManager(), this, "bomb.wav");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected Scene onCreateScene() {
		getEngine().registerUpdateHandler(new FPSLogger());

		this.mScene = new Scene();
		this.mScene.setBackground(new Background(0, 0, 0));

		this.mPhysicsWorld = new FixedStepPhysicsWorld(30, new Vector2(0, 0),
				false, 8, 1);
		
		initRoad(this.mRacetrackStraightTextureRegion,
				this.mRacetrackCurveTextureRegion);
		initBorders(this.mPhysicsWorld);
		initCar(this.mCarTextureRegion, this.mPhysicsWorld);
		initOnScreenControls();
		initBomb();
		initSwitcher(this.mSwitcherTextureRegion, this.mPhysicsWorld);
		initHandler();
		
		this.mPhysicsWorld.setContactListener(new ContactListener() {
			
			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {}
			
			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {}
			
			@Override
			public void endContact(Contact contact) {}
			
			@Override
			public void beginContact(Contact contact) {
				Body bodyA = contact.getFixtureA().getBody();
				Body bodyB = contact.getFixtureB().getBody();
				if (bodyA != null && bodyB != null) {
					if (bodyA.getUserData() != null
							&& bodyB.getUserData() != null) {
						if ((bodyA.getUserData().equals(CAR) && bodyB
								.getUserData().equals(BOMB))
								|| (bodyA.getUserData().equals(BOMB) && bodyB
										.getUserData().equals(CAR))) {
							gameOver();
						}
					}
					if (bodyA.getUserData() != null
							&& bodyB.getUserData() != null) {
						if ((bodyA.getUserData().equals(CAR) && bodyB
								.getUserData().equals(SWEITCHER))
								|| (bodyA.getUserData().equals(SWEITCHER) && bodyB
										.getUserData().equals(CAR))) {
							MainGameActivity.this.mSwitcher.setCurrentTileIndex(1);
							winGame();
						}
					}
				}
			}
		});
		
		return this.mScene;
	}
	
	

	/**
	 * 初始化计时器
	 */
	public void initTimer() {
		switch (LevelManager.getLevel()) {
		case 1:
			this.mTimer = new Timer(1, 0);
			break;
		case 2:
			this.mTimer = new Timer(0, 30);
			break;
		case 3:
			this.mTimer = new Timer(0, 15);
			break;
		default:
			this.mTimer = new Timer(1, 0);
			break;
		}
	}

	/**
	 * 初始化注册Handler
	 */
	public void initHandler() {
		this.mScene.registerUpdateHandler(this.mPhysicsWorld);

		initTimer();
		this.mTimeText = new Text(5, 5, this.mTimeFont, this.mTimer.next(),
				"00:00 00".length(), this.getVertexBufferObjectManager());
		this.mTimeText.setBlendFunction(GLES20.GL_SRC_ALPHA,
				GLES20.GL_ONE_MINUS_SRC_ALPHA);
		mScene.attachChild(mTimeText);

		// 时间倒数
		mScene.registerUpdateHandler(new TimerHandler(0.01f, true,
				new ITimerCallback() {

					@Override
					public void onTimePassed(TimerHandler pTimerHandler) {
						if (MainGameActivity.this.isGameRunning) {
							if (!MainGameActivity.this.mTimer.isFinish()) {
								MainGameActivity.this.mTimeText
										.setText(MainGameActivity.this.mTimer
												.next());
							} else {
								MainGameActivity.this.mTimeText
										.setText("00:00 00");
								gameOver();
							}
						}
					}
				}));

	}

	/**
	 * 初始化道路
	 * 
	 * @param mRacetrackStraightTextureRegion
	 *            直线
	 * @param mRacetrackCurveTextureRegion
	 *            转角
	 */
	protected abstract void initRoad(
			ITextureRegion mRacetrackStraightTextureRegion,
			ITextureRegion mRacetrackCurveTextureRegion);

	/**
	 * 初始化边界
	 * 
	 * @param mPhysicsWorld
	 */
	protected abstract void initBorders(PhysicsWorld mPhysicsWorld);

	/**
	 * 初始化炸弹位置
	 */
	protected abstract void initBomb();

	/**
	 * 初始化开关位置
	 * 
	 * @param mSwitcherTextureRegion
	 * @param mPhysicsWorld
	 */
	protected void initSwitcher(ITextureRegion mSwitcherTextureRegion,
			PhysicsWorld mPhysicsWorld) {
		initSwitcher(mSwitcherTextureRegion, mPhysicsWorld, 0, 0);
	}

	protected void initSwitcher(ITextureRegion mSwitcherTextureRegion,
			PhysicsWorld mPhysicsWorld, float x, float y) {
		this.mSwitcher = new TiledSprite(x, y, SWITCHER_SIZE, SWITCHER_SIZE,
				this.mSwitcherTextureRegion,
				this.getVertexBufferObjectManager());
		this.mSwitcher.setCurrentTileIndex(0);

		final FixtureDef bombFixtureDef = PhysicsFactory.createFixtureDef(0.1f,
				0.5f, 0.5f);
		final Body mSwitcherBody = PhysicsFactory.createBoxBody(mPhysicsWorld,
				mSwitcher, BodyType.StaticBody, bombFixtureDef);
		mSwitcherBody.setUserData(SWEITCHER);
		mSwitcherBody.setLinearDamping(10);
		mSwitcherBody.setAngularDamping(10);

		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(
				this.mSwitcher, mSwitcherBody, true, true));

		getScene().attachChild(this.mSwitcher);

	}

	/**
	 * 初始化汽车
	 * 
	 * @param mCarTextureRegion
	 * @param mPhysicsWorld
	 */
	protected void initCar(TiledTextureRegion mCarTextureRegion,
			PhysicsWorld mPhysicsWorld) {
		initCar(mCarTextureRegion, mPhysicsWorld, 0, 0);
	}

	protected void initCar(TiledTextureRegion mCarTextureRegion,
			PhysicsWorld mPhysicsWorld, float startX, float startY) {
		Scene mScene = getScene();
		this.mCar = new TiledSprite(startX, startY, CAR_SIZE, CAR_SIZE,
				mCarTextureRegion, this.getVertexBufferObjectManager());
		this.mCar.setCurrentTileIndex(LevelManager.getLevel() % 6);

		final FixtureDef carFixtureDef = PhysicsFactory.createFixtureDef(1,
				0.5f, 0.5f);
		this.mCarBody = PhysicsFactory.createBoxBody(mPhysicsWorld, this.mCar,
				BodyType.DynamicBody, carFixtureDef);
		this.mCarBody.setUserData(CAR);

		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(this.mCar,
				this.mCarBody, true, false));

		mScene.attachChild(this.mCar);
	}

	/**
	 * 初始化控制按钮
	 */
	protected void initOnScreenControls() {
		final AnalogOnScreenControl analogOnScreenControl = new AnalogOnScreenControl(
				0, SCREEN_HEIGHT
						- this.mOnScreenControlBaseTextureRegion.getHeight(),
				this.mCamera, this.mOnScreenControlBaseTextureRegion,
				this.mOnScreenControlKnobTextureRegion, 0.1f,
				this.getVertexBufferObjectManager(),
				new IAnalogOnScreenControlListener() {
					@Override
					public void onControlChange(
							final BaseOnScreenControl pBaseOnScreenControl,
							final float pValueX, final float pValueY) {
						if (isGameRunning) {
							final Body carBody = MainGameActivity.this.mCarBody;

							final Vector2 velocity = Vector2Pool.obtain(
									pValueX * 5, pValueY * 5);
							carBody.setLinearVelocity(velocity);
							Vector2Pool.recycle(velocity);

							final float rotationInRad = (float) Math.atan2(
									-pValueX, pValueY);
							carBody.setTransform(carBody.getWorldCenter(),
									rotationInRad);

							MainGameActivity.this.mCar.setRotation(MathUtils
									.radToDeg(rotationInRad));
						}
					}

					@Override
					public void onControlClick(
							final AnalogOnScreenControl pAnalogOnScreenControl) {
						/* Nothing. */
					}
				});
		analogOnScreenControl.getControlBase().setBlendFunction(
				GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		analogOnScreenControl.getControlBase().setAlpha(0.5f);
		analogOnScreenControl.refreshControlKnobPosition();

		this.mScene.setChildScene(analogOnScreenControl);
	}

	/**
	 * 添加炸弹
	 * 
	 * @param pX
	 * @param pY
	 */
	protected void addBomb(final float pX, final float pY) {
		final Sprite bomb = new Sprite(pX, pY, BOMB_SIZE, BOMB_SIZE,
				this.mBombTextureRegion, this.getVertexBufferObjectManager());

		final FixtureDef bombFixtureDef = PhysicsFactory.createFixtureDef(0.1f,
				0.5f, 0.5f);
		final Body bombBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld,
				bomb, BodyType.StaticBody, bombFixtureDef);
		 bombBody.setLinearDamping(10);
		 bombBody.setAngularDamping(10);

		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(bomb,
				bombBody, true, true));
		bombBody.setUserData(BOMB);

		this.mScene.attachChild(bomb);

	}

	/**
	 * 游戏失败
	 */
	protected void gameOver() {
		mBombSound.play();
		isGameRunning = false;
		mScene.unregisterUpdateHandler(mPhysicsWorld);
		this.mBaseMusic.pause();
	}

	/**
	 * 游戏胜利
	 */
	protected void winGame() {
		isGameRunning = false;
		mScene.unregisterUpdateHandler(mPhysicsWorld);
		this.mBaseMusic.pause();
	}

	/**
	 * 游戏开始
	 */
	public void startGame() {
		isGameRunning = true;
		this.mBaseMusic.play();
		this.mBaseMusic.setLooping(true);
	}

	public void stopGame() {
		isGameRunning = false;
		this.mBaseMusic.pause();
	}
	
	/**
	 * 重新开始
	 */
	@SuppressLint("NewApi")
	public void restart() {
		this.recreate();
	}

	protected Scene getScene() {
		return this.mScene;
	}

	protected TiledSprite getCar() {
		return this.mCar;
	}
	
}
