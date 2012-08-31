#version 150

uniform vec2 resolution;
uniform vec2 position;
uniform float lightCircle;
uniform float lightMaxDist;
uniform vec3 color;
uniform sampler2D passability;
out vec4 colorOut;

const vec4 lightFree = vec4(1, 1, 1, 1);

vec4 passColor(vec2 p)
{
    vec2 pos = ( p / resolution.xy );
    pos.y = 0.75 - pos.y;
    return texture2D(passability, pos);
}

bool isFree(vec2 p)
{
    return passColor(p) == lightFree;
}

void main()
{
    vec2 vecFromLight = gl_FragCoord.xy - position;
    float distFromLight = length(vecFromLight);

    colorOut = vec4(1, 1, 1, 0);
    float a = 0;

    if (distFromLight > lightCircle)
    {
        a = 1.0 - distFromLight / lightMaxDist;
        a = clamp(a, 0, 1);
        a = a*a;
    }

    if (a > 0)
    {
        vec2 vecFromLightOne = vecFromLight / distFromLight;
        float currLightLen = distFromLight+1;

        bool testFailedForFirst = false;
        bool testFailed = false;
        while (currLightLen > lightCircle+2.5)
        {
            vec2 currVecFromLight = vecFromLightOne*currLightLen;
            vec2 testPoint = position + currVecFromLight;

            if (!isFree(testPoint))
            {
                if (currLightLen > distFromLight)
                {
                    testFailedForFirst = true;
                }
                else
                {
                    testFailed = true;
                    break;
                }
            }

            currLightLen -= 2;
        }

        if (testFailedForFirst)
        {
            a = (1.0 + a)/2.0;
        }
        if (!testFailed)
        {
            colorOut = vec4(color, a);
        }

    }
} 