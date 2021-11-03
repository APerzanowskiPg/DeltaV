/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.orbitsgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;


/*
Using libGdx, source: http://libgdx.com/
*/

/**
 *
 * @author adrian
 */
public class CamController {
    private Vector2 orientation;
    private float dst;
    private boolean dragging;
    
    private float lastX;
    private float lastY;
    float xVelocity;
    float yVelocity;
    
    float scrollLevel;
    float scrollVelocity;
    float baseDst;
    
    CamController()
    {
        orientation = new Vector2(0,0);
        dragging = false;
        scrollLevel = 0f;
        baseDst = 5f;
        dst = 5f;
        
        xVelocity = 0 - 180f;
        yVelocity = 0 - 90f;
        
        scrollVelocity = 0.1f;
    }
    
    void TransformCam(Camera cam, Vector3 objOfInterest)
    {
        dst = (float)(baseDst*Math.exp(scrollLevel));
        
        Vector3 camPos = new Vector3(dst, 0, 0);
        //Quaternion quatRot = new Quaternion();
        //quatRot = quatRot.setEulerAngles(0, 0, orientation.y);
        
        //camPos = quatRot.transform(camPos);
        
        camPos = camPos.rotate(Vector3.Y, orientation.y);
        camPos = camPos.rotate(Vector3.Z, (float)(orientation.x));//*Math.cos(Math.toDegrees(orientation.y))));
        
        cam.position.set(camPos).add(objOfInterest);
        cam.up.set(0,0,1);
        cam.update();
        cam.lookAt(objOfInterest);
        cam.update();
    }
    
    void OnRMBPush(int screenX, int screenY)
    {
        dragging = true;
        lastX = screenX;
        lastY = screenY;
    }
    
    void OnRMBRelease()
    {
        dragging = false;
    }
    
    void OnMove(int screenX, int screenY)
    {
        if(dragging)
        {
            orientation.x += (screenX-lastX)/Gdx.graphics.getBackBufferWidth()*xVelocity;
            orientation.y += (screenY-lastY)/Gdx.graphics.getBackBufferHeight()*yVelocity;
            
            if(orientation.y >= 89)
            {
                orientation.y = 89;
            }
            else if(orientation.y <= -89)
            {
                orientation.y = -89;
            }
        }
        lastX = screenX;
        lastY = screenY;
    }
    
    void OnScroll(float y)
    {
        scrollLevel += y*scrollVelocity;
    }
    
}
