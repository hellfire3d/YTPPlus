package zone.arctic.ytpplus;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Random;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;

/**
 * FFMPEG utilities toolbox for YTP+
 *
 * @author benb
 */
public class Utilities {

    public String FFPROBE;
    public String FFMPEG;
    public String MAGICK;

    public String TEMP = "";
    public String SOURCES = "";
    public String SOUNDS = "";
    public String MUSIC = "";
    public String RESOURCES = "";
    private Random random = new Random();

    /**
     * Return the length of a video (in seconds)
     *
     * @param video input video filename to work with
     * @return Video length as a string (output from ffprobe)
     */
    public String getVideoLength(String video){
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(FFPROBE
                    + " -v error"
                    + " -sexagesimal"
                    + " -show_entries format=duration"
                    + " -of default=noprint_wrappers=1:nokey=1"
                    + " \"" + video + "\"");
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String s;
            proc.waitFor();
            while ((s = stdInput.readLine()) != null) {
                return s;
            }
        } catch (Exception ex) {ex.printStackTrace(); return "";}
        return "";
    }

    public String getLength(String file) {
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(FFPROBE
                    + " -i \"" + file + "\""
                    + " -show_entries format=duration"
                    + " -v error"
                    + " -of csv=p=0");
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String s;
            proc.waitFor();
            while ((s = stdInput.readLine()) != null) {
                return s;
            }
        } catch (Exception ex) {System.out.println(ex); return "";}
        return "";
    }

    /**
     * Snip a video file between the start and end time, and save it to an output file.
     *
     * @param video input video filename to work with
     * @param startTime start time (in TimeStamp format, e.g. new TimeStamp(seconds);)
     * @param endTime start time (in TimeStamp format, e.g. new TimeStamp(seconds);)
     * @param output output video filename to save the snipped clip to
     */
    public void snipVideo(String video, TimeStamp startTime, TimeStamp endTime, String output){
        try {
            String command1 = FFMPEG
                    + " -ss " + startTime.getTimeStamp()
                    + " -i \"" + video + "\""
                    + " -to " + endTime.getTimeStamp()
                    + " -copyts"
                    + " -ac 1"
                    + " -ar 44100"
                    + " -vf scale=640x480,setsar=1:1,fps=fps=30"
                    + " -y"
                    + " " + output;
            CommandLine cmdLine = CommandLine.parse(command1);
            DefaultExecutor executor = new DefaultExecutor();
            int exitValue = executor.execute(cmdLine);
            if (exitValue==1) {
                System.out.println("ERROR");
                System.exit(0);
            }
        } catch (Exception ex) {System.out.println(ex);}
    }

    /**
     * Copies a video and encodes it in the proper format without changes.
     *
     * @param video input video filename to work with
     * @param output output video filename to save the snipped clip to
     */
    public void copyVideo(String video, String output){
        try {
            String command1 = FFMPEG
                    + " -i \"" + video + "\""
                    + " -ac 1"
                    + " -ar 44100"
                    + " -vf scale=640x480,setsar=1:1,fps=fps=30"
                    + " -y"
                    + " " + output;
            CommandLine cmdLine = CommandLine.parse(command1);
            DefaultExecutor executor = new DefaultExecutor();
            int exitValue = executor.execute(cmdLine);
            if (exitValue==1) {
                System.out.println("ERROR");
                System.exit(0);
            }
        } catch (Exception ex) {System.out.println(ex);}
    }

    /**
     * Concatenate videos by count
     *
     * @param count number of input videos to concatenate
     * @param out output video filename
     */
    public void concatenateVideo(int count, String out ) {
        try {
            File export = new File(out);

            if (export.exists())
                export.delete();

            String command1 = FFMPEG;

            for (int i=0; i<count; i++) {
                if (new File(TEMP + "video" + i + ".mp4").exists()) {
                    command1 = command1.concat(" -i " + TEMP + "video" + i + ".mp4");
                }
            }
            command1 = command1.concat(" -filter_complex ");

            int realcount = 0;
            for (int i=0; i<count; i++) {
                if (new File(TEMP + "video" + i + ".mp4").exists()) {
                    realcount+=1;
                }
            }
            for (int i=0; i<realcount; i++) {
                command1 = command1.concat("[" + i + ":v:0][" + i + ":a:0]");
            }

            //realcount +=1;
            command1=command1.concat("concat=n=" + realcount + ":v=1:a=1[outv][outa] -map [outv] -map [outa] -y " + out);
            System.out.println(command1);

            CommandLine cmdLine = CommandLine.parse(command1);
            DefaultExecutor executor = new DefaultExecutor();
            int exitValue = executor.execute(cmdLine);

            //cmdLine = CommandLine.parse(command2);
            //executor = new DefaultExecutor();
            //exitValue = executor.execute(cmdLine);

            //temp.delete();

        } catch (Exception ex) {System.out.println(ex);}
    }

    public synchronized int randomInt(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }

    public int randomInt() {
        return randomInt(0, (1 << 30) - 1);
    }

    public String getTempVideoName() {
        return TEMP + randomInt() + "-temp.mp4";
    }
}
