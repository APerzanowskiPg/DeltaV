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

out vec4 color;//gl_FragColor;

void main()
{
    /*gl_FragColor*/color = vec4(1.0 ,1.0 ,1.0, 1.0);//v_color * texture2D(u_texture, v_texCoords);
}