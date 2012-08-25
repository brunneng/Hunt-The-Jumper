#version 150

uniform vec2 resolution;
uniform vec2 position;
uniform float lightCircle;
uniform vec3 color;
uniform sampler2D passability;
out vec4 colorOut;
 
void main()
{
    vec2 vecFromLight = gl_FragCoord.xy - position;
    float lightMaxLength = resolution.x/2;
    float distToLight = length(vecFromLight);

    float a = 0;

    if (distToLight > lightCircle)
    {
        a = 1.0 - distToLight / lightMaxLength;
        a = clamp(a, 0, 1);
        a = a*a;
    }

    //colorOut = vec4(color, a);

    vec2 pos = ( gl_FragCoord.xy / resolution.xy );
    pos.y = 0.75 - pos.y;
    colorOut = texture2D(passability, pos);
    //colorOut = vec4(clamp(colorOut[0]+0.2, 0, 1), colorOut[1], colorOut[2], colorOut[3]);
} 