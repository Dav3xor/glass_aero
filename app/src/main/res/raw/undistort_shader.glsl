precision mediump float;

uniform vec4 uColor;
in vec2 vTexture;

uniform sampler2D uTexture;

void main()
{
    vec2 focalLength = vec2(1024.568f, 768.699f);
    vec2 opticalCenter = vec2(512.0f, 384.0f);
    vec4 distortionCoefficients = vec4(-0.035109f, -0.002393f, 0.000335f, -0.000449f);

    const vec2 imageSize = vec2(1024.f, 768.f);

    vec2 opticalCenterUV = opticalCenter / imageSize;

    vec2 shiftedUVCoordinates = (vTexture - opticalCenterUV);

    vec2 lensCoordinates = (vTexture*imageSize-opticalCenter)/focalLength;
    //vec2 lensCoordinates = shiftedUVCoordinates / focalLength;

    float radiusSquared = dot(lensCoordinates, lensCoordinates);
    float radiusQuadrupled = radiusSquared * radiusSquared;

    float coefficientTerm = distortionCoefficients.x * radiusSquared + distortionCoefficients.y * radiusQuadrupled;

    vec2 distortedUV = (((lensCoordinates*focalLength) + (lensCoordinates*focalLength) * (coefficientTerm)));

    vec2 resultUV = (distortedUV + opticalCenterUV)/focalLength + .5;

    gl_FragColor = texture2D(uTexture, resultUV);
}