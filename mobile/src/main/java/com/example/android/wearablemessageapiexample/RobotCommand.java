package com.example.android.wearablemessageapiexample;


import org.codehaus.plexus.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class RobotCommand {

    public enum Command {
        Greet2,
        Right_Jab,
        Right_Hook,
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
        NoCommand
    }

    private Map<String, Command> referenceMap;

    private Command command;

    public RobotCommand(String voiceCommand){

        createReferenceMap();

        String[] voiceCommandArray = StringUtils.split(voiceCommand, "#");

        for (String word : voiceCommandArray) {
            command = matchStringWithMovement(word);
            if(command != Command.NoCommand){
                break;
            }
        }
    }

    private void createReferenceMap() {
        referenceMap = new HashMap<String, Command>();

        referenceMap.put("golpe;trompada;puño;mano;pegar;golpear", Command.Front_Attack);
        referenceMap.put("patada,pie,patear,pata,pierna", Command.Left_Kick);

    }

    private Command matchStringWithMovement(String word){

        for (String key : referenceMap.keySet()) {
            if(key.contains(word)){
                return referenceMap.get(key);
            }
        }

        return Command.NoCommand;
    }

    public Command getCommand() {
        return command;
    }




}
