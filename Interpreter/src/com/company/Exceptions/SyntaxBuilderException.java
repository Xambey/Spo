package com.company.Exceptions;

import com.company.ProgramThree.Node;
import com.company.ProgramThree.RootNode;

/**
 * Created by Андрей on 23.06.2017.
 */
public class SyntaxBuilderException extends Exception {
    private String message;
    private String nodeName;
    private int level;
    public SyntaxBuilderException(){}
    public SyntaxBuilderException(String message,String nodeName,int level)
    {
        this.message = message;
        this.nodeName = nodeName;
        this.level = level;
    }
    @Override
    public String toString()
    {
        return String.format("Node: %s Message: %s Level: %d", nodeName, message, level);
    }
}
