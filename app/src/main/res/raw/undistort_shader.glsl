precision mediump float;

uniform vec2 focalLength;
uniform vec2 opticalCenter;
uniform vec4 distortionCoefficients;
uniform vec2 tangentialCoefficients;
uniform vec2 imageSize;

varying vec2 vTexture;

uniform sampler2D uTexture;
/**
 * Loosely based on:
 *
 * http://stackoverflow.com/questions/25871452/distortion-correction-with-gpu-shader-bug
 *
 * But with tangential correction added, fixed handling of units, and rendering out of
 * bounds pixels as black.
 *
 */

void main()
{

    vec4 black = vec4(0.0,0.0,0.0,1.0);

    vec2 opticalCenterUV = opticalCenter / imageSize;

    vec2 shiftedUVCoordinates = (vTexture - opticalCenterUV);

    vec2 lensCoordinates = (vTexture*imageSize-opticalCenter)/focalLength;

    float radiusSquared    = dot(lensCoordinates, lensCoordinates);
    float radiusQuadrupled = radiusSquared * radiusSquared;
    float radiusSextupled  = radiusQuadrupled * radiusSquared;
    float radiusOctupled   = radiusSextupled * radiusSquared;

    // You could add as many coefficient terms as you want, just follow this pattern...
    // The example had two, I added 3 and 4.
    float coefficientTerm  = distortionCoefficients.x * radiusSquared +
                             distortionCoefficients.y * radiusQuadrupled +
                             distortionCoefficients.z * radiusSextupled;
                             distortionCoefficients.w * radiusOctupled;
    lensCoordinates *= focalLength;

    vec2 distortedUV = (((lensCoordinates) + (lensCoordinates) * (coefficientTerm)));

    // Tangential correction
    distortedUV.x += tangentialCoefficients[1]*(radiusSquared+2*lensCoordinates.x*lensCoordinates.x) +
                     2*tangentialCoefficients[0]*lensCoordinates.x*lensCoordinates.y;
    distortedUV.y += tangentialCoefficients[0]*(radiusSquared+2*lensCoordinates.y*lensCoordinates.y) +
                     2*tangentialCoefficients[1]*lensCoordinates.x*lensCoordinates.y;

    vec2 resultUV = (distortedUV + opticalCenterUV)/focalLength + .5;

    // render out of bounds as black.
    if((resultUV.x < 0.0)||(resultUV.x > 1.0)||(resultUV.y < 0.0)||(resultUV.y > 1.0)) {
        gl_FragColor = black;
    } else {
        gl_FragColor = texture2D(uTexture, resultUV);
    }
}