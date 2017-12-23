package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;

public class MainMenuScreen implements Screen {
    final MyGame game;
    OrthographicCamera camera;
    Texture title;
    Music titleMusic;

    public MainMenuScreen(final MyGame game) {
        this.game = game;
        title = new Texture(Gdx.files.internal("title.png"));
        titleMusic = Gdx.audio.newMusic(Gdx.files.internal("title.mp3"));
        titleMusic.setLooping(true);
        titleMusic.setVolume(0.1f);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1024, 768);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.batch.draw(title, 512 - (275 + 137) / 2, 768 / 2, (275 + 137), 114 + 57);
        game.font.draw(game.batch, "Press any key", 512 - 50, 768 / 2);
        game.batch.end();

        if (Gdx.input.isTouched()) {
            game.setScreen(new GameScreen(game));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void resume() {
    }

    @Override
    public void show() {
        titleMusic.play();
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void dispose() {
        title.dispose();
        titleMusic.dispose();
    }


}
