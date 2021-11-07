/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.orbitsgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import static com.badlogic.gdx.graphics.GL20.GL_BLEND;
import static com.badlogic.gdx.graphics.GL30.*;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.decals.DecalMaterial;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/*
Using libGdx, source: http://libgdx.com/
*/

/**
 *
 * @author adrian
 */
public class OrbitModel { 
    final short numOfVertices = 500;
    
    
    public class StateVector
    {
        //position in [km]
        Vector3 position;
        //linear velocity in [km/s]
        Vector3 velocity;
        StateVector()
        {
            position = new Vector3();
            velocity = new Vector3();
        }
    }
    boolean dirtyModel;
    
    float[] vertices;
    short[] indices;
    
    // RGB color
    float[] color;
    
    Mesh orbitMesh;
    Model orbitModel;
    ModelInstance instance;
    IntBuffer VAOi;
    int VBO;
    
    int err = 0;

    static ShaderProgram shader;
    
    
    // hardoced Earth mi(G*Mearth) in [km^3/s^2]
    double mi_E = 3.986e5;
    // hardcoded Sphere of Influence of Earth
    double SOI = 929000;
    
    //orbit params
    // a - semi-major axis in [km]
    double a = 6798;
    // e - eccentricity
    double e = 	0.0004112;
    
    // lan - longitude of ascending node[rad]
    double lan = 1.1015471;
    // inc - inclination[rad]
    double inc = 0.901357839;
    
    //argument of periapsis[rad]
    double aop = 2.36980806;
    
    // markers
    OrbitMarker APmarker;
    OrbitMarker PAmarker;
    OrbitMarker arrivalMarker;
    OrbitMarker departureMarker;
    OrbitMarker hoverMarker;
    OrbitMarker maneuverMarker;
    
    
    // Solves Kepler equation using Newton's method. The equation: E - e*sin(E) - M = 0, given e,M
    // where E - eccentric anomaly[rad](calculated)
    // e - orbit eccentricity
    // M - mean anomaly[rad]
    // threshold - maximal satisfying error
    // maxIterations - maximal number of iterations of Newton's method if threshold has not been satisfied
    double SolveKeplerEquation(double e, double M, double threshold, short maxIterations)
    {
        double E0 = M;
        double E1 = 0;
        
        for(short i=0; i<maxIterations; ++i)
        {
            E1 = E0 - (E0 - e*Math.sin(E0) - M)/(1-e*Math.cos(E0));
            if(Math.abs(E1-E0) <= threshold)
            {
                break;
            }
            E0 = E1;
        }
        return E1;
    }
    
    // Does the same as SolveKeplerEquation, but for hyperbolic orbit(when a e>1, a<0)
    // M = e*sinh(H) - H
    double SolveKeplerEquationHyperbolic(double e, double M, double threshold, short maxIterations)
    {
        double H0 = M;
        double H1 = 0;
        
        for(short i=0; i<maxIterations; ++i)
        {
            H1 = H0 - (e*Math.sinh(H0) - H0 - M)/(e*Math.cosh(H0) - 1);
            if(Math.abs(H1-H0) <= threshold)
            {
                break;
            }
            H0 = H1;
        }
        return H1;
    }
    
    float[] calcOrbitPoints()
    {
        if(e>1)
        {
            return calcOrbitPoints_hyperbolic();
        }
        
        float[] ret = new float[numOfVertices*3];
        
        //numOfVertices points
        for(short i=0; i<numOfVertices; ++i)
        {
            double E = 2*Math.PI*(i/((double)numOfVertices));
            
            // v - true anomaly
            double atanX = Math.sqrt(1+e)*Math.sin(0.5*E);
            double atanY = Math.sqrt(1-e)*Math.cos(0.5*E);
            double v = 2* Math.atan2(atanX, atanY);

            // rc - distance to the central body[km]
            double rc = a*(1-e*Math.cos(E));
            
            //double tmp = Math.sqrt(a*mi_E)/rc;
            
            double o_x = rc*Math.cos(v);//0 - tmp*Math.sin(E);
            double o_y = rc*Math.sin(v);//tmp * Math.sqrt(1-e*e)*Math.cos(E);
            
            float r_x = (float) ( o_x*(Math.cos(lan)*Math.cos(aop) - Math.sin(aop)*Math.cos(inc)*Math.sin(lan))
                    - o_y*(Math.sin(aop)*Math.cos(lan) + Math.cos(aop)*Math.cos(inc)*Math.sin(lan)));
            
            float r_y = (float) ( o_x*(Math.cos(aop)*Math.sin(lan) + Math.sin(aop)*Math.cos(inc)*Math.cos(lan))
                    + o_y*(Math.cos(aop)*Math.cos(inc)*Math.cos(lan) - Math.sin(aop)*Math.sin(lan)));
            
            float r_z = (float) ( o_x*Math.sin(aop)*Math.sin(inc) + o_y*Math.cos(aop)*Math.sin(inc) );
            
            ret[3*i + 0] = r_x;
            ret[3*i + 1] = r_y;
            ret[3*i + 2] = r_z;
        }
        
        return ret;
    }
    
    float[] calcOrbitPoints_hyperbolic()
    {
        float[] ret = new float[numOfVertices*3];
        
        // aba - angle between asymptotes [rad]
        double aba = 2*Math.acos(0-1/e);
        
        double H1 = acosh(Math.abs((a-SOI)/(a*e)));
        double H2 = -H1;
        double range = 2*H1;
        
        //numOfVertices points
        for(short i=0; i<numOfVertices; ++i)
        {
            double H = range*(i/((double)numOfVertices)) - H1;
            //double H = aba*(i/((double)numOfVertices)) - 0.5*aba;
        
            // v - true anomaly
            double v = 2* Math.atan(Math.sqrt((e+1)/(e-1))*Math.tanh(0.5*H));
        
            // rc - distance to the central body[km]
            double rc = a*(1-e*Math.cosh(H));
            
            //double tmp = Math.sqrt(a*mi_E)/rc;
            
            double o_x = rc*Math.cos(v);//0 - tmp*Math.sin(E);
            double o_y = rc*Math.sin(v);//tmp * Math.sqrt(1-e*e)*Math.cos(E);
            
            float r_x = (float) ( o_x*(Math.cos(lan)*Math.cos(aop) - Math.sin(aop)*Math.cos(inc)*Math.sin(lan))
                    - o_y*(Math.sin(aop)*Math.cos(lan) + Math.cos(aop)*Math.cos(inc)*Math.sin(lan)));
            
            float r_y = (float) ( o_x*(Math.cos(aop)*Math.sin(lan) + Math.sin(aop)*Math.cos(inc)*Math.cos(lan))
                    + o_y*(Math.cos(aop)*Math.cos(inc)*Math.cos(lan) - Math.sin(aop)*Math.sin(lan)));
            
            float r_z = (float) ( o_x*Math.sin(aop)*Math.sin(inc) + o_y*Math.cos(aop)*Math.sin(inc) );
            
            ret[3*i + 0] = r_x;
            ret[3*i + 1] = r_y;
            ret[3*i + 2] = r_z;
        }
        
        return ret;
    }
    
    
    //@t_s time in seconds(since periapis)
    StateVector calcOrbitPositionAt(double t_s)
    {
        if(e>1)
        {
            return calcOrbitPositionAt_Hyperbolic(t_s);
        }
        // n - Mean angular motion in [rad]
        double n = Math.sqrt(mi_E/(a*a*a));
        
        //M - mean anomaly at time t_s
        double M = (t_s * n);
        
        //normalize the M
        M = (M % (2*Math.PI));
        if(M < 0)
        {
            M = (2*Math.PI + M);
        }
        
        // Solve Kepler equation
        double E = SolveKeplerEquation(e, M, 0.01, (short)20);
        
        // v - true anomaly
        double atanX = Math.sqrt(1+e)*Math.sin(0.5*E);
        double atanY = Math.sqrt(1-e)*Math.cos(0.5*E);
        double v = 2* Math.atan2(atanX, atanY);
        
        // rc - distance to the central body[km]
        double rc = a*(1-e*Math.cos(E));
            
        
        
        // calc Position 
        double o_x = rc*Math.cos(v);//0 - tmp*Math.sin(E);
        double o_y = rc*Math.sin(v);//tmp * Math.sqrt(1-e*e)*Math.cos(E);
        
        StateVector ret = new StateVector();
        
        ret.position.x = (float) ( o_x*(Math.cos(lan)*Math.cos(aop) - Math.sin(aop)*Math.cos(inc)*Math.sin(lan))
                - o_y*(Math.sin(aop)*Math.cos(lan) + Math.cos(aop)*Math.cos(inc)*Math.sin(lan)));

        ret.position.y = (float) ( o_x*(Math.cos(aop)*Math.sin(lan) + Math.sin(aop)*Math.cos(inc)*Math.cos(lan))
                + o_y*(Math.cos(aop)*Math.cos(inc)*Math.cos(lan) - Math.sin(aop)*Math.sin(lan)));

        ret.position.z = (float) ( o_x*Math.sin(aop)*Math.sin(inc) + o_y*Math.cos(aop)*Math.sin(inc) );
        
        // calc Velocity
        double tmp = Math.sqrt(a*mi_E)/rc;
        double ov_x = 0 - tmp*Math.sin(E);
        double ov_y = tmp * Math.sqrt(1-e*e)*Math.cos(E);
        
        ret.velocity.x = (float) ( ov_x*(Math.cos(lan)*Math.cos(aop) - Math.sin(aop)*Math.cos(inc)*Math.sin(lan))
                - ov_y*(Math.sin(aop)*Math.cos(lan) + Math.cos(aop)*Math.cos(inc)*Math.sin(lan)));

        ret.velocity.y = (float) ( ov_x*(Math.cos(aop)*Math.sin(lan) + Math.sin(aop)*Math.cos(inc)*Math.cos(lan))
                + ov_y*(Math.cos(aop)*Math.cos(inc)*Math.cos(lan) - Math.sin(aop)*Math.sin(lan)));

        ret.velocity.z = (float) ( ov_x*Math.sin(aop)*Math.sin(inc) + ov_y*Math.cos(aop)*Math.sin(inc) );
        
        return ret;
    }
    
    private StateVector calcOrbitPositionAt_Hyperbolic(double t_s)
    {
        // n - Mean angular motion in [rad]
        double n = Math.sqrt(mi_E/Math.abs(a*a*a));
        
        //M - mean anomaly at time t_s
        double M = (t_s * n);
        
        //normalize the M
        /*M = (M % (2*Math.PI));
        if(M < 0)
        {
            M = (2*Math.PI + M);
        }*/
        
        // Solve Kepler equation
        double H = SolveKeplerEquationHyperbolic(e, M, 0.01, (short)20);
        
        // v - true anomaly
        double v = 2* Math.atan(Math.sqrt((e+1)/(e-1))*Math.tanh(0.5*H));
        
        // rc - distance to the central body[km]
        double rc = a*(1-e*Math.cosh(H));
            
        
        
        // calc Position 
        double o_x = rc*Math.cos(v);//0 - tmp*Math.sin(E);
        double o_y = rc*Math.sin(v);//tmp * Math.sqrt(1-e*e)*Math.cos(E);
        
        StateVector ret = new StateVector();
        
        ret.position.x = (float) ( o_x*(Math.cos(lan)*Math.cos(aop) - Math.sin(aop)*Math.cos(inc)*Math.sin(lan))
                - o_y*(Math.sin(aop)*Math.cos(lan) + Math.cos(aop)*Math.cos(inc)*Math.sin(lan)));

        ret.position.y = (float) ( o_x*(Math.cos(aop)*Math.sin(lan) + Math.sin(aop)*Math.cos(inc)*Math.cos(lan))
                + o_y*(Math.cos(aop)*Math.cos(inc)*Math.cos(lan) - Math.sin(aop)*Math.sin(lan)));

        ret.position.z = (float) ( o_x*Math.sin(aop)*Math.sin(inc) + o_y*Math.cos(aop)*Math.sin(inc) );
        
        // calc Velocity
        double tmp = Math.sqrt(Math.abs(a)*mi_E)/rc;
        double ov_x = 0 - tmp*Math.sinh(H);
        double ov_y = 0 /*-*/ + tmp * Math.sqrt(e*e-1)*Math.cosh(H);
        
        ret.velocity.x = (float) ( ov_x*(Math.cos(lan)*Math.cos(aop) - Math.sin(aop)*Math.cos(inc)*Math.sin(lan))
                - ov_y*(Math.sin(aop)*Math.cos(lan) + Math.cos(aop)*Math.cos(inc)*Math.sin(lan)));

        ret.velocity.y = (float) ( ov_x*(Math.cos(aop)*Math.sin(lan) + Math.sin(aop)*Math.cos(inc)*Math.cos(lan))
                + ov_y*(Math.cos(aop)*Math.cos(inc)*Math.cos(lan) - Math.sin(aop)*Math.sin(lan)));

        ret.velocity.z = (float) ( ov_x*Math.sin(aop)*Math.sin(inc) + ov_y*Math.cos(aop)*Math.sin(inc) );
        
        return ret;
    }
    
    
    public static double atanh(double x)
    {
        return (Math.log(1 + x) - Math.log(1 - x))/2;
    }
    
    public static double acosh(double x)
    {
        return Math.log(x + Math.sqrt(x-1)*Math.sqrt(x+1));
    }
    
    enum TypeOfPosition
    {
        TrueAnomaly,
        MeanAnomaly,
        EccentricAnomaly,
        TimeSincePeriapis,
        None;
    }
    
    // Sets orbit to match state vector. Returns position as type of provided in returnPosition
    // returns angle in [rad] for TrueAnomaly, MeanAnomaly, EccentricAnomaly
    // returns time in [s] for TimeSincePeriapis. Return 0 for None
    double SetOrbitFromStateVector(StateVector state, TypeOfPosition returnPosition)
    {
        // hV - orbital momentum vector[km^2/s]
        Vector3 hV = new Vector3(state.position);
        hV = hV.crs(state.velocity);
        
        // eV 
        Vector3 eV = (new Vector3(state.velocity)).crs(hV).scl((float)(1/mi_E));
        eV.sub((new Vector3(state.position)).nor());
        
        // nV - vector pointing ascending node[km^2/s]
        Vector3 nV = new Vector3(0-hV.y, hV.x, 0);
        
        // trueAnomaly(v) in [rad]
        double trueAnomaly = Math.acos((new Vector3(eV)).dot(state.position)/(eV.len()*state.position.len()));
        if(state.position.dot(state.velocity) < 0)
        {
            trueAnomaly = 2*Math.PI - trueAnomaly;
        }
        
        double inclination = Math.acos(hV.z/hV.len());
        
        double eccentricity = eV.len();
        
        boolean hyperbolic = (eccentricity > 1);
        
        double eccentricAnomaly = 0;
        if(hyperbolic)
        {
            eccentricAnomaly = 2*atanh(Math.tan(trueAnomaly*0.5)*Math.sqrt((eccentricity-1)/(eccentricity+1)));
        }
        else
        {
            eccentricAnomaly = 2*Math.atan(Math.tan(trueAnomaly*0.5)/Math.sqrt((1+eccentricity)/(1-eccentricity)));
        }
        
        
        // longitude of ascending node
        double loANode = Math.acos(nV.x/nV.len());
        if(nV.y < 0)
        {
            loANode = 2*Math.PI - loANode;
        }
        
        double argOfPeriapsis = Math.acos(nV.dot(eV)/(nV.len()*eV.len()));
        if(eV.z < 0)
        {
            argOfPeriapsis = 2*Math.PI - argOfPeriapsis;
        }
        
        double semiMajorAxis = 1.0/((2/state.position.len())-(state.velocity.len2())/mi_E);
        
        //set the properties
        a = semiMajorAxis;
        // e - eccentricity
        e = eccentricity;

        // lan - longitude of ascending node[rad]
        lan = loANode;
        // inc - inclination[rad]
        inc = inclination;

        //argument of periapsis[rad]
        aop = argOfPeriapsis;
        
        // mark as dirty to regenerate vertices
        dirtyModel = true;
        
        if(returnPosition == TypeOfPosition.TrueAnomaly)
        {
            return trueAnomaly;
        }
        else if(returnPosition == TypeOfPosition.EccentricAnomaly)
        {
            return eccentricAnomaly;
        }
        else if(returnPosition == TypeOfPosition.MeanAnomaly)
        {
            if(hyperbolic)
            {
                return eccentricity*Math.sinh(eccentricAnomaly) - eccentricAnomaly;
            }
            else
            {
                return eccentricAnomaly - eccentricity*Math.sin(eccentricAnomaly);
            }
        }
        else if(returnPosition == TypeOfPosition.TimeSincePeriapis)
        {
            double meanAnomaly = 0;
            if(hyperbolic)
            {
                meanAnomaly = eccentricity*Math.sinh(eccentricAnomaly) - eccentricAnomaly;
            }
            else
            {
                meanAnomaly = eccentricAnomaly - eccentricity*Math.sin(eccentricAnomaly);
            }
            //double meanAnomaly = eccentricAnomaly - eccentricity*Math.sin(eccentricAnomaly);
            double absA = Math.abs(semiMajorAxis);
            double meanMotion = Math.sqrt(mi_E/(absA*absA*absA));
            
            return meanAnomaly/meanMotion;
        }
        else
        {
            return 0;
        }
    }
    
    //converts time that passed since last passage through periapsis to true anomaly
    double TimeSincePeriapsisToTrueAnomaly(double t_sinceP)
    {
        double n = Math.sqrt(mi_E/(a*a*a));
        
        //M - mean anomaly at time t_s
        double M = (t_sinceP * n);
        
        //normalize the M
        M = (M % (2*Math.PI));
        if(M < 0)
        {
            M = (2*Math.PI + M);
        }
        
        // Solve Kepler equation
        double E = SolveKeplerEquation(e, M, 0.01, (short)20);
        
        // v - true anomaly
        double atanX = Math.sqrt(1+e)*Math.sin(0.5*E);
        double atanY = Math.sqrt(1-e)*Math.cos(0.5*E);
        double v = 2* Math.atan2(atanX, atanY);
        
        return v;
    }
    
    
    double NormalizeTimeSincePeriapsis(double t_sinceP)
    {
        // calc cylce
        double T = 2*Math.PI/Math.sqrt(mi_E/(a*a*a));
        
        return t_sinceP % T;
    }
    
    static void Init(Camera cam)
    {
        String vertexShader = Gdx.files.internal("orbit_vertex.glsl").readString();
        String fragmentShader = Gdx.files.internal("orbit_fragment.glsl").readString();
        shader = new ShaderProgram(vertexShader,fragmentShader);
        
        OrbitMarker.Init();
    }
    
    OrbitModel()
    {
        VertexAttributes attrs = new VertexAttributes(VertexAttribute.Position());
        //vertices = new float[200*3];
        indices = new short[400];
        color = new float[] { 1.0f, 1.0f, 1.0f};
        
        vertices = calcOrbitPoints();
        
        /*
        for(short i=0; i<200; ++i)
        {
            Vector3 vec = new Vector3(4000f,0,0);
            Vector3 nvec = vec.rotate(new Vector3(0,0,1), 360.0f*(i/200.0f));
            vertices[3*i  ] = nvec.x;
            vertices[3*i+1] = nvec.y;
            vertices[3*i+2] = nvec.z;
            /*if(i == 1)
            {
                vertices[3*i  ] = 0.3f;
                vertices[3*i+1] = 0.3f;
                vertices[3*i+2] = 0.3f;
            }*//*
            
            indices[2*i] = i;
            indices[2*i+1] = (short) ((i+1)%200);
        }*/
            
        int[] VAO = new int[1];
        VAO[0] = 0;
        //VAOi = IntBuffer.wrap(VAO);
        VAOi = creatIntBuffer(VAO);
        err = Gdx.gl30.glGetError();
        Gdx.gl30.glGenVertexArrays(1, VAOi);
        err = Gdx.gl30.glGetError();
        Gdx.gl30.glBindVertexArray(VAOi.get(0));
        err = Gdx.gl30.glGetError();
        
        FloatBuffer vbo_data = createVerticesBuffer(vertices);
                //FloatBuffer.wrap(vertices);

        VBO = Gdx.gl30.glGenBuffer();
        err = Gdx.gl30.glGetError();
        Gdx.gl30.glBindBuffer(GL_ARRAY_BUFFER, VBO);
        err = Gdx.gl30.glGetError();
        Gdx.gl30.glBufferData(GL_ARRAY_BUFFER, numOfVertices*3*4, vbo_data, GL_DYNAMIC_DRAW);
        err = Gdx.gl30.glGetError();
        
        
        Gdx.gl30.glVertexAttribPointer(0, 3/*200*3*4*/, GL_FLOAT, false, 3*4 , 0);//(0, 3, GL_FLOAT, false, 200*3*4 , 0);
        err = Gdx.gl30.glGetError();
        Gdx.gl30.glEnableVertexAttribArray(0);
        err = Gdx.gl30.glGetError();
        
        dirtyModel = false;
        
        
        APmarker = new OrbitMarker(OrbitMarker.MarkerType.AP);
        PAmarker = new OrbitMarker(OrbitMarker.MarkerType.PA);
        arrivalMarker = new OrbitMarker(OrbitMarker.MarkerType.arrival);
        departureMarker = new OrbitMarker(OrbitMarker.MarkerType.departure);
        hoverMarker = new OrbitMarker(OrbitMarker.MarkerType.hover);
        // todo: there will be a list of maneuvers when(or if?) maneuvers will be implemented
        //maneuverMarker = new Marker(MarkerType.maneuver);
        
        UpdateMarkers();
    }
    
    void UpdateMarkers()
    {
        PAmarker.SetMarker(calcOrbitPositionAt(0).position, 0);
        PAmarker.active = true;
        if(e<1)
        {
            //calc time of apoapsis
            double APtime = Math.PI*Math.sqrt((a*a*a)/mi_E);
            APmarker.SetMarker(calcOrbitPositionAt(APtime).position, APtime);
            
            // for now there's no patching, so the departure and arrival points are only for hyperbolic instersecting with SOI
            arrivalMarker.Disable();
            departureMarker.Disable();
        }
        else
        {
            // hyperbolic orbits don't have apoapsis
            APmarker.Disable();
            
            // calc eccentric anomaly of departure point
            double H = acosh(Math.abs((a-SOI)/(a*e)));
            double depTs = Math.sqrt(Math.abs((a*a*a)/mi_E))*(e*Math.sinh(H)-H);
            departureMarker.SetMarker(calcOrbitPositionAt(depTs).position, depTs);
            
            arrivalMarker.SetMarker(calcOrbitPositionAt(-depTs).position, -depTs);
            
            
        }
    }
    
    void UpdateModel()
    {
        if(dirtyModel)
        {
            vertices = calcOrbitPoints();
            // todo: remove redundant copies and reallocations caused by regenerating vertex buffer
            FloatBuffer vbo_data = createVerticesBuffer(vertices);
            Gdx.gl30.glBindBuffer(GL_ARRAY_BUFFER, VBO);
            Gdx.gl30.glBufferSubData(GL_ARRAY_BUFFER, 0, numOfVertices*3*4, vbo_data);
            
            UpdateMarkers();
            
            dirtyModel = false;
        }
    }
    
    void Render(Camera cam)
    {
        //todo: consider adding minimal time-step between updatig model
        UpdateModel();
        
        //Gdx.gl30.gl
        shader.bind();
        err = Gdx.gl30.glGetError();
        shader.setUniformMatrix4fv("u_projTrans", cam.combined.getValues(), 0, 16);
        shader.setUniform3fv("u_color", color, 0, 3);
        err = Gdx.gl30.glGetError();
        
        Gdx.gl30.glBindBuffer(GL_ARRAY_BUFFER, VBO);
        err = Gdx.gl30.glGetError();
        Gdx.gl30.glBindVertexArray(VAOi.get(0));
        err = Gdx.gl30.glGetError();
        if(e>=1)
        {
            Gdx.gl30.glDrawArrays( GL_LINE_STRIP, 0, numOfVertices);
        }
        else
        {
            Gdx.gl30.glDrawArrays( GL_LINE_LOOP, 0, numOfVertices);
        }
        err = Gdx.gl30.glGetError();
        
        //orbitMesh.render(shader, GL20.GL_LINES);
        
        Gdx.gl30.glEnable(GL_BLEND);
        
        float[] markerScale = new float[2];
        markerScale[0] = 45.0f/Gdx.graphics.getWidth();
        markerScale[1] = 45.0f/Gdx.graphics.getHeight();
        APmarker.Render(cam, markerScale, color);
        PAmarker.Render(cam, markerScale, color);
        arrivalMarker.Render(cam, markerScale, color);
        departureMarker.Render(cam, markerScale, color);
        //maneuverMarker.Render(cam, markerScale, color);
        //hoverMarker.Render(cam, markerScale, color);
        
    }
    
    void Dispose()
    {
        
    }
    
    
    
    
  private static FloatBuffer createVerticesBuffer(float[] vertices){
    FloatBuffer buffer=ByteBuffer.allocateDirect(vertices.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
    buffer.put(vertices).position(0);
    return buffer;
  }
  
  private static IntBuffer creatIntBuffer(int[] vals){
    IntBuffer buffer=ByteBuffer.allocateDirect(vals.length * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
    buffer.put(vals).position(0);
    return buffer;
  }
 
}
