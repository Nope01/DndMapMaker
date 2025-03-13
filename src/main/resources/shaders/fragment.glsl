#version 330 core
out vec4 FragColor;

uniform vec3 color;
uniform int selected;
uniform int inLine;

void main() {

    FragColor = vec4(color, 1.0);
    vec3 selectedColour = vec3(173.0f/255.0f, 62.0f/255.0f, 62.0f/255.0f);
    vec3 lineColour = vec3(0.0f, 1.0f, 0.3f);

    if (selected > 0) {
        FragColor = vec4(selectedColour, 1);
    }

    if (inLine > 0) {
        FragColor = vec4(lineColour, 1);
    }
}