package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Sword extends Actor {
    Texture swordImg;
    TextureRegion idleFrame;
    private final float GRAVITY = -1f;

    Rectangle rectangle;
    TiledMapTileLayer staticwall;

    float xVelocity = 0;
    float yVelocity = 0;
    float time = 0;

    public Sword() {
        swordImg = new Texture(Gdx.files.internal("sword.jpg"));
        this.setSize(1, swordImg.getHeight()*3 / swordImg.getHeight());
        rectangle = new Rectangle();
        idleFrame = new TextureRegion(swordImg);
        rectangle.set(getX(), getY(), this.getWidth() * 0.8f, this.getHeight());
    }

    @Override
    public void act(float delta) {
        time+=delta;
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
        if(time>2){
            remove();
        }

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(idleFrame, this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }

    private boolean canMoveTo(float startX, float startY) {
        float endX = startX + this.getWidth();
        float endY = startY + this.getHeight();

        int x = (int) startX;
        while (x < endX) {
            int y = (int) startY;
            while (y < endY) {
                if (staticwall.getCell(x, y) != null)
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
