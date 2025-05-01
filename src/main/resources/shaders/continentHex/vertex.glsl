#version 410 core
layout(location = 0) in vec3 aPos;
layout(location = 1) in vec2 tex;

out vec2 texCoords;
out vec2 iconTexCoords;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main() {
    texCoords = tex;
    mat2 scale = mat2(1.5f, 0.0f,
                      0.0f  , 1.5f);
    iconTexCoords = (texCoords * scale) - vec2(0.25f, 0.25f);
    gl_Position = projection * view * model * vec4(aPos, 1.0);
}