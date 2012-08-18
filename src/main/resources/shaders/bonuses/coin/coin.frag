#version 150

uniform vec2 position;
uniform float angle;
uniform float len;
uniform float sphereRadius;

out vec4 colorOut;

const vec3 colors[] = vec3[3](vec3(1, 0, 0), vec3(0, 0.7, 0.3), vec3(0, 0, 1));
const float PI = 3.14159265358979323;

vec4 blend(vec4 c1, vec4 c2)
{
    vec4 res = vec4(0, 0, 0, 0);
    if (c2.w > c1.w)
    {
        vec4 tmp = c2;
        c2 = c1;
        c1 = tmp;
    }

    if (c2.w <= 0)
    {
        res = c1;
    }
    else
    {
        res = vec4(clamp(c1.xyz + c2.xyz * (c2.w / c1.w), 0, 1), c1.w);
    }
    return res;
}

void main()
{
    colorOut = vec4(0, 0, 0, 0);

    float maxA = 0.0;
    float angleStep = 2*PI / 3;
    float currAngle = radians(angle);
    for (int i = 0; i < 3; i++)
    {
        vec2 dir = vec2(len*cos(currAngle), len*sin(currAngle));
        vec2 centerPos = position + dir;
        vec2 vecFromCenter = gl_FragCoord.xy - centerPos;

        float a = 1.0 - clamp(length(vecFromCenter) / sphereRadius, 0, 1);
        vec4 newColor = vec4(colors[i], a);

        colorOut = blend(colorOut, newColor);
        currAngle = currAngle + angleStep;
    }

    vec2 centerPos = position;
    vec2 vecFromCenter = gl_FragCoord.xy - centerPos;
    float a = 1.0 - clamp(length(vecFromCenter) / sphereRadius, 0, 1);
    a = a*a;
    vec4 newColor = vec4(0.6, 0.6, 0.6, a);
    colorOut = blend(colorOut, newColor);

    colorOut.w *= 0.9;
}