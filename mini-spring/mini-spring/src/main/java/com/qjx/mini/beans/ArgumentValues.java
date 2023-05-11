package com.qjx.mini.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <Description>
 *
 * @author qinjiaxing on 2023/5/10
 * @author <others>
 */


public class ArgumentValues {

    private final List<ArgumentValue> argumentValueList = new ArrayList<ArgumentValue>();

    public ArgumentValues() {
    }

    public void addArgumentValue(ArgumentValue argumentValue) {
        this.argumentValueList.add(argumentValue);
    }

    public ArgumentValue getIndexedArgumentValue(int index) {
        ArgumentValue argumentValue = this.argumentValueList.get(index);
        return argumentValue;
    }

    public int getArgumentCount() {
        return (this.argumentValueList.size());
    }

    public boolean isEmpty() {
        return (this.argumentValueList.isEmpty());
    }
}