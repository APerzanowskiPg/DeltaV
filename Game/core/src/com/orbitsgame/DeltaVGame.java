package com.orbitsgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Input.Buttons;
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

/*
Using libGdx, source: http://libgdx.com/
*/


public class DeltaVGame implements ApplicationListener, InputProcessor {

        public PerspectiveCamera cam;
	public Model model;
        //public Model spacecraftModel;
        //public ModelInstance spacecraftInstance;
        
	public ModelInstance instance;

	public Model orbitModel;
	public ModelInstance orbitInstance;

        public ModelBatch modelBatch;
        public OrbitModel orbit;
        
        public Spacecraft spacecraft;
        
        public CamController camController;
        public SpacecraftController spController;
        
        Vector2 camOrientation;

	@Override
	public void create () {
            modelBatch = new ModelBatch();

            camOrientation = new Vector2(0,0);

            cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            cam.position.set(10000.0f, 10000.0f, 10000.0f);
            cam.up.set(0,0,1);
            cam.lookAt(0,0,0);
            cam.near = 0.1f;
            cam.far = 20000f;
            cam.update();

            OrbitModel.Init(cam);
            Spacecraft.Init();
            orbit = new OrbitModel();

            ModelLoader loader = new ObjLoader();
            model = loader.loadModel(Gdx.files.internal("planet.obj"));


            instance = new ModelInstance(model);
            instance.transform.setToScaling(6371.0f, 6371.0f, 6371.0f);

            spacecraft = new Spacecraft(orbit, 0, 10, 100, 300);

            camController = new CamController();
            spController = new SpacecraftController(spacecraft);
            Gdx.input.setInputProcessor(this);
                
	}

	@Override
	public void render () {
            spacecraft.Propagate(Gdx.graphics.getDeltaTime());
            float dst = cam.position.dst(spacecraft.lastState.position);
            cam.near = 0.5f*dst;
            cam.far = 10000f*dst;
            
            camController.TransformCam(cam, spacecraft.lastState.position);
            
            Gdx.gl30.glEnable(GL20.GL_DEPTH_TEST);

            Gdx.gl30.glDepthFunc(GL20.GL_LESS);
            Gdx.gl30.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            Gdx.gl30.glClearColor(0, 0, 0, 1); 
            Gdx.gl30.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );

            modelBatch.begin(cam);
            modelBatch.render(instance);
            spacecraft.Render(modelBatch);
            //modelBatch.render(spacecraftInstance);

            //modelBatch.render(orbit.instance);
            modelBatch.end();
            Gdx.gl30.glEnable(GL20.GL_DEPTH_TEST);
            Gdx.gl30.glDepthFunc(GL20.GL_LESS);
            
            orbit.Render(cam);
            
            spController.Update(Gdx.graphics.getDeltaTime());
	}
	
	@Override
	public void dispose () {
            model.dispose();
            modelBatch.dispose();
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
        
        
        
        
        
        // input processing
        @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if(button == Buttons.RIGHT)
        {
            camController.OnRMBPush(screenX, screenY);
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        camController.OnRMBRelease();
        return false;
    }


    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        //camController.OnMove(screenX, screenY);
        return false;
    }
    
    @Override
    public boolean scrolled(float x, float y) {
        camController.OnScroll(y);
        return false;//return false;
    }

    @Override
    public boolean keyDown(int i) {
        if(spController.IsKeyOfInterest(i))
        {
            spController.KeyDown(i);
            return true;
        }
        return false;//throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean keyUp(int i) {
        if(spController.IsKeyOfInterest(i))
        {
            spController.KeyUp(i);
            return true;
        }
        return false;//throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean keyTyped(char c) {
        return false;//throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        camController.OnMove(screenX, screenY);
        return false;//throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

