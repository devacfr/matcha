package org.cfr.direct.testing;

import org.cfr.matcha.api.direct.DirectMethod;
import org.cfr.matcha.api.direct.IDirectAction;

public class MyAction implements IDirectAction {

    @DirectMethod
    public String myMethod(String test) {
        return this.getClass() + "called with data " + test;
    }

}
