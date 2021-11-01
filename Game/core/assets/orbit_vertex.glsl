#version 300 es 
//attribute vec3 a_position;
in vec3 a_position;
//layout (location = 0) in vec3 a_position;

uniform mat4 u_projTrans;

void main()
{
    mat4 tmp = u_projTrans; 

    vec4 pos = u_projTrans * vec4(a_position.x, a_position.y, a_position.z, 1.0f);
    gl_Position = pos;//u_projTrans * vec4(a_position.x, a_position.y, a_position.z, 1);
}