package com.mygdx.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.math.Rectangle;

public class Knight extends BadGuy {
    Animation<TextureRegion> walkRightAnimation;
    Animation<TextureRegion> walkLeftAnimation;
    Animation<TextureRegion> waveAnimation;
    TextureRegion idleFrame;
    TextureRegion jumpFrame;
    Texture img;
    Sound step;
    Sound block;
    Sound dead;

    private final float GRAVITY = -2.5f;
    private final float MAX_VELOCITY = 8f;
    private final float DAMPING = 0.7f;
    private final float TIME_BETWEEN_STEP = 0.3f;
    SpriteBatch batch;

    float time = 0;
    boolean canJump = false;
    boolean iswalking = false;
    boolean isdead;
    float xVelocity = 0;
    float yVelocity = 0;
    boolean isFacingRight = true;
    double mTimeToNextStep = 0;
    TiledMapTileLayer wall;
    TiledMapTileLayer death;
    TiledMapTileLayer staticwall;
    Rectangle rectangle;


    public Knight() {
        img = new Texture(Gdx.files.internal("simpleguy.png"));
        batch = new SpriteBatch();
        TextureRegion[][] tmp = TextureRegion.split(img, img.getWidth() / 8, img.getHeight());
        this.setSize(getWidth()/getHeight(), 1);
        TextureRegion[] idleFrames = new TextureRegion[1];
        idleFrames[0] = tmp[0][0];
        TextureRegion[] walkRightFrames = new TextureRegion[2];
        walkRightFrames[0] = tmp[0][1];
        walkRightFrames[1] = tmp[0][2];
        TextureRegion[] walkLeftFrames = new TextureRegion[2];
        walkLeftFrames[0] = tmp[0][3];
        walkLeftFrames[1] = tmp[0][4];
        TextureRegion[] jumpFrames = new TextureRegion[1];
        jumpFrames[0] = tmp[0][5];
        TextureRegion[] waveFrames = new TextureRegion[2];
        waveFrames[0] = tmp[0][6];
        waveFrames[1] = tmp[0][7];

        walkRightAnimation = new Animation<TextureRegion>(0.25f, walkRightFrames);
        walkLeftAnimation = new Animation<TextureRegion>(0.25f, walkLeftFrames);
        waveAnimation = new Animation<TextureRegion>(0.15f, waveFrames);
        idleFrame = idleFrames[0];
        jumpFrame = jumpFrames[0];
        walkLeftAnimation.setPlayMode(Animation.PlayMode.LOOP);
        walkRightAnimation.setPlayMode(Animation.PlayMode.LOOP);
        waveAnimation.setPlayMode(Animation.PlayMode.LOOP);

        step = Gdx.audio.newSound(Gdx.files.internal("footstep.wav"));
        block = Gdx.audio.newSound(Gdx.files.internal("block.wav"));
        dead = Gdx.audio.newSound(Gdx.files.internal("dead.wav"));

        rectangle = new Rectangle();
        rectangle.set(getX(), getY(), getWidth() * 0.8f, getHeight());
        isdead = false;
    }

    public void act(float delta) {
        time += delta;

        if (xVelocity != 0 && canJump)          //脚步
            iswalking = true;
        else iswalking = false;

        if (iswalking) {
            mTimeToNextStep -= delta;
            if (mTimeToNextStep < 0) {
                step.play();
                while (mTimeToNextStep < 0)
                    mTimeToNextStep += TIME_BETWEEN_STEP;
            }
        } else {
            mTimeToNextStep = 0.05f;
        }

        //控制
        boolean upTouched = Gdx.input.isTouched() && Gdx.input.getY() < Gdx.graphics.getHeight() / 2;
        if (upTouched || Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            if (canJump) {
                yVelocity += MAX_VELOCITY * 4;
                canJump = false;
            }
        }

        boolean leftTouched = Gdx.input.isTouched() && Gdx.input.getX() < Gdx.graphics.getWidth() / 3;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || leftTouched) {
            xVelocity = -1 * MAX_VELOCITY;
            isFacingRight = false;
        }

        boolean rightTouched = Gdx.input.isTouched() && Gdx.input.getX() > Gdx.graphics.getWidth() * 2 / 3;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || rightTouched) {
            xVelocity = MAX_VELOCITY;
            isFacingRight = true;

        }
        yVelocity += (GRAVITY);
        float x = this.getX();
        float y = this.getY();
        float xChange = xVelocity * delta;
        float yChange = yVelocity * delta;


        if (canMoveTo(x + xChange, y, false) == false) {
            xVelocity = xChange = 0;
        }

        if (canMoveTo(x, y + yChange, yVelocity > 0) == false) {
            canJump = (yVelocity < 0);
            yVelocity = yChange = 0;
        }
        if (isdead) {
            x = 3;
            y = 5;
            isdead = false;
            dead.play();
        }


        this.setPosition(x + xChange, y + yChange);    //更新位置

        rectangle.setPosition(getX(), getY());
        xVelocity = xVelocity * DAMPING;
        if (Math.abs(xVelocity) < 0.5f) {
            xVelocity = 0;
        }

    }

    public void draw(Batch batch, float parentAlpha) {          //动画
        if (yVelocity != 0)
            batch.draw(jumpFrame, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        else if (xVelocity != 0)
            if (!isFacingRight) {
                batch.draw(walkRightAnimation.getKeyFrame(time), this.getX(), this.getY(), this.getWidth(), this.getHeight());
            } else {
                batch.draw(walkLeftAnimation.getKeyFrame(time), this.getX(), this.getY(), this.getWidth(), this.getHeight());
            }
        else {
            boolean centertouched = Gdx.input.isTouched() && Gdx.input.getX() > Gdx.graphics.getWidth() *2/ 5 &&
                    Gdx.input.getX() < Gdx.graphics.getWidth() * 3 / 5;
            if (Gdx.input.isKeyPressed(Input.Keys.UP)||centertouched) {
                batch.draw(waveAnimation.getKeyFrame(time), this.getX(), this.getY(), this.getWidth(), this.getHeight());
            } else batch.draw(idleFrame, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        }

    }

    private boolean canMoveTo(float startX, float startY, boolean shouldDestroy) {
        float endX = startX + this.getWidth() / 2;
        float endY = startY + this.getHeight();

        int x = (int) startX;
        while (x < endX) {
            int y = (int) startY;
            while (y < endY) {
                if (death.getCell(x, y) != null)
                    isdead = true;
                if (wall.getCell(x, y) != null) {
                    if (shouldDestroy) {
                        wall.setCell(x, y, null);
                        block.play();
                    }
                    return false;
                }
                if (staticwall.getCell(x, y) != null)
                    return false;
                y = y + 1;
            }
            x = x + 1;
        }
        return true;
    }


    public boolean isCanJump() {
        return canJump;
    }

    public boolean isdead() {
        return isdead;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }
}

