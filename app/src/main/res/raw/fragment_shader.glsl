
precision mediump float;
uniform sampler2D uTexture;
varying vec2 vTexture;uniform vec4 uColor;
void main() {
  gl_FragColor = texture2D(uTexture, vTexture);
}

