#include "BTGLHandle.h"

void Handle::init(int type){
	//std::cout << "handle init" << std::endl;
	btVaoVbo = new BTVaoVbo();
	//������
	if (type == 1)
	{
		btVaoVbo->initVaoVbo();
	}
	if (type == 2)
	{
		btVaoVbo->initBallVaoVbo();
	}
	
	BTprogram* btProgram = new BTprogram();
	programId = btProgram->createProgram();
	btProgram->useProgram();
	btVaoVbo->initSampler(programId);
}
void Handle::drawTriangles(int type, float alpha, glm::mat4 viewMatrix , GLfloat aspect){
	//std::cout << "draw triangle" << std::endl;
	btVaoVbo->drawArrays(type , programId, alpha , viewMatrix , aspect);
}
Handle::Handle()
{
	printf("here is constructor\n");
}

Handle::~Handle()
{
}