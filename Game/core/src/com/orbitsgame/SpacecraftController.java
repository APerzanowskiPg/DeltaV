/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.orbitsgame;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

/*
Using libGdx, source: http://libgdx.com/
*/

/**
 *
 * @author adrian
 */
public class SpacecraftController {
    
    private Spacecraft spacecraft;
    
    boolean pressedW;
    boolean pressedA;
    boolean pressedS;
    boolean pressedD;
    boolean pressedQ;
    boolean pressedE;
    boolean pressedShift;
    boolean pressedCtrl;
    
    float thrustVelocity;
    float rotVelocity = 45f;
    
    SpacecraftController(Spacecraft spacecr)
    {
        spacecraft = spacecr;
        thrustVelocity = 0.5f;
    }
    
    boolean IsKeyOfInterest(int key)
    {
        boolean ret = (key == Input.Keys.W);
        ret = ret | (key == Input.Keys.A);
        ret = ret | (key == Input.Keys.S);
        ret = ret | (key == Input.Keys.D);
        ret = ret | (key == Input.Keys.Q);
        ret = ret | (key == Input.Keys.E);
        ret = ret | (key == Input.Keys.SHIFT_LEFT);
        ret = ret | (key == Input.Keys.CONTROL_LEFT);
        
        return ret;
    }
    
    void KeyDown(int key)
    {
        switch (key) {
            case Input.Keys.W:
                pressedW = true;
                break;
            case Input.Keys.A:
                pressedA = true;
                break;
            case Input.Keys.S:
                pressedS = true;
                break;
            case Input.Keys.D:
                pressedD = true;
                break;
            case Input.Keys.Q:
                pressedQ = true;
                break;
            case Input.Keys.E:
                pressedE = true;
                break;
            case Input.Keys.SHIFT_LEFT:
                pressedShift = true;
                break;
            case Input.Keys.CONTROL_LEFT:
                pressedCtrl = true;
                break;
            default:
                break;
        }
    }
    
    void KeyUp(int key)
    {
        switch (key) {
            case Input.Keys.W:
                pressedW = false;
                break;
            case Input.Keys.A:
                pressedA = false;
                break;
            case Input.Keys.S:
                pressedS = false;
                break;
            case Input.Keys.D:
                pressedD = false;
                break;
            case Input.Keys.Q:
                pressedQ = false;
                break;
            case Input.Keys.E:
                pressedE = false;
                break;
            case Input.Keys.SHIFT_LEFT:
                pressedShift = false;
                break;
            case Input.Keys.CONTROL_LEFT:
                pressedCtrl = false;
                break;
            default:
                break;
        }
    }
    
    void Update(float dt)
    {
        
        if(pressedW)
        {
            //spacecraft.orientation.RotateAroundY(10*dt);
            spacecraft.eulerAngles.y += rotVelocity*dt;
            spacecraft.orientation.RotateAroundPitch(rotVelocity*dt);
        }
        if(pressedS)
        {
            spacecraft.eulerAngles.y -= rotVelocity*dt;
            spacecraft.orientation.RotateAroundPitch(-rotVelocity*dt);
        }
        
        if(pressedA)
        {
            spacecraft.eulerAngles.x += rotVelocity*dt;
            spacecraft.orientation.RotateAroundYaw(rotVelocity*dt);
        }
        if(pressedD)
        {
            spacecraft.eulerAngles.x -= rotVelocity*dt;
            spacecraft.orientation.RotateAroundYaw(-rotVelocity*dt);
        }
        
        if(pressedQ)
        {
            spacecraft.eulerAngles.z += rotVelocity*dt;
            spacecraft.orientation.RotateAroundRoll(rotVelocity*dt);
        }
        if(pressedE)
        {
            spacecraft.eulerAngles.z -= rotVelocity*dt;
            spacecraft.orientation.RotateAroundRoll(-rotVelocity*dt);
        }
        if(pressedShift)
        {
            spacecraft.thrustLevel += dt*thrustVelocity;
            if(spacecraft.thrustLevel > 1.0f)
            {
                spacecraft.thrustLevel = 1.0f;
            }
        }
        if(pressedCtrl)
        {
            spacecraft.thrustLevel -= dt*thrustVelocity;
            if(spacecraft.thrustLevel < 0.0f)
            {
                spacecraft.thrustLevel = 0.0f;
            }
        }
    }
    
    /*void Update(float dt)
    {
        Vector3 currentAxisZ = spacecraft.orientation.transform(new Vector3(Vector3.Z));
        Vector3 currentAxisY = spacecraft.orientation.transform(new Vector3(Vector3.Y));
        Vector3 currentAxisX = spacecraft.orientation.transform(new Vector3(Vector3.X));
        
        
        if(pressedW)
        {
            //spacecraft.orientation.RotateAroundY(10*dt);
            Quaternion rot = new Quaternion(currentAxisY, 10*dt);
            spacecraft.orientation = rot.mul(spacecraft.orientation.mul(rot.conjugate()));
        }
        if(pressedS)
        {
            //spacecraft.orientation.RotateAroundY(-10*dt);
            Quaternion rot = new Quaternion(currentAxisY, -10*dt);
            spacecraft.orientation = rot.mul(spacecraft.orientation.mul(rot.conjugate()));
        }
        
        if(pressedA)
        {
            //spacecraft.orientation.RotateAroundX(10*dt);
            Quaternion rot = new Quaternion(currentAxisX, 10*dt);
            spacecraft.orientation = rot.mul(spacecraft.orientation.mul(rot.conjugate()));
        }
        if(pressedD)
        {
            //spacecraft.orientation.RotateAroundX(-10*dt);
            Quaternion rot = new Quaternion(currentAxisX, -10*dt);
            spacecraft.orientation = rot.mul(spacecraft.orientation.mul(rot.conjugate()));
        }
        
        if(pressedQ)
        {
            //spacecraft.orientation.RotateAroundZ(10*dt);
            Quaternion rot = new Quaternion(currentAxisZ, 10*dt);
            spacecraft.orientation = rot.mul(spacecraft.orientation.mul(rot.conjugate()));
        }
        if(pressedE)
        {
            //spacecraft.orientation.RotateAroundZ(-10*dt);
            Quaternion rot = new Quaternion(currentAxisZ, -10*dt);
            spacecraft.orientation = rot.mul(spacecraft.orientation.mul(rot.conjugate()));
        }
    }*/
}
