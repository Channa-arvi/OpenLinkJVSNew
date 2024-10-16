package com.customer.garolfcommon;

import com.olf.openjvs.*;
import com.olf.openjvs.enums.*;

public class GarOlfCommonScript implements IScript
{
    public void execute(IContainerContext context) throws OException
    {
        OConsole.oprint("Hello World!");
    }
}
