package org.app.game;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.util.color.Color;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.KeyEvent;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

/**
 * 游戏第三关
 * 
 * @author leaf
 * 
 */
public class LevelThreeGame extends MainGameActivity {

	@Override
	protected synchronized void onCreateGame() {
		showReadyDialog();
		super.onCreateGame();
	}

	@Override
	protected Scene onCreateScene() {
		Scene mScene = super.onCreateScene();

		return mScene;
	}

	@Override
	protected void initBomb() {
		addBomb(SCREEN_WIDTH / 2, BASE_LENGTH - BOMB_SIZE);
		addBomb(SCREEN_WIDTH * 3 / 4, 2);
		addBomb(SCREEN_WIDTH - BASE_LENGTH / 2, BASE_LENGTH / 2);
		addBomb(SCREEN_WIDTH / 2, SCREEN_HEIGHT - BASE_LENGTH / 2);

		addBomb(SCREEN_WIDTH - BASE_LENGTH, SCREEN_HEIGHT - BASE_LENGTH);
		addBomb(SCREEN_WIDTH - BOMB_SIZE - 10, SCREEN_HEIGHT - BOMB_SIZE - 10);

		addBomb(SCREEN_WIDTH / 2 + BOMB_SIZE + 5, 2);
		addBomb(SCREEN_WIDTH - BASE_LENGTH - 5, SCREEN_HEIGHT - BOMB_SIZE);
		addBomb(SCREEN_WIDTH - BASE_LENGTH - 12 - BOMB_SIZE, SCREEN_HEIGHT
				- BASE_LENGTH / 2);
	}

	@Override
	protected void initRoad(ITextureRegion mRacetrackStraightTextureRegion,
			ITextureRegion mRacetrackCurveTextureRegion) {

		Scene mScene = getScene();
		// 直线
		final ITextureRegion racetrackHorizontalStraightTextureRegion = mRacetrackStraightTextureRegion
				.deepCopy();
		racetrackHorizontalStraightTextureRegion
				.setTextureWidth(3 * mRacetrackStraightTextureRegion.getWidth());
		final ITextureRegion racetrackVerticalStraightTextureRegion = mRacetrackStraightTextureRegion;
		// 上直线
		mScene.attachChild(new Sprite(BASE_LENGTH, 0, 3 * BASE_LENGTH,
				BASE_LENGTH, racetrackHorizontalStraightTextureRegion, this
						.getVertexBufferObjectManager()));
		// 下直线
		mScene.attachChild(new Sprite(BASE_LENGTH, SCREEN_HEIGHT - BASE_LENGTH,
				3 * BASE_LENGTH, BASE_LENGTH,
				racetrackHorizontalStraightTextureRegion,
				getVertexBufferObjectManager()));

		// 左直线
		final Sprite leftVerticalStraight = new Sprite(0, BASE_LENGTH,
				BASE_LENGTH, BASE_LENGTH,
				racetrackVerticalStraightTextureRegion,
				getVertexBufferObjectManager());
		leftVerticalStraight.setRotation(90);
		mScene.attachChild(leftVerticalStraight);
		// 右直线
		final Sprite rightVerticalStraight = new Sprite(SCREEN_WIDTH
				- BASE_LENGTH, BASE_LENGTH, BASE_LENGTH, BASE_LENGTH,
				racetrackVerticalStraightTextureRegion,
				getVertexBufferObjectManager());
		rightVerticalStraight.setRotation(90);
		mScene.attachChild(rightVerticalStraight);

		// 画弯道
		final ITextureRegion racetrackCurveTextureRegion = mRacetrackCurveTextureRegion;
		// 右上
		final Sprite upperRightCurve = new Sprite(SCREEN_WIDTH - BASE_LENGTH,
				0, BASE_LENGTH, BASE_LENGTH, racetrackCurveTextureRegion,
				getVertexBufferObjectManager());
		upperRightCurve.setRotation(180);
		mScene.attachChild(upperRightCurve);

		// 右下
		final Sprite lowerRightCurve = new Sprite(SCREEN_WIDTH - BASE_LENGTH,
				SCREEN_HEIGHT - BASE_LENGTH, BASE_LENGTH, BASE_LENGTH,
				racetrackCurveTextureRegion, getVertexBufferObjectManager());
		lowerRightCurve.setRotation(270);
		mScene.attachChild(lowerRightCurve);

		// 左上
		final Sprite upperLeftCurve = new Sprite(0, 0, BASE_LENGTH,
				BASE_LENGTH, racetrackCurveTextureRegion,
				getVertexBufferObjectManager());
		upperLeftCurve.setRotation(90);
		mScene.attachChild(upperLeftCurve);
	}

	@Override
	protected void initBorders(PhysicsWorld mPhysicsWorld) {
		Scene mScene = getScene();

		// 左下角
		final Rectangle leftLowerRect = new Rectangle(0, SCREEN_HEIGHT
				- BASE_LENGTH, BASE_LENGTH, BASE_LENGTH,
				getVertexBufferObjectManager());
		leftLowerRect.setColor(Color.BLACK);
		// 上下左右外侧边界
		final Rectangle bottomOuter = new Rectangle(0, SCREEN_HEIGHT - 2,
				SCREEN_WIDTH, 2, getVertexBufferObjectManager());
		final Rectangle topOuter = new Rectangle(0, 0, SCREEN_WIDTH, 2,
				getVertexBufferObjectManager());
		final Rectangle leftOuter = new Rectangle(0, 0, 2, SCREEN_HEIGHT,
				getVertexBufferObjectManager());
		final Rectangle rightOuter = new Rectangle(SCREEN_WIDTH - 2, 0, 2,
				SCREEN_HEIGHT, getVertexBufferObjectManager());

		// 上下左右内测边界
		final Rectangle bottomInner = new Rectangle(BASE_LENGTH, SCREEN_HEIGHT
				- 2 - BASE_LENGTH, SCREEN_WIDTH - 2 * BASE_LENGTH, 2,
				getVertexBufferObjectManager());
		final Rectangle topInner = new Rectangle(BASE_LENGTH, BASE_LENGTH,
				SCREEN_WIDTH - 2 * BASE_LENGTH, 2,
				getVertexBufferObjectManager());
		final Rectangle leftInner = new Rectangle(BASE_LENGTH, BASE_LENGTH, 2,
				SCREEN_HEIGHT - 2 * BASE_LENGTH, getVertexBufferObjectManager());
		final Rectangle rightInner = new Rectangle(SCREEN_WIDTH - 2
				- BASE_LENGTH, BASE_LENGTH, 2, SCREEN_HEIGHT - 2 * BASE_LENGTH,
				getVertexBufferObjectManager());

		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0,
				0.5f, 0.5f);
		PhysicsFactory.createBoxBody(mPhysicsWorld, leftLowerRect,
				BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(mPhysicsWorld, bottomOuter,
				BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(mPhysicsWorld, topOuter,
				BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(mPhysicsWorld, leftOuter,
				BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(mPhysicsWorld, rightOuter,
				BodyType.StaticBody, wallFixtureDef);

		PhysicsFactory.createBoxBody(mPhysicsWorld, bottomInner,
				BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(mPhysicsWorld, topInner,
				BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(mPhysicsWorld, leftInner,
				BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(mPhysicsWorld, rightInner,
				BodyType.StaticBody, wallFixtureDef);

		mScene.attachChild(leftLowerRect);

		mScene.attachChild(bottomOuter);
		mScene.attachChild(topOuter);
		mScene.attachChild(leftOuter);
		mScene.attachChild(rightOuter);

		mScene.attachChild(bottomInner);
		mScene.attachChild(topInner);
		mScene.attachChild(leftInner);
		mScene.attachChild(rightInner);
	}

	@Override
	protected void initCar(TiledTextureRegion mCarTextureRegion,
			PhysicsWorld mPhysicsWorld) {
		super.initCar(mCarTextureRegion, mPhysicsWorld,
				(BASE_LENGTH - BOMB_SIZE) / 2, SCREEN_HEIGHT - BASE_LENGTH
						- CAR_SIZE - 5);

	}

	@Override
	protected void initSwitcher(ITextureRegion mSwitcherTextureRegion,
			PhysicsWorld mPhysicsWorld) {
		super.initSwitcher(mSwitcherTextureRegion, mPhysicsWorld,
				BASE_LENGTH + 2, SCREEN_HEIGHT - (BASE_LENGTH - BOMB_SIZE) / 2);
	}


	/**
	 * 显示准备对话框
	 */
	public void showReadyDialog() {
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				new AlertDialog.Builder(LevelThreeGame.this)
						.setTitle("准备好了吗？")
						.setPositiveButton("开始游戏",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										startGame();
									}
								})
						.setNegativeButton("退出",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										System.exit(0);
									}
								}).show();
			}
		});
	}

	@Override
	protected void gameOver() {
		super.gameOver();
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				new AlertDialog.Builder(LevelThreeGame.this)
						.setTitle("这样都输！比三岁小孩子还菜！")
						.setPositiveButton("重来",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface arg0,
											int arg1) {
										LevelThreeGame.this.restart();
									}
								})
						.setNegativeButton("退出",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										System.exit(0);
									}
								}).show();
			}
		});

	}

	@Override
	protected void winGame() {
		super.winGame();

		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				new AlertDialog.Builder(LevelThreeGame.this)
						.setTitle("恭喜你已经完成所有关卡")
						.setNegativeButton("退出",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										System.exit(0);
									}
								}).show();
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			stopGame();
			new AlertDialog.Builder(LevelThreeGame.this)
					.setTitle("真要退出?")
					.setPositiveButton("退出",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									System.exit(0);
								}
							})
					.setNegativeButton("返回",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									startGame();
								}
							}).show();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@SuppressLint("NewApi")
	@Override
	public void restart() {
		super.restart();
	}

}
