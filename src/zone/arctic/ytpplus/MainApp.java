package zone.arctic.ytpplus;


public class MainApp {
    
    public static void main(String[] args) {
        YTPGenerator generatorMain = new YTPGenerator("job_" + System.currentTimeMillis() + ".mp4");
        
        generatorMain.setMaxClips(20);

        generatorMain.addSource("jim.mp4");
        
        generatorMain.go();
        System.out.println("Starting");
        while (generatorMain.doneCount<0.25) {};
        System.out.println("25%");
        while (generatorMain.doneCount<0.50) {};
        System.out.println("50%");
        while (generatorMain.doneCount<0.75) {};
        System.out.println("75%");
        while (!generatorMain.done) {};
        System.out.println("Done");
    }
}
