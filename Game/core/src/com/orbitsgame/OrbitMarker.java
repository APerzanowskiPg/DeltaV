/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.orbitsgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import static com.badlogic.gdx.graphics.GL20.GL_ARRAY_BUFFER;
import static com.badlogic.gdx.graphics.GL20.GL_FLOAT;
import static com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA;
import static com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA;
import static com.badlogic.gdx.graphics.GL20.GL_STATIC_DRAW;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 *
 * @author adrian
 */
public class OrbitMarker {
    // resources for rendering
    private static IntBuffer VAOi;
    private static int VBO;
    private static Texture PA_marker_tex;
    private static Texture AP_marker_tex;
    private static Texture arrival_marker_tex;
    private static Texture departure_marker_tex;
    private static Texture maneuver_marker_tex;
    private static Texture hover_marker_tex;
    private static ShaderProgram shader;
    
    public enum MarkerType
    {
        PA,
        AP,
        arrival,
        departure,
        maneuver,
        hover
    };
    
    static void Init()
    {
        // create 3d model of rect
        VertexAttributes attrs = new VertexAttributes(VertexAttribute.Position());
        
        int err;
            
        int[] VAO = new int[1];
        VAO[0] = 0;
        //VAOi = IntBuffer.wrap(VAO);
        VAOi = createIntBuffer(VAO);
        err = Gdx.gl30.glGetError();
        Gdx.gl30.glGenVertexArrays(1, VAOi);
        err = Gdx.gl30.glGetError();
        Gdx.gl30.glBindVertexArray(VAOi.get(0));
        err = Gdx.gl30.glGetError();
        
        // float size(4) * number of components(3) * number of points (6)
        FloatBuffer vbo_data = ByteBuffer.allocateDirect(4*3*6).order(ByteOrder.nativeOrder()).asFloatBuffer();
        float[] rectCoords = { 0,0,0,  1,0,0,  0,1,0,
                               1,0,0,  1,1,0,  0,1,0};
        vbo_data.put(rectCoords).position(0);

        VBO = Gdx.gl30.glGenBuffer();
        err = Gdx.gl30.glGetError();
        Gdx.gl30.glBindBuffer(GL_ARRAY_BUFFER, VBO);
        err = Gdx.gl30.glGetError();
        Gdx.gl30.glBufferData(GL_ARRAY_BUFFER, 6*3*4, vbo_data, GL_STATIC_DRAW);
        err = Gdx.gl30.glGetError();
        
        
        Gdx.gl30.glVertexAttribPointer(0, 3, GL_FLOAT, false, 3*4 , 0);
        err = Gdx.gl30.glGetError();
        Gdx.gl30.glEnableVertexAttribArray(0);
        err = Gdx.gl30.glGetError();
        
        // load shaders
        String vertexShader = Gdx.files.internal("marker_vertex.vert").readString();
        String fragmentShader = Gdx.files.internal("marker_fragment.frag").readString();
        shader = new ShaderProgram(vertexShader, fragmentShader);
        
        // create textures
        PA_marker_tex = new Texture(Gdx.files.internal("PA_marker.png"));
        AP_marker_tex = new Texture(Gdx.files.internal("AP_marker.png"));
        arrival_marker_tex = new Texture(Gdx.files.internal("arrival_marker.png"));
        departure_marker_tex = new Texture(Gdx.files.internal("departure_marker.png"));
        maneuver_marker_tex = new Texture(Gdx.files.internal("maneuver_marker.png"));
        hover_marker_tex = new Texture(Gdx.files.internal("hover_marker.png"));
        
    }
    
  
    private static IntBuffer createIntBuffer(int[] vals){
        IntBuffer buffer=ByteBuffer.allocateDirect(vals.length * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
        buffer.put(vals).position(0);
        return buffer;
    }
    
    
    
    /// NON-STATIC PART
    private MarkerType type;
    private Vector3 pos;
    private double timeSincePA;
    boolean active;
    
    OrbitMarker(MarkerType type)
    {
        this.type = type;
        active = false;
    }
    
    void Disable()
    {
        active = false;
    }
    
    void SetMarker(Vector3 pos, double timeSincePA)
    {
        this.pos = pos;
        this.timeSincePA = timeSincePA;
        
        active = true;
    }
    
    // scale is two component vector of scale
    // color is three component vector of RGB color
    void Render(Camera cam, float[] scale, float[] color)
    {
        if(active)
        {
            int err = 0;

            TypeToTexture(type).bind();

            Matrix4 transform = new Matrix4(cam.combined);
            transform.mul(new Matrix4().setToTranslation(pos));

            shader.bind();
            err = Gdx.gl30.glGetError();
            shader.setUniformMatrix4fv("u_projTrans", transform.getValues(), 0, 16);
            shader.setUniform2fv("u_scale", scale, 0, 2);
            shader.setUniform3fv("u_color", color, 0, 3);
            err = Gdx.gl30.glGetError();

            Gdx.gl30.glBindBuffer(GL_ARRAY_BUFFER, VBO);
            err = Gdx.gl30.glGetError();
            Gdx.gl30.glBindVertexArray(VAOi.get(0));
            err = Gdx.gl30.glGetError();
            Gdx.gl30.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            err = Gdx.gl30.glGetError();
            Gdx.gl30.glDrawArrays( GL20.GL_TRIANGLES, 0, 6);
            err = Gdx.gl30.glGetError();
        }
    }
    
    private Texture TypeToTexture(MarkerType type)
    {
        if(null != type)
            switch (type) {
                case PA:
                    return PA_marker_tex;
                case AP:
                    return AP_marker_tex;
                case arrival:
                    return arrival_marker_tex;
                case departure:
                    return departure_marker_tex;
                case maneuver:
                    return maneuver_marker_tex;
                case hover:
                    return hover_marker_tex;
                default:
                    return null;
            }
        else
        {
            return null;
        }
    }
}
