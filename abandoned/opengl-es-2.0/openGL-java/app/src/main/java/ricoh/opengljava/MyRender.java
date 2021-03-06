package ricoh.opengljava;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by 田帅 on 2017/7/22.
 */

public class MyRender implements GLSurfaceView.Renderer {
    private static final String TAG = "ooopppp";
    private Triangle mTriangle1;
    float[] coord1 = {
            -1.0f,-1.0f, 0.0f, // vertex 1
            1.0f,-1.0f, 0.0f, // vertex 2
            1.0f,-0.5f, 0.0f, // vertex 3

            1.0f, 1.0f, 0.0f, // vertex 4
            -1.0f, 1.0f, 0.0f, // vertex 5
            -1.0f, 0.5f, 0.0f, // vertex 6

            0.0f, 0.5f, 0.0f, // vertex 7
            -0.5f, 0.0f, 0.0f, // vertex 8
            0.5f, 0.0f, 0.0f, // vertex 9

            -0.5f, 0.0f, 0.0f, // vertex 12
            0.0f, -0.5f, 0.0f, // vertex 11
            0.5f, 0.0f, 0.0f, // vertex 12
    };
    float[] color1 = { 1f, 0f, 0f ,0f};

    //只调用一次．
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//        GLES20.glClearColor(0.0f,255.0f,0.0f,1.0f);
        mTriangle1 = new Triangle();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width ,height);
    }
    float grey = 0.0f;
    //系统在每次重绘GLSurfaceView时调用此方法．此方法是绘制图形对象的主要的执行点
    @Override
    public void onDrawFrame(GL10 gl) {
        grey += 0.01f;
        if (grey > 1.0f) {
            grey = 0.0f;
        }
        GLES20.glClearColor(grey, grey, grey, 1.0f);
        //checkGlError("glClearColor");
        GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        //GLES20.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
        //GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        Log.i("sdasda","draw  "+grey);
        mTriangle1.draw(coord1,color1);
    }

    /**
     * 画一个三角形
     */
    class Triangle {
        private String vertexShaderCode =
                "attribute vec4 vPosition;" +
                        "void main() {" +
                        "  gl_Position = vPosition;" +
                        "}";
        private String fragmentShaderCode =
                "precision mediump float;" +
                        "uniform vec4 vColor;" +
                        "void main() {" +
                        "  gl_FragColor = vColor;" +
                        "}";
        private int mProgram = GLES20.glCreateProgram();
        private int mPositionHandler;
        private float trianglecoords[] ;
        private FloatBuffer vertexBuffer;
        //数组中每个顶点的坐标数
        int COORDS_PER_VERTEX = 3;
//        private final int vertexStride = COORDS_PER_VERTEX * 4; // bytes per vertex
        private int mColorHandle;
        float color[] ;


        public void draw(float coords[],float c[]) {
            this.trianglecoords  = coords;
            this.color = c;
            int vertexCount = trianglecoords.length / COORDS_PER_VERTEX;
            /*
            //为三角形初始化顶点字节缓冲区
            //不能直接使用FloatBuffer ： Must use a native order direct Buffer
            //GLES20.glVertexAttribPointer(mPositionHandler, COORDS_PER_VERTEX,GLES20.GL_FLOAT,false,vertexStride,vertexBuffer);
            //最后一个参数有限制
            */
            ByteBuffer bb = ByteBuffer.allocateDirect(trianglecoords.length * 4);
//            for (int i = 0; i < trianglecoords.length; i++) {
//                Log.i(TAG,"float[] "+trianglecoords[i]);
//            }
//            for (int i = 0 ; i < bb.array().length ; i++) {
//                Log.i(TAG,"bb "+bb.array()[i]);
//            }
            //修改缓冲区的字节顺序
            //ByteOrder.nativeOrder():分配与硬件相同字节顺序的直接缓冲区，使本机代码库效率更高
            bb.order(ByteOrder.nativeOrder());
            Log.i(TAG, "program: " + mProgram);
            vertexBuffer = bb.asFloatBuffer();
            vertexBuffer.put(trianglecoords);
            vertexBuffer.position(0);

//            float[] a = new float[vertexBuffer.limit()];
//            vertexBuffer.get(a);
//            for (int i = 0; i < a.length; i++) {
//                Log.i(TAG, "a " + a[i]);
//            }

            //准备渲染和openGL程序
            int vertexShader = MyRender.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
            int fragmentShader = MyRender.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
            Log.i(TAG, "vertex shader: " + vertexShader);
            Log.i(TAG, "fragment shader: " + fragmentShader);
            mProgram = GLES20.glCreateProgram();
            GLES20.glAttachShader(mProgram, vertexShader);
            GLES20.glAttachShader(mProgram, fragmentShader);
            //创建opengl程序的可执行文件/
            GLES20.glLinkProgram(mProgram);
            GLES20.glUseProgram(mProgram);

            mPositionHandler = GLES20.glGetAttribLocation(mProgram, "vPosition");
            Log.i(TAG , "mPositionHandler: "+mPositionHandler);
            GLES20.glEnableVertexAttribArray(0);
            GLES20.glVertexAttribPointer(0, COORDS_PER_VERTEX,GLES20.GL_FLOAT,true,0,vertexBuffer);
            //设置颜色
            mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
//            Log.i(TAG , "mColorHandle: "+mColorHandle);
            GLES20.glUniform4fv(mColorHandle, 1, color, 0);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
            // Disable vertex array
            GLES20.glDisableVertexAttribArray(mPositionHandler);
        }
    }

    private static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        Log.i(TAG, "type: " + type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

}
