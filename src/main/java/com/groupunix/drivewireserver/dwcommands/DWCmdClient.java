package com.groupunix.drivewireserver.dwcommands;


import com.groupunix.drivewireserver.dwprotocolhandler.DWVSerialProtocol;

public class DWCmdClient extends DWCommand {

    static final String command = "client";
    private final DWCommandList commands;

    DWCmdClient(DWVSerialProtocol dwProto, DWCommand parent) {
        setParentCmd(parent);
        commands = new DWCommandList(dwProto, dwProto.getCMDCols());

        commands.addcommand(new DWCmdClientRestart(dwProto, this));
    }


    public String getCommand() {
        return command;
    }

    public DWCommandList getCommandList() {
        return (this.commands);
    }


    public DWCommandResponse parse(String cmdline) {
        if (cmdline.length() == 0) {
            return (new DWCommandResponse(this.commands.getShortHelp()));
        }
        return (commands.parse(cmdline));
    }

    public String getShortHelp() {
        return "Commands that manage the attached client device";
    }


    public String getUsage() {
        return "dw client [command]";
    }

    public boolean validate(String cmdline) {
        return (commands.validate(cmdline));
    }
}
