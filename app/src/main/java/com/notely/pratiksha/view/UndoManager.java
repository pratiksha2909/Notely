package com.notely.pratiksha.view;


import java.util.Stack;

/**
 * Created by pratiksha on 5/1/18.
 */

public class UndoManager {

    private static UndoManager undoManager = null;
    private Stack<Pair> stack = new Stack<>();

    private UndoManager(){}

    public static UndoManager getInstance(){
        if(undoManager == null) {
            undoManager = new UndoManager();
        }

        return undoManager;
    }

    public void pushToStack(String textId, String text){
        stack.push(new Pair(textId, text));
    }

    public Pair popFromStack(){
        if(stack.isEmpty())
            return null;
        return stack.pop();
    }

}
