package frogma;

public class Frogma {

    public static void main(String[] args) {

        int fps = 50;
        int screenWidth = 1024;
        int screenHeight = 768;
        boolean fullscreen = false;

        for (String arg : args) {
            if (arg.equals("fullscreen")) {
                fullscreen = true;
            } else if (arg.startsWith("framerate=")) {
                fps = Integer.parseInt(arg.substring(4));
            } else {
                System.out.println("Unrecognized option " + arg);
                System.exit(1);
            }
        }

        System.out.println("Fullscreen: " + fullscreen);
        System.out.println("Resolution: " + screenWidth + "x" + screenHeight);
        System.out.println("Framerate: " + fps);

        new GameEngineImpl(screenWidth, screenHeight, fps, fullscreen).run();
    }

}
