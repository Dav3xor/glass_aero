attribute vec4 aPosition;
attribute vec2 aTexture;
varying   vec2 vTexture;
void main() {
  vTexture = aTexture;
  gl_Position = aPosition;
}