#version 150

uniform vec2 resolution;
uniform vec2 position;
uniform vec3 color;
uniform sampler2D backbuffer;
out vec4 colorOut;
 
void main()
{
    vec2 vecFromCenter = gl_FragCoord.xy - position;
    float a = 1.0 - length(vecFromCenter) / (resolution.x/2.2);
    a = clamp(a, 0, 1);

    //colorOut = vec4(color, a);

    vec2 pos = ( gl_FragCoord.xy / resolution.xy );
    colorOut = texture2D(backbuffer, pos);
    colorOut = vec4(clamp(colorOut[0]+0.2, 0, 1), colorOut[1], colorOut[2], colorOut[3]);
} 