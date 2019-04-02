/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zone.arctic.ytpplus;

import java.io.File;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;

    
public class EffectsFactory {
    
    public Utilities toolBox;
    
    public EffectsFactory(Utilities utilities) {
        this.toolBox = utilities;
    }
    
    public String pickSound() {
        FilenameFilter fileFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                String lowercase = name.toLowerCase();
                if (lowercase.endsWith(".mp3")) 
                    return true;
                else 
                    return false;
            }
        };
        File[] files = new File(toolBox.SOUNDS).listFiles(fileFilter);
        Random rand = new Random();
        File file = files[rand.nextInt(files.length)];
        return file.getName();
    }
    public String pickSource() {
        FilenameFilter fileFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                String lowercase = name.toLowerCase();
                if (lowercase.endsWith(".mp4")) 
                    return true;
                else 
                    return false;
            }
        };
        File[] files = new File(toolBox.SOURCES).listFiles(fileFilter);
        Random rand = new Random();
        File file = files[rand.nextInt(files.length)];
        return file.getName();
    }
    
    public String pickMusic() {
        FilenameFilter fileFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                String lowercase = name.toLowerCase();
                if (lowercase.endsWith(".mp3")) 
                    return true;
                else 
                    return false;
            }
        };
        File[] files = new File(toolBox.MUSIC).listFiles(fileFilter);
        Random rand = new Random();
        File file = files[rand.nextInt(files.length)];
        return file.getName();
    }
    
    /* EFFECTS */
    public void effect_RandomSound(String video){
        System.out.println(new Object() {}.getClass().getEnclosingMethod().getName() + " initiated");
        try {
            File in = new File(video);
            File temp = new File(toolBox.TEMP + "temp.mp4");
            if (in.exists())
                in.renameTo(temp);
            String randomSound = pickSound();
            String command = toolBox.FFMPEG
                    + " -i " + toolBox.TEMP + "temp.mp4"
                    + " -i "+ toolBox.SOUNDS + randomSound
                    + " -filter_complex \"[1:a]volume=1,apad[A];[0:a][A]amerge[out]\""
                    + " -ac 2"
                    //+ " -c:v copy"
                    
                    + " -ar 44100"
                    + " -vf scale=640x480,setsar=1:1,fps=fps=30"
                    
                    + " -map 0:v"
                    + " -map [out]"
                    + " -y " + video;
            CommandLine cmdLine = CommandLine.parse(command);
            DefaultExecutor executor = new DefaultExecutor();
            int exitValue = executor.execute(cmdLine);
            temp.delete();
        } catch (Exception ex) {System.out.println(new Object() {}.getClass().getEnclosingMethod().getName() + "\n" +ex);}
    }   
    public void effect_RandomSoundMute(String video){
        System.out.println(new Object() {}.getClass().getEnclosingMethod().getName() + " initiated");
        try {
            String randomSound = pickSound();
            String soundLength = toolBox.getLength(toolBox.SOUNDS+randomSound);
            System.out.println("Doing a mute now. " + randomSound + " length: " + soundLength + ".");
            //Scanner userInput = new Scanner(System.in);
            //String input = userInput.nextLine();
            
            //if (!input.isEmpty()) {
                File in = new File(video);
                File temp = new File(toolBox.TEMP + "temp.mp4");
                File temp2 = new File(toolBox.TEMP + "temp2.mp4");
                if (in.exists())
                    in.renameTo(temp);
                if (temp2.exists())
                    temp2.delete();
                String command1 = toolBox.FFMPEG
                        + " -i " + toolBox.TEMP + "temp.mp4"
                        + " -ar 44100"
                        + " -vf scale=640x480,setsar=1:1,fps=fps=30"
                        + " -af \"volume=0\" -y " + toolBox.TEMP + "temp2.mp4";
                String command2 = toolBox.FFMPEG
                        + " -i " + toolBox.TEMP + "temp2.mp4"
                        + " -i \"" + toolBox.SOUNDS +""+randomSound+"\""
                        + " -to "+soundLength
                        + " -ar 44100"
                        + " -vf scale=640x480,setsar=1:1,fps=fps=30"
                        + " -filter_complex \"[1:a]volume=1,apad[A]; [0:a][A]amerge[out]\" -ac 2 -map 0:v -map [out] -y " + video;
                //String command1 = "lib/ffmpeg -i " + toolBox.TEMP + "temp.mp4 -i sounds/"+randomSound+" -c:v copy -map 0:v:0 -map 1:a:0 -shortest "+ video;
                CommandLine cmdLine = CommandLine.parse(command1);
                DefaultExecutor executor = new DefaultExecutor();
                int exitValue = executor.execute(cmdLine);

                cmdLine = CommandLine.parse(command2);
                executor = new DefaultExecutor();
                exitValue = executor.execute(cmdLine);

                temp.delete();
                temp2.delete();
            //}
            //System.out.println("Did a mute sfx. Type anything to verify.");

        } catch (Exception ex) {System.out.println(new Object() {}.getClass().getEnclosingMethod().getName() + "\n" +ex);}
    }   
    public void effect_Reverse(String video){
        System.out.println(new Object() {}.getClass().getEnclosingMethod().getName() + " initiated");
        try {
            File in = new File(video);
            File temp = new File(toolBox.TEMP + "temp.mp4");
            File temp2 = new File(toolBox.TEMP + "temp2.mp4");
            if (in.exists())
                in.renameTo(temp);
            if (temp2.exists())
                temp2.delete();
            String command1 = toolBox.FFMPEG
                    + " -i " + toolBox.TEMP + "temp.mp4 -map 0"
                    + " -ar 44100"
                    + " -vf scale=640x480,setsar=1:1,fps=fps=30"
                    + " -af \"areverse\" -y " + toolBox.TEMP + "temp2.mp4";
            String command2 = toolBox.FFMPEG
                    + " -i " + toolBox.TEMP + "temp2.mp4"
                    + " -ar 44100"
                    + " -vf scale=640x480,setsar=1:1,fps=fps=30"
                    + " -vf reverse -y " + video;

            CommandLine cmdLine = CommandLine.parse(command1);
            DefaultExecutor executor = new DefaultExecutor();
            int exitValue = executor.execute(cmdLine);
            
            cmdLine = CommandLine.parse(command2);
            executor = new DefaultExecutor();
            exitValue = executor.execute(cmdLine);
            
            temp.delete();
            temp2.delete();
        } catch (Exception ex) {System.out.println(new Object() {}.getClass().getEnclosingMethod().getName() + "\n" +ex);}
    }  
    
    
    public void effect_SpeedUp(String video){
        System.out.println(new Object() {}.getClass().getEnclosingMethod().getName() + " initiated");
        try {
            File in = new File(video);
            File temp = new File(toolBox.TEMP + "temp.mp4");
            if (in.exists())
                in.renameTo(temp);
            String command = toolBox.FFMPEG
                    + " -i " + toolBox.TEMP + "temp.mp4"
                    + " -ar 44100"
                    + " -vf scale=640x480,setsar=1:1,fps=fps=30"
                    + " -filter:v setpts=0.5*PTS -filter:a atempo=2.0 -y " + video;
            CommandLine cmdLine = CommandLine.parse(command);
            DefaultExecutor executor = new DefaultExecutor();
            int exitValue = executor.execute(cmdLine);
            temp.delete();
        } catch (Exception ex) {System.out.println(new Object() {}.getClass().getEnclosingMethod().getName() + "\n" +ex);}
    }   
    
    public void effect_SlowDown(String video){
        System.out.println(new Object() {}.getClass().getEnclosingMethod().getName() + " initiated");
        try {
            File in = new File(video);
            File temp = new File(toolBox.TEMP + "temp.mp4");
            if (in.exists())
                in.renameTo(temp);
            String command = toolBox.FFMPEG
                    + " -i " + toolBox.TEMP + "temp.mp4"
                    + " -ar 44100"
                    + " -vf scale=640x480,setsar=1:1,fps=fps=30"
                    + " -filter:v setpts=2*PTS -filter:a atempo=0.5 -y " + video;
            CommandLine cmdLine = CommandLine.parse(command);
            DefaultExecutor executor = new DefaultExecutor();
            int exitValue = executor.execute(cmdLine);
            temp.delete();
        } catch (Exception ex) {
            System.out.println(new Object() {}.getClass().getEnclosingMethod().getName() + "\n" +ex);
        }
    }   
    
    public void effect_Chorus(String video){
        System.out.println(new Object() {}.getClass().getEnclosingMethod().getName() + " initiated");
        try {
            File in = new File(video);
            File temp = new File(toolBox.TEMP + "temp.mp4");
            if (in.exists())
                in.renameTo(temp);
            String command = toolBox.FFMPEG
                    + " -i " + toolBox.TEMP + "temp.mp4"
                    + " -ar 44100"
                    + " -vf scale=640x480,setsar=1:1,fps=fps=30"
                    + " -af chorus=0.5:0.9:50|60|40:0.4|0.32|0.3:0.25|0.4|0.3:2|2.3|1.3 -y " + video;
            CommandLine cmdLine = CommandLine.parse(command);
            DefaultExecutor executor = new DefaultExecutor();
            int exitValue = executor.execute(cmdLine);
            temp.delete();
        } catch (Exception ex) {System.out.println(new Object() {}.getClass().getEnclosingMethod().getName() + "\n" +ex);}
    }   

    public void effect_Vibrato(String video){
        System.out.println(new Object() {}.getClass().getEnclosingMethod().getName() + " initiated");
        try {
            File in = new File(video);
            File temp = new File(toolBox.TEMP + "temp.mp4");
            if (in.exists())
                in.renameTo(temp);
            String command = toolBox.FFMPEG
                    + " -i " + toolBox.TEMP + "temp.mp4"
                    + " -ar 44100"
                    + " -vf scale=640x480,setsar=1:1,fps=fps=30"
                    + " -af vibrato=f=7.0:d=0.5 -y " + video;
            CommandLine cmdLine = CommandLine.parse(command);
            DefaultExecutor executor = new DefaultExecutor();
            int exitValue = executor.execute(cmdLine);
            temp.delete();
        } catch (Exception ex) {System.out.println(new Object() {}.getClass().getEnclosingMethod().getName() + "\n" +ex);}
    } 
    
    public void effect_LowPitch(String video) {
        System.out.println(new Object() {}.getClass().getEnclosingMethod().getName() + " initiated");
        try {
            File in = new File(video);
            File temp = new File(toolBox.TEMP + "temp.mp4");
            if (in.exists())
                in.renameTo(temp);
            String command = toolBox.FFMPEG
                    + " -i " + toolBox.TEMP + "temp.mp4"
                    + " -ar 44100"
                    + " -vf scale=640x480,setsar=1:1,fps=fps=30"
                    + " -filter:v setpts=2*PTS -af asetrate=44100*0.5,aresample=44100 -y " + video;
            CommandLine cmdLine = CommandLine.parse(command);
            DefaultExecutor executor = new DefaultExecutor();
            int exitValue = executor.execute(cmdLine);
            temp.delete();
        } catch (Exception ex) {System.out.println(new Object() {}.getClass().getEnclosingMethod().getName() + "\n" +ex);}
    }

    public void effect_HighPitch(String video) {
        System.out.println(new Object() {}.getClass().getEnclosingMethod().getName() + " initiated");
        try {
            File in = new File(video);
            File temp = new File(toolBox.TEMP + "temp.mp4");
            if (in.exists())
                in.renameTo(temp);
            String command = toolBox.FFMPEG
                    + " -i " + toolBox.TEMP + "temp.mp4"
                    + " -ar 44100"
                    + " -vf scale=640x480,setsar=1:1,fps=fps=30"
                    + " -filter:v setpts=0.5*PTS -af asetrate=44100*2,aresample=44100 -y " + video;
            CommandLine cmdLine = CommandLine.parse(command);
            DefaultExecutor executor = new DefaultExecutor();
            int exitValue = executor.execute(cmdLine);
            temp.delete();
        } catch (Exception ex) {System.out.println(new Object() {}.getClass().getEnclosingMethod().getName() + "\n" +ex);}
    }
    
    public void effect_Dance(String video){
        System.out.println(new Object() {}.getClass().getEnclosingMethod().getName() + " initiated");
        try {
            File in = new File(video);
            
            File temp = new File(toolBox.TEMP + "temp.mp4"); //og file
            File temp2 = new File(toolBox.TEMP + "temp2.mp4"); //1st cut
            File temp3 = new File(toolBox.TEMP + "temp3.mp4"); //backwards (silent
            File temp4 = new File(toolBox.TEMP + "temp4.mp4"); //forwards (silent
            File temp5 = new File(toolBox.TEMP + "temp5.mp4"); //backwards & forwards concatenated
            File temp6 = new File(toolBox.TEMP + "temp6.mp4"); //backwards & forwards concatenated
            File temp7 = new File(toolBox.TEMP + "temp7.mp4"); //backwards & forwards concatenated
            
            // final result is backwards & forwards concatenated with music
            
            if (in.exists())
                in.renameTo(temp);
            if (temp2.exists())
                temp2.delete();
            if (temp3.exists())
                temp3.delete();
            if (temp4.exists())
                temp4.delete();
            if (temp5.exists())
                temp5.delete();
            if (temp6.exists())
                temp6.delete();
            if (temp7.exists())
                temp7.delete();
            
            String randomSound = pickMusic();
            
            /*
            lib/ffmpeg -i stu.mp4 -map 0 -ar 44100 -to 00:00:00.5 -vf "scale=640x480,setsar=1:1" -an -y " + toolBox.TEMP + "temp2.mp4
            lib/ffmpeg -i " + toolBox.TEMP + "temp2.mp4 -map 0 -ar 44100 -vf "reverse,scale=640x480,setsar=1:1" -y " + toolBox.TEMP + "temp3.mp4
            lib/ffmpeg -i " + toolBox.TEMP + "temp3.mp4 -map 0 -ar 44100 -vf "reverse,scale=640x480,setsar=1:1" -y " + toolBox.TEMP + "temp4.mp4
            lib/ffmpeg -i " + toolBox.TEMP + "temp3.mp4 -i " + toolBox.TEMP + "temp4.mp4 -filter_complex "[0:v:0][1:v:0][0:v:0][1:v:0][0:v:0][1:v:0][0:v:0][1:v:0]concat=n=8:v=1[outv]" -map "[outv]" -c:v libx264 -shortest -y " + toolBox.TEMP + "temp5.mp4
            lib/ffmpeg -i " + toolBox.TEMP + "temp5.mp4 -map 0 -ar 44100 -vf "setpts=0.5*PTS,scale=640x480,setsar=1:1" -af "atempo=2.0" -shortest -y " + toolBox.TEMP + "temp6.mp4
            lib/ffmpeg -i " + toolBox.TEMP + "temp6.mp4 -i music/dancemusic.mp3 -c:v libx264 -map 0:v:0 -map 1:a:0 -vf "scale=640x480,setsar=1:1,fps=fps=30" -shortest -y " + toolBox.TEMP + "temp7.mp4
            */
            ArrayList<String> commands = new ArrayList<String>();
            int randomTime = randomInt(3,9);
            int randomTime2 = randomInt(0,1);
            commands.add(toolBox.FFMPEG
                    + " -i " + toolBox.TEMP + "temp.mp4 -map 0"// -c:v copy"
                    + " -ar 44100"
                    + " -to 00:00:0"+randomTime2+"." + randomTime
                    + " -vf scale=640x480,setsar=1:1"
                    + " -an"
                    + " -y " + toolBox.TEMP + "temp2.mp4");
            
            commands.add(toolBox.FFMPEG
                    + " -i " + toolBox.TEMP + "temp2.mp4 -map 0"// -c:v copy"
                    + " -ar 44100"
                    + " -vf reverse,scale=640x480,setsar=1:1"
                    + " -y " + toolBox.TEMP + "temp3.mp4");
            
            commands.add(toolBox.FFMPEG
                    + " -i " + toolBox.TEMP + "temp3.mp4"
                    + " -ar 44100"
                    + " -vf reverse,scale=640x480,setsar=1:1"
                    + " -y " + toolBox.TEMP + "temp4.mp4");
            
            commands.add(toolBox.FFMPEG
                    + " -i " + toolBox.TEMP + "temp3.mp4"
                    + " -i " + toolBox.TEMP + "temp4.mp4"
                    + " -filter_complex \"[0:v:0][1:v:0][0:v:0][1:v:0][0:v:0][1:v:0][0:v:0][1:v:0]concat=n=8:v=1[outv]\""
                    + " -map \"[outv]\""
                    + " -c:v libx264 -shortest"
                    + " -y " + toolBox.TEMP + "temp5.mp4");
            
            commands.add(toolBox.FFMPEG
                    + " -i " + toolBox.TEMP + "temp5.mp4"
                    + " -map 0"
                    + " -ar 44100"
                    + " -vf \"setpts=0.5*PTS,scale=640x480,setsar=1:1\""
                    + " -af \"atempo=2.0\""
                    + " -shortest"
                    + " -y " + toolBox.TEMP + "temp6.mp4");
            
            commands.add(toolBox.FFMPEG
                    + " -i " + toolBox.TEMP + "temp6.mp4"
                    + " -i " + toolBox.MUSIC + randomSound
                    + " -c:v libx264"
                    + " -map 0:v:0 -map 1:a:0"
                    + " -vf \"scale=640x480,setsar=1:1,fps=fps=30\""
                    + " -shortest"
                    + " -y " + video);
            
            //String command = "lib/ffmpeg -i " + toolBox.TEMP + "temp.mp4 -i sounds/"+randomSound+" -c:v copy -map 0:v:0 -map 1:a:0 -shortest "+ video;
            for (String command : commands) {
                CommandLine cmdLine = CommandLine.parse(command);
                DefaultExecutor executor = new DefaultExecutor();
                int exitValue = executor.execute(cmdLine);
            }
            
            temp.delete();
            temp2.delete();
            temp3.delete();
            temp4.delete();
            temp5.delete();
            temp6.delete();
            temp7.delete();
        } catch (Exception ex) {System.out.println(new Object() {}.getClass().getEnclosingMethod().getName() + "\n" +ex);}
    }  
    
    public void effect_Squidward(String video){
        System.out.println(new Object() {}.getClass().getEnclosingMethod().getName() + " initiated");
        try {
            File in = new File(video);
            
            File temp = new File(toolBox.TEMP + "temp.mp4"); //og file
            
            // final result is backwards & forwards concatenated with music
            
            if (in.exists())
                in.renameTo(temp);
            
            ArrayList<String> commands = new ArrayList<String>();

            commands.add(toolBox.FFMPEG
                    + " -i " + toolBox.TEMP + "temp.mp4"// -c:v copy"
                    + " -vf \"select=gte(n\\,1)\""
                    + " -vframes 1"
                    + " -y " + toolBox.TEMP + "squidward0.png");
            
            for (int i=1; i<6; i++) { 
                String effect = "";
                int random = randomInt(0,6);
                switch (random) {
                    case 0: 
                        effect = " -flop";
                        break;
                    case 1:
                        effect = " -flip";
                        break;
                    case 2:
                        effect = " -implode -" + randomInt(1,3);
                        break;
                    case 3:
                        effect = " -implode " + randomInt(1,3);
                        break;
                    case 4:
                        effect = " -swirl " + randomInt(1,180);
                        break;
                    case 5:
                        effect = " -swirl -" + randomInt(1,180);
                        break;
                    case 6:
                        effect = " -channel RGB -negate";
                        break;
                    //case 7:
                    //    effect = " -virtual-pixel Black +distort Cylinder2Plane " + randomInt(1,90);
                    //    break;
                }
                commands.add(toolBox.MAGICK
                        + " convert " + toolBox.TEMP + "squidward0.png"
                        + effect
                        + " " + toolBox.TEMP + "squidward" + i + ".png"
                );
            }
            commands.add(toolBox.MAGICK
                    + " convert -size 640x480 canvas:black " + toolBox.TEMP + "black.png");
            
            if (new File(toolBox.TEMP + "concatsquidward.txt").exists())
                new File(toolBox.TEMP + "concatsquidward.txt").delete();
            PrintWriter writer = new PrintWriter(toolBox.TEMP + "concatsquidward.txt", "UTF-8");
            writer.write
                        ("file 'squidward0.png'\n" +
                        "duration 0.467\n" +
                        "file 'squidward1.png'\n" +
                        "duration 0.434\n" +
                        "file 'squidward2.png'\n" +
                        "duration 0.4\n" +
                        "file 'black.png'\n" +
                        "duration 0.834\n" +
                        "file 'squidward3.png'\n" +
                        "duration 0.467\n" +
                        "file 'squidward4.png'\n" +
                        "duration 0.4\n" +
                        "file 'squidward5.png'\n" +
                        "duration 0.467");
            writer.close();
            
            commands.add(toolBox.FFMPEG
                    + " -f concat"
                    + " -i " + toolBox.TEMP + "concatsquidward.txt"
                    + " -i " + toolBox.RESOURCES + "squidward/music.wav"
                    + " -map 0:v:0 -map 1:a:0"
                    + " -vf \"scale=640x480,setsar=1:1\""
                    + " -pix_fmt yuv420p"
                    + " -y " + video);
            
            //String command = "lib/ffmpeg -i " + toolBox.TEMP + "temp.mp4 -i sounds/"+randomSound+" -c:v copy -map 0:v:0 -map 1:a:0 -shortest "+ video;
            for (String command : commands) {
                System.out.println("Executing: " + command);
                CommandLine cmdLine = CommandLine.parse(command);
                DefaultExecutor executor = new DefaultExecutor();
                int exitValue = executor.execute(cmdLine);
            }
            
            temp.delete();
            for (int i=0; i<6; i++) {
                new File(toolBox.TEMP + "squidward"+i+".png").delete();
            }
            new File(toolBox.TEMP + "black.png").delete();
            new File(toolBox.TEMP + "concatsquidward.txt").delete();
        } catch (Exception ex) {System.out.println(new Object() {}.getClass().getEnclosingMethod().getName() + "\n" +ex);}
    }  
    
    public static int randomInt(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }
}
