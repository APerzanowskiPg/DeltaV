/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.orbitsgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
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
    static private SpriteBatch spBatch;
    static private TextureRegion progradeMarkerTex, retrogradeMarkerTex;
    static private TextureRegion normalMarkerTex, antinormalMarkerTex;
    static private TextureRegion radialMarkerTex, antiradialMarkerTex;
    
    private Marker progradeMarker, retrogradeMarker;
    private Marker normalMarker, antinormalMarker;
    private Marker radialMarker, antiradialMarker;
    
    class Marker {
        // marker size in pixels
        static final int markerSize = 32;
        // unit vector pointing the direction in Earth-Centered Inertial coordinate system(ECI)
        // Z axis is the polar axis
        Vector3 orientation;
        TextureRegion tex;
        
        Marker(Vector3 orientation, TextureRegion tex)
        {
            this.orientation = new Vector3(orientation);
            this.tex = tex;
        }
        
        // draws marker. To be set inside .begin() & .end() of sprite batch
        void draw(SpriteBatch sbatch, Matrix4 tranMat)
        {
            //calc screen space position(of center)
            Vector3 scrPos = new Vector3(orientation);
            scrPos.mul(tranMat);
            
            if(scrPos.z >= -10 )
            //sbatch.draw(tex, scrPos.x-0.5f*markerSize, scrPos.y-0.5f*markerSize, markerSize, markerSize);
                sbatch.draw(tex, scrPos.y-0.5f*markerSize, scrPos.x-0.5f*markerSize, markerSize, markerSize);
        }
    }
    
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
        
        Texture markersTex = new Texture(Gdx.files.internal("attIndMarkers.png"));
        final int tileSize=128;
        int i=0;
        progradeMarkerTex = new TextureRegion(markersTex, i*tileSize, 0, 128, 128);
        i++;
        retrogradeMarkerTex = new TextureRegion(markersTex, i*tileSize, 0, 128, 128);
        
        i++;
        normalMarkerTex = new TextureRegion(markersTex, i*tileSize, 0, 128, 128);
        i++;
        antinormalMarkerTex = new TextureRegion(markersTex, i*tileSize, 0, 128, 128);
        
        i++;
        radialMarkerTex = new TextureRegion(markersTex, i*tileSize, 0, 128, 128);
        i++;
        antiradialMarkerTex = new TextureRegion(markersTex, i*tileSize, 0, 128, 128);
        
        batch = new ModelBatch();
        spBatch = new SpriteBatch();
    }

    // non-static
    ModelInstance modelInstance;
    Spacecraft spacecraft;
    
    public AttitudeIndicator(Spacecraft spacecraft) {
        modelInstance = new ModelInstance(attIndicatorModel);
        this.spacecraft = spacecraft;
        
        //////  init markers
        progradeMarker = new Marker(new Vector3(spacecraft.lastState.velocity).nor(), progradeMarkerTex);
        retrogradeMarker = new Marker(new Vector3(spacecraft.lastState.velocity).nor().scl(-1), retrogradeMarkerTex);
        
        // calc radial and antiradial
        radialMarker = new Marker(new Vector3(spacecraft.lastState.position).scl(-1).nor(), radialMarkerTex);
        antiradialMarker = new Marker(new Vector3(spacecraft.lastState.position).nor(), radialMarkerTex);
        
        Vector3 normal = new Vector3(progradeMarker.orientation).crs(radialMarker.orientation).nor();
        normalMarker = new Marker(normal, normalMarkerTex);
        antinormalMarker = new Marker(new Vector3(normal).scl(-1), antinormalMarkerTex);
        //normalMarker;
        //antinormalMarker;
        //radialMarker;
        //antiradialMarker;
    }
    
    private void RenderMarkers()
    {
        
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
        //rotMat.mul(cam.combined);
        
        Matrix4 markersMat = new Matrix4();
        //markersMat.setToTranslation(0.5f*Gdx.graphics.getWidth(),size,0);
        markersMat.setToTranslation(size, 0.5f*Gdx.graphics.getWidth(), 0);
        markersMat.scale(size, size, size);
        markersMat.mul(rotMat);
        //markersMat.rotate(0, 1, 0, -90);
        //rotMat.translate(z,0,size);
        //rotMat.scale(size, size, size);
        //rotMat.rotate(0, 1, 0, -90);
        //rotMat.mul(cam.combined);
        
        // update markers loc
        progradeMarker.orientation.set(spacecraft.lastState.velocity).nor();
        retrogradeMarker.orientation.set(progradeMarker.orientation).scl(-1,-1,-1);
        radialMarker.orientation.set(spacecraft.lastState.position).scl(-1).nor();
        antiradialMarker.orientation.set(spacecraft.lastState.position).nor();
        
        Vector3 normal = new Vector3(progradeMarker.orientation).crs(radialMarker.orientation).nor();
        normalMarker.orientation.set(normal);
        antinormalMarker.orientation.set(normal).scl(-1);
        
        spBatch.begin();
        //spBatch.
        progradeMarker.draw(spBatch, markersMat);
        retrogradeMarker.draw(spBatch, markersMat);
        radialMarker.draw(spBatch, markersMat);
        antiradialMarker.draw(spBatch, markersMat);
        normalMarker.draw(spBatch, markersMat);
        antinormalMarker.draw(spBatch, markersMat);
        spBatch.end();
    }
}
