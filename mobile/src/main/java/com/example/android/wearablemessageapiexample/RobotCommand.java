package com.example.android.wearablemessageapiexample;


import org.codehaus.plexus.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RobotCommand {

    public enum Command {
        Greet2,
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

        for (String word : voiceCommandArray) {
            command = matchStringWithMovement(word);
            if(command != Command.NoCommand){
                commands.add(command);
            }
        }
    }

    private static Map<String,Command> createReferenceMap() {
        Map<String, Command> newReferenceMap = new HashMap<String, Command>();

        newReferenceMap.put("golpe;trompada;puño;piña;mano;pegar;golpear", Command.Front_Attack);
        newReferenceMap.put("patada,pie,patear,pata,pierna", Command.Left_Kick);
        newReferenceMap.put("sentado;sentarse;sentar;sientate", Command.Sit);
        newReferenceMap.put("parar;pararse;parate", Command.Stand_Up);
        newReferenceMap.put("levantar;levantarse", Command.Get_Up);
        newReferenceMap.put("saludar",Command.Greet1);
        newReferenceMap.put("hola,chau",Command.Right_Hand_Wave);
        newReferenceMap.put("bailar,baile,danza",Command.Horse_Dance);
        newReferenceMap.put("bloquear",Command.Block_1);
        newReferenceMap.put("gancho",Command.Right_Hook);

        return newReferenceMap;
    }

    private Command matchStringWithMovement(String word){

        for (String key : referenceMap.keySet()) {
            if(key.contains(word)){
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
