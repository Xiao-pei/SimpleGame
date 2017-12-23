package com.mygdx.game;

import java.util.Iterator;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;


public class GameScreen implements Screen {
    final MyGame game;
    final float pixelsPerTile = 16;
    float lastDropTime;
    Stage stage;
    TiledMap map;
    Music bgm;
    OrthogonalTiledMapRenderer renderer;
    OrthographicCamera camera;
    Knight knight;
    Rectangle knightRectangle;
    Rectangle victory;
    Rectangle[] badGuysRectangle;
    BadGuy[] badGuys;

    Array<Sword> swords;


    GameScreen(final MyGame game) {
        this.game = game;

        map = new TmxMapLoader().load("map.tmx");
        bgm = Gdx.audio.newMusic(Gdx.files.internal("8-bit Internets Levels.mp3"));
        bgm.setVolume(0.1f);
        bgm.setLooping(true);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1024 / 2, 768 / 2);

        stage = new Stage();
        stage.getViewport().setCamera(camera);


        knight = new Knight();
        knight.wall = (TiledMapTileLayer) map.getLayers().get("wall");
        knight.death = (TiledMapTileLayer) map.getLayers().get("death");
        knight.staticwall = (TiledMapTileLayer) map.getLayers().get("staticwall");
        knightRectangle = knight.getRectangle();
        knight.setPosition(4, 4);

        badGuys = new BadGuy[5];
        badGuysRectangle = new Rectangle[5];
        for (int i = 0; i < badGuys.length; ++i) {
            badGuys[i] = new BadGuy();
            badGuys[i].wall = (TiledMapTileLayer) map.getLayers().get("wall");
            badGuys[i].death = (TiledMapTileLayer) map.getLayers().get("death");
            badGuys[i].staticwall = (TiledMapTileLayer) map.getLayers().get("staticwall");
            badGuys[i].badwall = (TiledMapTileLayer) map.getLayers().get("badguywall");
            badGuysRectangle[i] = badGuys[i].getRectangle();
        }
        spawnBadguy();
        swords = new Array<Sword>();
        spawnSwords();
        stage.addActor(knight);

        victory=new Rectangle();
        victory.set(223,4,knight.getWidth(),knight.getHeight());

    }

    private void spawnSwords() {
        Sword sword=new Sword();
        sword.staticwall=(TiledMapTileLayer) map.getLayers().get("staticwall");
        sword.setPosition(MathUtils.random(189, 210),20);
        swords.add(sword);
        lastDropTime = TimeUtils.nanoTime();
    }

    private void spawnBadguy() {
        for (BadGuy badGuy : badGuys) {
            badGuy.setPosition(MathUtils.random(18, 200), 8);
            stage.addActor(badGuy);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.4f, 0.6f, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.position.x = knight.getX();
        camera.update();

        for (int i = 0; i < badGuys.length; ++i) {
            badGuysRectangle[i].setPosition(badGuys[i].getX(), badGuys[i].getY());
            if (knightRectangle.overlaps(badGuysRectangle[i]))
                knight.isdead = true;
        }

        if (TimeUtils.nanoTime() - lastDropTime > 1000000000)
            spawnSwords();
        Iterator<Sword> iter = swords.iterator();
        while (iter.hasNext()) {
            Sword tmp=iter.next();
            stage.addActor(tmp);
            if (knightRectangle.overlaps(tmp.getRectangle())){
                knight.isdead = true;
            }
            if(tmp.getY()<5)
                iter.remove();
        }


        renderer.setView(camera);
        renderer.render();
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, 20 * width / height, 20);
    }

    @Override
    public void show() {
        bgm.play();
        renderer = new OrthogonalTiledMapRenderer(map, 1 / pixelsPerTile);
    }

    @Override
    public void dispose() {
        stage.dispose();
        bgm.dispose();
    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

}
