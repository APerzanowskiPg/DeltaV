/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.orbitsgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import static com.orbitsgame.Spacecraft.spacecraftModel;

/*
Using libGdx, source: http://libgdx.com/
*/

/**
 *
 * @author adrian
 */
public class AttitudeIndicator {
    static private OrthographicCamera cam;
    static private Model attIndicatorModel;
    static private ModelBatch batch;
    
    
    static void Init()
    {
        ModelLoader loader = new ObjLoader();
        
        attIndicatorModel = loader.loadModel(Gdx.files.internal("attIndicator.obj"));
        cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        //cam.position.set(0,0,0);
        //cam.up.set(0,0,1);
        //cam.lookAt(0,1,0);
        cam.position.set(0,0,0);
        cam.up.set(1,0,0);
        cam.lookAt(0,0,1);
        cam.update();
        
        batch = new ModelBatch();
    }

    // non-static
    ModelInstance modelInstance;
    Spacecraft spacecraft;
    
    public AttitudeIndicator(Spacecraft spacecraft) {
        modelInstance = new ModelInstance(attIndicatorModel);
        this.spacecraft = spacecraft;
    }
    
    void Render()
    {
        //setTransform
        float x = 0;//Gdx.graphics.getWidth()*0.5f
        float z;//-300;//Gdx.graphics.getHeight()*0.5f
        float size = 100f;
        z = size - (Gdx.graphics.getHeight())*0.5f;
        modelInstance.transform.setToTranslation(z,0,size);//x, size, z);
        modelInstance.transform.scale(size, size, size);
        //modelInstance.transform.rotate(0, 0, 1, 90);
        
        
        //  centralize
        /*Matrix4 centMat = new Matrix4();
        Vector3 up = new Vector3(spacecraft.orientation.GetUp());
        Vector3 zplaneUp = new Vector3(up);
        zplaneUp.x = 0;
        float zrot = (float)(Math.atan2(zplaneUp.len(), up.z));
        Vector3 pivot = (new Vector3(up)).crs(Vector3.Z);
        
        float zrot2 = (float)(Math.atan2(zplaneUp.x, zplaneUp.y));
        centMat.rotate(Vector3.X, (float)Math.toDegrees(zrot2));
        centMat.rotate(Vector3.Z, (float)Math.toDegrees(zrot));*/
        
        Matrix4 centMat = new Matrix4();
        Vector3 u = new Vector3(spacecraft.orientation.GetUp());
        u = u.crs(Vector3.Y);
        u = u.nor();
        float centRotAngle = (float)Math.toDegrees(Math.acos(spacecraft.orientation.GetUp().dot(Vector3.Y)));
        centMat.setToRotation(u, centRotAngle);
        
        
        //Matrix4 rotMat = new Matrix4();
        //rotMat.setFromEulerAngles(spacecraft.eulerAngles.x, spacecraft.eulerAngles.y, spacecraft.eulerAngles.z);
        Matrix4 rotMat = new Matrix4(spacecraft.orientation.GetTransform());
        rotMat = rotMat.inv();
        //modelInstance.transform = modelInstance.transform.mul(centMat);
        modelInstance.transform = modelInstance.transform.mul(rotMat);//rotMat);
        
        //modelInstance.transform = modelInstance.transform.mul(centMat);
        modelInstance.transform.rotate(0, 1, 0, -90);
        
        //modelInstance.transform.rotate(0, 1, 0, 90);
        //Gdx.gl30.glClearColor(0, 0, 0, 1); 
        Gdx.gl30.glClear(GL20.GL_DEPTH_BUFFER_BIT );
        batch.begin(cam);
        Gdx.gl30.glDepthFunc(GL20.GL_ALWAYS);
        batch.render(modelInstance);
        
        batch.end();
    }
}
