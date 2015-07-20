package com.example.android.wearablemessageapiexample;


import org.codehaus.plexus.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RobotCommand {

    public enum Command {
        Greet2,
        Sidestep_Right,
        Sidestep_Left,
        Left_Turn,
        Right_Turn,
        Advance,
        Right_Jab,
        Left_Jab,
        Left_Hook,
        Left_Uppercut,
        Right_Hand_Wave,
        Left_Hand_Wave,
        Back_Roll,
        Left_Attack1,
        Right_Attack1,
        Left_Attack2,
        Right_Attack2,
        Front_Attack,
        Left_Kick,
        Right_Kick,
        Left_Side_Kick,
        Horse_Dance,
        Sit,
        Right_Hook,
        Get_Up,
        Stand_Up,
        Greet1,
        Block_1,
        NoCommand
    }

    private static List<String> buttonMovements = createButtonMovements();

    private static List<String> createButtonMovements() {
        List<String> movementList = new  ArrayList<String>();
        movementList.add("Left_Turn(2)");
        movementList.add("Right_Turn(2)");
        movementList.add("Advance");
        return movementList;
    }

    private static  Map<String, Command> referenceMap = createReferenceMap();

    private List<Command> commands = new ArrayList<Command>();

    public RobotCommand(String voiceCommand){
        String[] voiceCommandArray = StringUtils.split(voiceCommand, " ");
        Command command;

        int size = voiceCommandArray.length;
        int i = 0;

        while (size != i) {
            try {
                command = matchStringWithMovement(voiceCommandArray[i], voiceCommandArray[i + 1]);
                if (command != Command.NoCommand) {
                    commands.add(command);
                    i++;
                }
            } catch (IndexOutOfBoundsException e) {
                command = matchStringWithMovement(voiceCommandArray[i]);
                if (command != Command.NoCommand) {
                    commands.add(command);
                }

            } finally {
                i++;
            }
        }

//        for (String word : voiceCommandArray) {
//            command = matchStringWithMovement(word);
//            if(command != Command.NoCommand){
//                commands.add(command);
//            }
//        }
    }


    private static Map<String,Command> createReferenceMap() {
        Map<String, Command> newReferenceMap = new HashMap<String, Command>();

        newReferenceMap.put("attack", Command.Front_Attack);
        newReferenceMap.put("back roll", Command.Back_Roll);
        newReferenceMap.put("left sidestep", Command.Sidestep_Left);
        newReferenceMap.put("right sidestep", Command.Sidestep_Right);
        newReferenceMap.put("right kick", Command.Right_Kick);
        newReferenceMap.put("left kick", Command.Left_Kick);
        newReferenceMap.put("sit", Command.Sit);
        newReferenceMap.put("stand up", Command.Stand_Up);
        newReferenceMap.put("get up", Command.Get_Up);
        newReferenceMap.put("greet",Command.Greet1);
        newReferenceMap.put("hi,hello,bye",Command.Right_Hand_Wave);
        newReferenceMap.put("dance",Command.Horse_Dance);
        newReferenceMap.put("block",Command.Block_1);
        newReferenceMap.put("right hook",Command.Right_Hook);

        return newReferenceMap;
    }

    private Command matchStringWithMovement(String word1, String word2){

        for (String key : referenceMap.keySet()) {
            if(key.contains(word1 + " " + word2)){
                return referenceMap.get(key);
            }
            if(key.contains(word1) && !key.contains(" ")){
                return referenceMap.get(key);
            }
        }
        for (Command command : Command.values()) {
            if(command.name().equals(word1)){
                return command;
            }
        }
        return Command.NoCommand;
    }

    private Command matchStringWithMovement(String word){

        for (String key : referenceMap.keySet()) {
            if(key.contains(word) && !key.contains(" ")){
                return referenceMap.get(key);
            }
        }
        for (Command command : Command.values()) {
            if(command.name().equals(word)){
                return command;
            }
        }
        return Command.NoCommand;
    }

    public String getCommand() {
        return buildCommand();
    }

    private String buildCommand() {
        String completeCommand = "";
        for(Command command: commands){
            completeCommand = completeCommand.concat(command.name() + "->");
        }
        if (!completeCommand.isEmpty()) {
            completeCommand = completeCommand.substring(0, completeCommand.length() - 2);
        }
        return  completeCommand;
    }


}
