package lightsim;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Random;

public class Main {

    private static int FPS = 120;
    private static int WWIDTH = 1920;
    private static int WHEIGHT = 1080;
    private static float maxRadius;
    private static int cameraX = 0;
    private static int cameraY = 0;
    private static float numSegments;
    private static int tick;
    static class Light {
        float x, y;
        float r, g, b;
        float alpha;

        Light(float x, float y) {
            this.x = x;
            this.y = y;
            Random rand = new Random();
            this.r = 0.5f + 0.5f * rand.nextFloat();
            this.g = 0.5f + 0.5f * rand.nextFloat();
            this.b = 0.5f + 0.5f * rand.nextFloat();
            this.alpha = 0.5f + 0.5f * rand.nextFloat();
        }

        void draw() {
            int numCircles = 5;

            for (int i = 0; i < numCircles; i++) {
                float radius = maxRadius * (i + 1) / numCircles;
                float currentAlpha = alpha * (1.0f - (float) i / numCircles);
                drawCircle(x - cameraX, y - cameraY, radius, r, g, b, currentAlpha);
            }
        }

        void drawCircle(float cx, float cy, float radius, float r, float g, float b, float alpha) {
            GL11.glColor4f(r, g, b, alpha);
            GL11.glBegin(GL11.GL_TRIANGLE_FAN);
            GL11.glVertex2f(cx, cy);
            for (int i = 0; i <= numSegments; i++) {
                double angle = 2 * Math.PI * i / numSegments;
                float dx = (float) (radius * Math.cos(angle));
                float dy = (float) (radius * Math.sin(angle));
                GL11.glVertex2f(cx + dx, cy + dy);
            }
            GL11.glEnd();
        }
    }

    static ArrayList<Light> lights = new ArrayList<>();

    public static void input() {
        try {
            if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) && Display.isCloseRequested()) {
                die();
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            	if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
                    cameraY += 7;
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
                    cameraY -= 7;
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
                    cameraX -= 7;
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
                    cameraX += 7;
                }
                
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
                cameraY += 5;
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
                cameraY -= 5;
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
                cameraX -= 5;
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
                cameraX += 5;
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_Z)) {
                maxRadius += 1;
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_X)) {
                maxRadius -= 1;
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_B)) {
                numSegments -= 1;
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_G)) {
                numSegments += 1;
            }


        } finally {
        }
    }

    public static void die() {
        Display.destroy();
        Keyboard.destroy();
        System.out.println("die");
    }

    public static void tick() {
    	tick++;
    	if(tick == FPS) {
    		tick = 0;
    	}
    }
    
    public static void main(String[] args) {
        try {
        	maxRadius = 10;
        	numSegments = 100f;
            System.out.println("Frame Per Second : " + FPS);
            Display.setTitle("The Rhomboid of Desire");
            Display.setFullscreen(true);
            Display.create();
            Keyboard.create();
            Display.setVSyncEnabled(true);

            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glLoadIdentity();
            GL11.glOrtho(0, WWIDTH, 0, WHEIGHT, -1, 1);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glLoadIdentity();

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

            while (!Display.isCloseRequested()) {
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
                GL11.glLoadIdentity();

                input();
                tick();
                System.out.println(tick);

                boolean wasMousePressed = false;

                boolean isMousePressed = Mouse.isButtonDown(0);

                if (isMousePressed && !wasMousePressed) {
                	int mouseX = Mouse.getX() + cameraX;
                	int mouseY = Mouse.getY() + cameraY;
                	lights.add(new Light(mouseX, mouseY));
                }

                wasMousePressed = isMousePressed;


                for (Light light : lights) {
                    light.draw();
                }

                Display.update();
                Display.sync(FPS);
                if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
                	die();
                }
            }

        } catch (LWJGLException e) {
            e.printStackTrace();
        } finally {
            die();
        }
    }
}
