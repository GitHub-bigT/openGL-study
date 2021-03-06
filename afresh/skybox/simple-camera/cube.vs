#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aNormal;
//layout (location = 2) in vec2 aTexCoords;

//out vec2 TexCoords;
out vec3 Position;
out vec3 Normal;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main()
{
	//问题：没有用法线矩阵，效果看起来差不多
	Normal = mat3(transpose(inverse(model))) * aNormal;
    //Position = vec3(model * vec4(aPos, 1.0));
	//Normal = aNormal;
	//Position = aPos;
	//Normal = vec3(model * vec4(aNormal, 1.0));
	Position = vec3(model * vec4(aPos, 1.0));
    //TexCoords = aTexCoords;
    gl_Position = projection * view * model * vec4(aPos, 1.0);
	gl_PointSize = 10.0f;
}