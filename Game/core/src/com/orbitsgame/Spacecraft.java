/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.orbitsgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.orbitsgame.OrbitModel.StateVector;

/*
Using libGdx, source: http://libgdx.com/
*/

/**
 *
 * @author adrian
 */
public class Spacecraft {
    public class Orientation
    {
        //initial target is [0,0,1]
        Vector3 axisX;
        Vector3 axisY;
        Vector3 axisZ;
        
        Orientation()
        {
            axisX = new Vector3(Vector3.X);
            axisY = new Vector3(Vector3.Y);
            axisZ = new Vector3(Vector3.Z);
        }
        
        Orientation(Vector3 axX, Vector3 axY, Vector3 axZ)
        {
            axisX = axX;
            axisY = axY;
            axisZ = axZ;
        }
        
        void RotateAroundX(float degrees)
        {
            axisY = axisY.rotate(axisX, degrees);
            axisZ = axisZ.rotate(axisX, degrees);
        }
        
        void RotateAroundY(float degrees)
        {
            axisX = axisX.rotate(axisY, degrees);
            axisZ = axisZ.rotate(axisY, degrees);
        }
        
        void RotateAroundZ(float degrees)
        {
            axisX = axisX.rotate(axisZ, degrees);
            axisY = axisY.rotate(axisZ, degrees);
        }
        
        Quaternion GetQuaternion()
        {
            Quaternion ret =  new Quaternion();
            ret = ret.setFromAxes(axisX.x, axisX.y, axisX.z, axisY.x, axisY.y, axisY.z, axisZ.x, axisZ.y, axisZ.z);
            //Matrix4 mat = new Matrix4();
            //mat.setToR
            return ret;
        }
    }
    
    
    OrbitModel orbit;
    
    // time since last periapsis(indicates position on orbit)
    double timeSincePeriapsis;
    
    // orientation quaternion
    Quaternion orientation;
    //Orientation orientation;
    Vector3 eulerAngles;
    
    // spacecraft dry mass(without fuel)[tonnes]
    double dryMass;
    // mass of fuel in spacecraft[tonnes]
    double fuelMass;
    
    // engine specific impulse[s]
    double isp;
    
    // level of thrust(between 0 and 1)
    double thrustLevel;
    
    // newest state. Updated after timeSincePerapsis during propagation
    StateVector lastState;
    
    static Model spacecraftModel;
    ModelInstance modelInstance;
    
    static void Init()
    {
        ModelLoader loader = new ObjLoader();
        
        spacecraftModel = loader.loadModel(Gdx.files.internal("shezhou.obj"));
        //modelInstance = new ModelInstance(spacecraftModel);
    }
    
    Spacecraft(OrbitModel orbModel, double tSincePer, double dMass, double fMass, double spImpulse)
    {
        modelInstance = new ModelInstance(spacecraftModel);
        orbit = orbModel;
        timeSincePeriapsis = tSincePer;
        orientation = new Quaternion();
        //orientation = new Orientation();
        dryMass = dMass;
        fuelMass = fMass;
        isp = spImpulse;
        thrustLevel = 0;
        eulerAngles = new Vector3(0,0,0);        
        lastState = orbit.calcOrbitPositionAt(timeSincePeriapsis);
    }
    
    //
    void Propagate(double deltaT)
    {
        if(thrustLevel <= 0.00)
        {
        timeSincePeriapsis += deltaT;
        //timeSincePeriapsis = orbit.NormalizeTimeSincePeriapsis(timeSincePeriapsis);
        
        lastState = orbit.calcOrbitPositionAt(timeSincePeriapsis);
        }
        //threshold for potential float error
        else if(thrustLevel > 0.001)
        {
            Vector3 thrustV = new Vector3(Vector3.Z);
            Matrix4 rotMat = new Matrix4();
            rotMat.setFromEulerAngles(eulerAngles.x, eulerAngles.y, eulerAngles.z);
            thrustV = thrustV.mul(rotMat);
            
            // todo: do job on tchiolkovsky model
            thrustV = thrustV.scl((float)thrustLevel);
            lastState.velocity = lastState.velocity.add(thrustV.scl((float)deltaT));
            
            timeSincePeriapsis = orbit.SetOrbitFromStateVector(lastState, OrbitModel.TypeOfPosition.TimeSincePeriapis);
            
            timeSincePeriapsis += deltaT;
            lastState = orbit.calcOrbitPositionAt(timeSincePeriapsis);
        }
    }
    
    void Render(ModelBatch batch)
    {
        double dst = batch.getCamera().position.dst(lastState.position);
        float scaleFactor = (float)( 1f + 0.03*dst*(0.5 + (1.0/Math.PI)*Math.atan(dst-6))  );
        
        //setTransform
        modelInstance.transform.setToTranslation(lastState.position);
        modelInstance.transform.scale(scaleFactor, scaleFactor, scaleFactor);//(1f, 1f, 1f);
        //modelInstance.transform.rotate(orientation.GetQuaternion());
        //modelInstance.transform.rotate(orientation);
        Matrix4 rotMat = new Matrix4();
        rotMat.setFromEulerAngles(eulerAngles.x, eulerAngles.y, eulerAngles.z);
        modelInstance.transform = modelInstance.transform.mul(rotMat);
        
        batch.render(modelInstance);
    }
}
