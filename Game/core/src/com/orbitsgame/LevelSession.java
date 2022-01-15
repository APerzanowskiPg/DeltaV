/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.orbitsgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.math.Vector2;
import com.kotcrab.vis.ui.VisUI;

/**
 *
 * @author adrian
 */
public class LevelSession implements InputProcessor {
    
    static public class LevelDesc{
        // starting orbit description
        double targetOrbit_e = 0.0002540;
        double targetOrbit_inc = 1.70213759;
        double targetOrbit_lan = 1.53462263;
        double targetOrbit_aop = 1.33423265;
        double targetOrbit_a = 6873;
        
        double startOrbit_e = 0.0004112;
        double startOrbit_inc = 0.901357839;
        double startOrbit_lan = 1.1015471;
        double startOrbit_aop = 2.36980806;
        double startOrbit_a = 6798;
        double start_timeSincePeriapsis = 0;
        
        // dry mass in spacecraft[tonnes]
        double spacecraft_dryMass = 2;
        // mass of fuel in spacecraft[tonnes]
        double spacecraft_fuelMass = 8;
        double spacecraft_startingFuelMass = 8;
        // engine specific impulse[s]
        double spacecraft_isp = 300;
        double spacecraft_maxMassFlowRatio = 2.9;
        
        LevelDesc() {}
    }
    
    public LevelDesc desc;
    
    static public PerspectiveCamera cam;
    static public Model model;
    static public Model skyboxModel;

    public ModelInstance instance;
    public ModelInstance skyboxInstance;

    public ModelInstance orbitInstance;

    static public ModelBatch modelBatch;
    public OrbitModel orbit;

    public OrbitModel targetOrbit;

    public Spacecraft spacecraft;

    public CamController camController;
    public SpacecraftController spController;
    public AttitudeIndicator attIndicator;
    public GameSessionUI gameSessionUI;

    Vector2 camOrientation;
    
    public static void Init()
    {
        GameSessionUI.Init();
        
        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(10000.0f, 10000.0f, 10000.0f);
        cam.up.set(0,0,1);
        cam.lookAt(0,0,0);
        cam.near = 0.1f;
        cam.far = 20000f;
        cam.update();

        OrbitModel.Init(cam);
        Spacecraft.Init();
        AttitudeIndicator.Init();
        
        ModelLoader loader = new ObjLoader();
        model = loader.loadModel(Gdx.files.internal("planet.obj"));
        skyboxModel = loader.loadModel(Gdx.files.internal("skybox.obj"));
    }
    
    LevelSession(LevelDesc levelDescription)
    {
        desc = levelDescription;
        
        // start level, load resources, etc.
        Gdx.input.setInputProcessor(new InputMultiplexer());
            InputMultiplexer inputMultiplexer = (InputMultiplexer) Gdx.input.getInputProcessor();
            if (!inputMultiplexer.getProcessors().contains(this, true))
                inputMultiplexer.addProcessor(this);
            
        // UI
        gameSessionUI = new GameSessionUI();

        modelBatch = new ModelBatch();

        camOrientation = new Vector2(0,0);

        

        orbit = new OrbitModel();
        orbit = new OrbitModel();
        orbit.color = new float[]{1.0f, 1.0f, 1.0f};
        orbit.e = desc.startOrbit_e;
        orbit.inc = desc.startOrbit_inc;
        orbit.lan = desc.startOrbit_lan;
        orbit.aop = desc.startOrbit_aop;
        orbit.a = desc.startOrbit_a;
        orbit.dirtyModel = true;
        orbit.UpdateModel();
        
        targetOrbit = new OrbitModel();
        targetOrbit.color = new float[]{1.0f, 0.0f, 1.0f};
        targetOrbit.e = desc.targetOrbit_e;
        targetOrbit.inc = desc.targetOrbit_inc;
        targetOrbit.lan = desc.targetOrbit_lan;
        targetOrbit.aop = desc.targetOrbit_aop;
        targetOrbit.a = desc.targetOrbit_a;
        targetOrbit.dirtyModel = true;
        targetOrbit.UpdateModel();

        
        instance = new ModelInstance(model);
        instance.transform.setToScaling(6371.0f, 6371.0f, 6371.0f);

        skyboxInstance = new ModelInstance(skyboxModel);

        spacecraft = new Spacecraft(orbit, desc.start_timeSincePeriapsis,
                desc.spacecraft_dryMass, desc.spacecraft_fuelMass, desc.spacecraft_isp, gameSessionUI);
        
        attIndicator = new AttitudeIndicator(spacecraft);

        camController = new CamController();
        spController = new SpacecraftController(spacecraft, gameSessionUI);
    }
    
    public void render () {
        double physicsDelta = gameSessionUI.GetTimeMultiplier()*Gdx.graphics.getDeltaTime();
        spacecraft.Propagate(physicsDelta);
        float dst = cam.position.dst(spacecraft.lastState.position);
        cam.near = 0.5f*dst;
        cam.far = 10000f*dst;

        camController.TransformCam(cam, spacecraft.lastState.position);

        Gdx.gl30.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl30.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl30.glClearColor(0, 0, 0, 1); 
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));
        //render skybox
        modelBatch.begin(cam);

        //skyboxInstance.transform.setToScaling(8000.0f, 8000.0f, 8000.0f);
        //skyboxInstance.transform.translate(cam.position);
        skyboxInstance.transform.setToTranslation(cam.position);
        skyboxInstance.transform.scale(0.99f*cam.far, 0.99f*cam.far, 0.99f*cam.far);
        Gdx.gl30.glCullFace(GL20.GL_CW);
        Gdx.gl30.glDisable(GL20.GL_DEPTH_TEST);
        modelBatch.render(skyboxInstance);
        Gdx.gl30.glEnable(GL20.GL_DEPTH_TEST);

        //clear depth after rendering skybox
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glFlush();
        Gdx.gl30.glCullFace(GL20.GL_CCW);

        Gdx.gl30.glDepthFunc(GL20.GL_LESS);

        //Gdx.gl30.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );

        //modelBatch.begin(cam);
        modelBatch.render(instance);
        spacecraft.Render(modelBatch);
        //modelBatch.render(spacecraftInstance);

        //modelBatch.render(orbit.instance);
        modelBatch.end();
        Gdx.gl30.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl30.glDepthFunc(GL20.GL_LESS);

        orbit.Render(cam);
        targetOrbit.Render(cam);
        attIndicator.Render();

        spController.Update(Gdx.graphics.getDeltaTime());

        //ui
        gameSessionUI.Render();
    }
    
    public void dispose () {
        model.dispose();
        modelBatch.dispose();

        //ui
        gameSessionUI.Dispose();
        //VisUI.dispose();
    }
    
    
    // input processing
        @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if(button == Input.Buttons.RIGHT)
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
