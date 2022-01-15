package com.orbitsgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.loaders.ModelLoader;

import com.badlogic.gdx.graphics.GL20;
import static com.badlogic.gdx.graphics.GL20.GL_BLEND;
import com.badlogic.gdx.graphics.PerspectiveCamera;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;

import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;

import com.badlogic.gdx.math.Vector2;
import com.kotcrab.vis.ui.VisUI;

/*
Using libGdx, source: http://libgdx.com/
*/


public class DeltaVGame implements ApplicationListener {

    public LevelSession level;

    @Override
    public void create () {
        //static res load
        VisUI.load();
        LevelSession.Init();
        
        LevelSession.LevelDesc desc = new LevelSession.LevelDesc(); 
        level = new LevelSession(desc);

    }

    @Override
    public void render () {
        level.render();
    }

    @Override
    public void dispose () {
        VisUI.dispose();
    }

    @Override
    public void resume () {
    }

    @Override
    public void resize (int width, int height) {
    }

    @Override
    public void pause () {
    }
        
}

