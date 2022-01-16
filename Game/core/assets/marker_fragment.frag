#version 300 es 
precision highp float;
/* 
#ifdef GL_ES
#define LOWP lowp
    precision mediump float;
#else
    #define LOWP
#endif

varying LOWP vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;*/

in vec2 v_texCoords;

uniform sampler2D u_texture;
uniform vec3 u_color;

out vec4 color;//gl_FragColor;

void main()
{
    /*gl_FragColor*/color = texture(u_texture, v_texCoords);
    color.x = color.x*u_color.x;
    color.y = color.x*u_color.y;
    color.z = color.x*u_color.z;
}