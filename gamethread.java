
package com.game.snake;
 
import android.content.Context;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
 
public class GameThread extends Thread {
 
        private SurfaceHolder surfaceHolder = null;
 
        private Snake snake = null;
 
        private int FRAMES_PER_SECOND = 15;
 
        private long fpsTimer = 0;
 
        private boolean quit = false;
 
        private Paint textColor = null;
 
        private long snakePauseTimer = 0;
 
        // main FrameBuffer class
        private FrameBuffer frameBuffer = null;
 
        public void setSnakePauseTimer(long time) {
                this.snakePauseTimer = time;
        }
 
        public GameThread(SurfaceHolder surfaceHolder, Snake snake, FrameBuffer frameBuffer) {
                this.surfaceHolder = surfaceHolder;
                this.snake = snake;
                this.frameBuffer = frameBuffer;
                this.textColor = new Paint();
                this.textColor.setARGB(255, 255, 255, 255);
                this.textColor.setTextSize(25);
        }
 
        public void setQuit() {
                this.quit = true;
        }
 
        public void increaseFps() {
                this.FRAMES_PER_SECOND += 1;
        }
 
        public void run() {
                Canvas canvas = null;
 
                while (quit == false) {
 
                        // Reset fpsTimer
                        this.fpsTimer = System.currentTimeMillis();
 
                        if (System.currentTimeMillis() - this.snakePauseTimer >= 1000) {
                                snake.moveSnake();
                                snake.handleCollisions(this);
                        }
                       
                        // Render
                        canvas = this.surfaceHolder.lockCanvas(null);
                        synchronized(this.surfaceHolder) {
                                // Clear screen
                                canvas.drawColor(Color.BLACK);
                                // If we touched down we are going to draw a circle around the cordinates
                                if (this.frameBuffer.getCircleValue() == true)
                                        this.frameBuffer.drawCircle(canvas);
                                // Draw objects
                                this.snake.draw(canvas);
                                // Draw score
                                canvas.drawText(String.format("Score: %d", this.snake.getScore()), 10, 30, this.textColor);
                        }
                        this.surfaceHolder.unlockCanvasAndPost(canvas);
 
                        // Cap framerate
                        if ( (System.currentTimeMillis() - this.fpsTimer) < 1000 / this.FRAMES_PER_SECOND)
                                try {
                                        Thread.sleep( (1000 / this.FRAMES_PER_SECOND) - (System.currentTimeMillis() - this.fpsTimer));
                                } catch (InterruptedException e) {
                                        System.exit(1);
                                }
                }
        }
}
