
package com.game.snake;
 
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.Color;
import android.graphics.Paint;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import java.util.Random;
import java.util.ArrayList;
 
interface Functionality {
 
        public void moveSnake();
        public void snakeCollision(GameThread gameThread);
        public void wallCollision(GameThread gameThread);
        public void fruitCollision(GameThread gameThread);
        public boolean checkFruitCollision();
        public void handleCollisions(GameThread gameThread);
        public void fruitUpdate();
        public void goToMenu(GameThread gameThread);
}
 
public class Snake implements Functionality {
 
        public final int LEFT = 0;
 
        public final int RIGHT = 1;
 
        public final int UP = 2;
 
        public final int DOWN = 3;
 
        private final int SNAKE_WIDTH = 10;
 
        private final int SNAKE_HEIGHT = 10;
 
        private final int START_X = 140;
 
        private final int START_Y = 50;
 
        private final int SCORE_INCREASE = 10;
 
        private int screenWidth = 0;
 
        private int screenHeight = 0;
 
        private int direction;
 
        private ArrayList<Rect> snakePieces = null;
 
        // Fruit bitmaps
        private Bitmap appleImage = null;
 
        private Bitmap orangeImage = null;
 
        private Bitmap bananaImage = null;
 
        private Bitmap watermelonImage = null;
 
        // Cordinates of the current fruit to render
        private Rect fruitCordinates = null;
 
        private Random randomGenerator = null;
 
        private Paint color = null;
 
        // Get the GameActivity instance so we can use the goBack method on collision
        private GameActivity gameActivity = null;
 
        private int snakeCount = 0;
 
        private int totalSnakeCount = 0;
 
        private int score = 0;
 
        // Some constants for selecting fruits
        private final int FRUIT_APPLE = 0;
 
        private final int FRUIT_ORANGE = 1;
 
        private final int FRUIT_BANANA = 2;
 
        private final int FRUIT_WATERMELON = 3;
 
        // The current fruit we are rendering
        private int currentFruit = 0;
 
        // Media player to play the "chomp sound"
        private MediaPlayer mPlayer = null;
 
        public Snake(Context context) {
                this.snakePieces = new ArrayList<Rect>(0);
 
                // Add 4 snake pieces
                for (int i = 0; i < 5; i++) {
                        if (i == 0) {
                                // Add the snake head
                                this.snakePieces.add(new Rect(this.START_X, this.START_Y, this.START_X + this.SNAKE_WIDTH,
                                                                                          this.START_Y + this.SNAKE_HEIGHT));
                        }
                        else {
                                // Add a snake piece
                                int left = this.START_X - (i * this.SNAKE_WIDTH);
                                this.snakePieces.add(new Rect(left, this.START_Y, left + this.SNAKE_WIDTH,
                                                                                          this.START_Y + this.SNAKE_HEIGHT));
                        }
                }
                // We start moving in the RIGHT direction
                this.direction = this.RIGHT;
 
                // Load apple bitmap
                this.appleImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.apple);
                if (this.appleImage == null)
                        System.exit(1);
 
                // Load orange bitmap
                this.orangeImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.orange);
                if (this.orangeImage == null)
                        System.exit(1);
 
                // Load banana bitmap
                this.bananaImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.banana);
                if (this.bananaImage == null)
                        System.exit(1);
 
                // Load grape bitmap
                this.watermelonImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.watermelon);
                if (this.watermelonImage == null)
                        System.exit(1);
 
                // Initialize Chomp sound
                this.mPlayer = MediaPlayer.create(context, R.raw.bite);
 
                // Initialize a random generator
                this.randomGenerator = new Random();
 
                // Initialize fruit cordinates
                this.fruitCordinates = new Rect(0, 0, this.appleImage.getWidth(), this.appleImage.getHeight());
 
                // Snake color
                this.color = new Paint();
                this.color.setARGB(255, 57, 177, 29);
 
                this.gameActivity = (GameActivity)context;
        }
 
        public void moveSnake() {
 
                Rect snakeHead = this.snakePieces.get(0);
 
                for (int i = this.snakePieces.size() - 1; i > 0; i--) {
                        // Set each snake piece at the cordinates the piece before had
                        Rect lastPiece = this.snakePieces.get(i - 1);
                        Rect currentPiece = this.snakePieces.get(i);
 
                        if (this.direction == this.LEFT)
                                currentPiece.offsetTo(lastPiece.left, lastPiece.top);
                        else if (this.direction == this.RIGHT)
                                currentPiece.offsetTo(lastPiece.left, lastPiece.top);
                        else if (this.direction == this.UP)
                                currentPiece.offsetTo(lastPiece.left, lastPiece.top);
                        else if (this.direction == this.DOWN)
                                currentPiece.offsetTo(lastPiece.left, lastPiece.top);
                }
               
                if (this.direction == this.LEFT)
                        snakeHead.offsetTo(snakeHead.left - this.SNAKE_WIDTH, snakeHead.top);
                else if (this.direction == this.RIGHT)
                        snakeHead.offsetTo(snakeHead.left + this.SNAKE_WIDTH, snakeHead.top);
                else if (this.direction == this.UP)
                        snakeHead.offsetTo(snakeHead.left, snakeHead.top - this.SNAKE_HEIGHT);
                else if (this.direction == this.DOWN)
                        snakeHead.offsetTo(snakeHead.left, snakeHead.top + this.SNAKE_HEIGHT);
        }
 
        public void fruitCollision(GameThread gameThread) {
                Rect snakeHead = this.snakePieces.get(0);
                if (this.checkFruitCollision() == true) {
                        Rect lastPiece = this.snakePieces.get(this.snakePieces.size() - 1);
 
                        // Add a new piece
                        if (this.direction == this.LEFT) {
                                this.snakePieces.add(new Rect(lastPiece.right, lastPiece.top,
                                                                                          lastPiece.right + this.SNAKE_WIDTH, lastPiece.top + this.SNAKE_HEIGHT));
                        }
                        else if (this.direction == this.RIGHT) {
                                this.snakePieces.add(new Rect(lastPiece.left - this.SNAKE_WIDTH, lastPiece.top,
                                                                                          lastPiece.left, lastPiece.top + this.SNAKE_HEIGHT));
                        }
                        else if (this.direction == this.UP) {
                                this.snakePieces.add(new Rect(lastPiece.left, lastPiece.bottom,
                                                                                          lastPiece.left + this.SNAKE_WIDTH, lastPiece.bottom + this.SNAKE_HEIGHT));
                        }
                        else if (this.direction == this.DOWN) {
                                this.snakePieces.add(new Rect(lastPiece.left, lastPiece.top - this.SNAKE_HEIGHT,
                                                                                          lastPiece.left + this.SNAKE_WIDTH, lastPiece.top));
                        }
 
                        // Change position for the apple and update score
                        this.fruitUpdate();
                        this.score += this.SCORE_INCREASE;
 
                        this.totalSnakeCount++;
                        if (this.totalSnakeCount <= 68) {
                                this.snakeCount++;
                                if (this.snakeCount == 4) {
                                        gameThread.increaseFps();
                                        this.snakeCount = 0;
                                }
                        }
                }
        }
 
        public void fruitUpdate() {
                int newX = this.randomGenerator.nextInt(this.screenWidth - 70);
                int newY = this.randomGenerator.nextInt(this.screenHeight - 70);
                if (newX < 70)
                        newX = 70;
                if (newY < 70)
                        newY = 70;
                this.fruitCordinates.offsetTo(newX, newY);
 
                // Select a new fruit, get last one first
                int lastFruit = this.currentFruit;
                while ( (this.currentFruit = this.randomGenerator.nextInt(this.FRUIT_WATERMELON + 1)) == lastFruit)
                        ;
        }
 
        public void wallCollision(GameThread gameThread) {
                Rect snakeHead = this.snakePieces.get(0);
                if ( (snakeHead.left < 0) || (snakeHead.right > this.screenWidth)
                   ||(snakeHead.top < 0) || (snakeHead.bottom > this.screenHeight)) {
                        this.goToMenu(gameThread);
                }
        }
 
        public void snakeCollision(GameThread gameThread) {
                Rect snakeHead = this.snakePieces.get(0);
                for (int i = 2; i < this.snakePieces.size(); i++) {
                        Rect snakePiece = this.snakePieces.get(i);
                        if (snakeHead.contains(snakePiece) == true) {
                                this.goToMenu(gameThread);
                        }
                }
        }
 
        public void handleCollisions(GameThread gameThread) {
                // Check for wall collision
                this.wallCollision(gameThread);
                // Check for a snake collision
                this.snakeCollision(gameThread);
                // Check for a fruit collision
                this.fruitCollision(gameThread);
        }
 
        public void setDirection(int direction) {
                this.direction = direction;
        }
 
        public int getDirection() {
                return this.direction;
        }
 
        public void draw(Canvas canvas) {
                // Render snake pieces
                for (int i = 0; i < this.snakePieces.size(); i++) {
                        Rect currentPiece = this.snakePieces.get(i);
                        canvas.drawRect(currentPiece, this.color);
                }
 
                // Render fruit
                if (this.currentFruit == this.FRUIT_APPLE)
                        canvas.drawBitmap(this.appleImage, null, this.fruitCordinates, null);
                else if (this.currentFruit == this.FRUIT_ORANGE)
                        canvas.drawBitmap(this.orangeImage, null, this.fruitCordinates, null);
                else if (this.currentFruit == this.FRUIT_BANANA)
                        canvas.drawBitmap(this.bananaImage, null, this.fruitCordinates, null);
                else if (this.currentFruit == this.FRUIT_WATERMELON)
                        canvas.drawBitmap(this.watermelonImage, null, this.fruitCordinates, null);
        }
 
        public void setScreenDimensions(int width, int height) {
                this.screenWidth = width;
                this.screenHeight = height;
                // Initialize the apple
                this.fruitUpdate();
        }
 
        public Rect getSnakeHead() {
                return this.snakePieces.get(0);
        }
 
        public int getScore() {
                return this.score;
        }
 
        public boolean checkFruitCollision() {
                Rect snakeHead = this.snakePieces.get(0);
 
                if (snakeHead.right < this.fruitCordinates.left)
                        return false;
                if (snakeHead.left > this.fruitCordinates.right)
                        return false;
                if (snakeHead.bottom < this.fruitCordinates.top)
                        return false;
                if (snakeHead.top > this.fruitCordinates.bottom)
                        return false;
                return true;
        }
 
        public void goToMenu(GameThread gameThread) {
                // Stop the gamethread
                gameThread.setQuit();
 
                // Update score
                SharedPreferences settings = this.gameActivity.getSharedPreferences("highscore", 0);
                for (int i = 1; i <= 5; i++) {
                        int val = settings.getInt("highscore" + i, 0);
                        if (this.score > val) {
                                SharedPreferences.Editor edit = settings.edit();
                                edit.putInt("highscore" + i, this.score);
                                edit.commit();
                                break;
                        }
                }
 
                // Go to the parent activity (Main screen)
                this.gameActivity.finish();
        }
}
