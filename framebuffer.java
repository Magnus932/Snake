
package com.game.snake;
 
import android.content.Context;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.view.MotionEvent;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Paint;
import android.os.SystemClock;
 
public class FrameBuffer extends SurfaceView implements SurfaceHolder.Callback {
 
        private GameThread gameThread = null;
 
        private Snake snake = null;
 
        private int screenWidth = 0;
 
        private int screenHeight = 0;
 
        // Used for motion detecting
        private int saveX = 0;
 
        private int saveY = 0;
 
        // Shall we draw a circle on touchDown?
        private boolean circle = false;
 
        // Circle color
        private Paint circleColor = null;
 
        private boolean pauseGameOnRotation = false;
 
 
        public FrameBuffer(Context context) {
                super(context);
                this.getHolder().addCallback(this);
                // Initialize the snake
                this.snake = new Snake(context);
 
                // Initialize the game thread
                this.gameThread = new GameThread(this.getHolder(), this.snake, this);
 
                this.circleColor = new Paint();
                this.circleColor.setARGB(255, 27, 68, 143);
        }
 
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
                Canvas canvas = holder.lockCanvas(null);
                synchronized(holder) {
                        // Set the width and height of the Framebuffer
                        this.snake.setScreenDimensions(canvas.getWidth(), canvas.getHeight());
                        // Save them here aswell for motion detecting
                        this.screenWidth = canvas.getWidth();
                        this.screenHeight = canvas.getHeight();
                }
                holder.unlockCanvasAndPost(canvas);
                // Start the game thread
                this.gameThread.start();
        }
 
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                this.gameThread.setSnakePauseTimer(System.currentTimeMillis());
        }
 
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
                this.gameThread.setQuit();
        }
 
        @Override
        public boolean onTouchEvent(MotionEvent event) {
                int x = (int)event.getX();
                int y = (int)event.getY();
 
                switch(event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                                this.saveX = x;
                                this.saveY = y;
                                // We are going to draw a circle
                                this.circle = true;
                        break;
                        case MotionEvent.ACTION_UP:
                                if (this.saveX != 0 && this.saveY != 0) {
                                        if ( (x > this.saveX + 50) && this.snake.getDirection() != this.snake.LEFT)
                                                this.snake.setDirection(this.snake.RIGHT);
                                        else if ( (x < this.saveX - 50) && this.snake.getDirection() != this.snake.RIGHT)
                                                this.snake.setDirection(this.snake.LEFT);
                                        else if ( (y > this.saveY + 50) && this.snake.getDirection() != this.snake.UP)
                                                this.snake.setDirection(this.snake.DOWN);
                                        else if ( (y < this.saveY - 50) && this.snake.getDirection() != this.snake.DOWN)
                                                this.snake.setDirection(this.snake.UP);
                                }
                                // Reset
                                this.saveX = 0;
                                this.saveY = 0;
                                // We are going to stop drawing a circle
                                this.circle = false;
                        break;
                }
 
                return true;
        }
 
        public void drawCircle(Canvas canvas) {
                canvas.drawCircle(this.saveX, this.saveY, 10, this.circleColor);
        }
 
        public boolean getCircleValue() {
                return this.circle;
        }
}
