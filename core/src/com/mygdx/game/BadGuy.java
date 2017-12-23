package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.*;


public class BadGuy extends Actor {
    Texture img;
    TextureRegion idleFrame;
    Animation<TextureRegion> walkAnimation;
    Rectangle rectangle;

    private final float GRAVITY = -2.5f;
    private final float MAX_VELOCITY = 8f;
    private final float DAMPING = 0.7f;

    boolean isdead = false;
    boolean isFacingRight = false;
    float xVelocity = MAX_VELOCITY / 4;
    float yVelocity = 0;
    float time = 0;

    TiledMapTileLayer wall;
    TiledMapTileLayer death;
    TiledMapTileLayer staticwall;
    TiledMapTileLayer badwall;

    public BadGuy() {
        img = new Texture(Gdx.files.internal("evil1.png"));
        TextureRegion[][] tmp = TextureRegion.split(img, img.getWidth() / 5, img.getHeight());
        this.setSize(1, img.getHeight() / img.getHeight());
        TextureRegion[] idleFrames = new TextureRegion[1];
        idleFrames[0] = tmp[0][0];
        TextureRegion[] walkFrames = new TextureRegion[4];
        walkFrames[0] = tmp[0][1];
        walkFrames[2] = tmp[0][3];
        walkFrames[3] = tmp[0][4];
        walkFrames[1] = tmp[0][2];
        walkAnimation = new Animation<TextureRegion>(0.25f, walkFrames);
        walkAnimation.setPlayMode(Animation.PlayMode.LOOP);
        idleFrame = idleFrames[0];

        rectangle = new Rectangle();
        rectangle.set(getX(), getY(), getWidth() * 0.8f, getHeight());
    }

    @Override
    public void act(float delta) {
        time += delta;

        yVelocity += (GRAVITY);
        float x = this.getX();
        float y = this.getY();
        float xChange = xVelocity * delta;
        float yChange = yVelocity * delta;
        if (canMoveTo(x + xChange, y) == false) {
            xVelocity = xVelocity * (-1);
            xChange = 0;
        }
        if (canMoveTo(x, y + yChange) == false) {
            yVelocity = yChange = 0;
        }
        this.setPosition(x + xChange, y + yChange);    //更新位置
        rectangle.setPosition(getX(), getY());

        if (xVelocity > 0)
            isFacingRight = true;
        if (xVelocity < 0)
            isFacingRight = false;

        if (isdead)
            remove();
    }

    public void draw(Batch batch, float parentAlpha) {
        if (xVelocity != 0) {
            if (isFacingRight)
                batch.draw(walkAnimation.getKeyFrame(time), this.getX() + this.getWidth(), this.getY(), -1 * this.getWidth(), this.getHeight());
            else
                batch.draw(walkAnimation.getKeyFrame(time), this.getX(), this.getY(), this.getWidth(), this.getHeight());
        } else {
            if (isFacingRight)
                batch.draw(idleFrame, this.getX(), this.getY(), -1 * this.getWidth(), this.getHeight());
            else
                batch.draw(idleFrame, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        }
    }

    private boolean canMoveTo(float startX, float startY) {
        float endX = startX + this.getWidth();
        float endY = startY + this.getHeight();

        int x = (int) startX;
        while (x < endX) {
            int y = (int) startY;
            while (y < endY) {
                if (death.getCell(x, y) != null)
                    isdead = true;
                if (wall.getCell(x, y) != null) {
                    return false;
                }
                if (staticwall.getCell(x, y) != null)
                    return false;
                if (badwall.getCell(x, y) != null)
                    return false;
                y++;
            }
            x++;
        }
        return true;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }
}
