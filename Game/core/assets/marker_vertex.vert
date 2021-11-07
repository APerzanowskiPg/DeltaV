#version 300 es 
//attribute vec3 a_position;
in vec3 a_position;
out vec2 v_texCoords;
//layout (location = 0) in vec3 a_position;

uniform mat4 u_projTrans;
uniform vec2 u_scale;

void main()
{
    v_texCoords.x = a_position.x;
    v_texCoords.y = 1.0f-a_position.y;
    vec4 pos = u_projTrans * vec4(0.0f, 0.0f, 0.0f, 1.0f);//vec4(-0.5f, -0.5f, 0.0f, 1.0f);
    pos = vec4(pos.x/pos.w, pos.y/pos.w, pos.z/pos.w, 1.0f);
    pos.xyz = pos.xyz + /*a_position + */vec3((a_position.x-0.5f)*u_scale.x, (a_position.y-0.5f)*u_scale.y, 0.0f);
    //pos.xyz = pos.xyz + a_position.xyz;
    gl_Position = vec4(pos);//pos;//u_projTrans * vec4(a_position.x, a_position.y, a_position.z, 1);
}