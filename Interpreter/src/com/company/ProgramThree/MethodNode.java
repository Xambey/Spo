package com.company.ProgramThree;

import com.company.Tokens.Token;

import java.util.List;

/**
 * Created by Андрей on 23.06.2017.
 */
public class MethodNode extends Node {
    private Token ReturnValue;
    private List<Argument> arguments;
    private String name;

    public MethodNode()
    {
        super(1);
    }
    public void SetName(String name){
        this.name = name;
    }
    public void AddArgument(Token vartype, Token name)
    {
        arguments.add(new Argument(vartype, name));
    }
    public void AddArgument(String vartype, String name)
    {
        arguments.add(new Argument(vartype, name));
    }
    public void AddArguments(List<Argument> arguments){
        for(Argument x : arguments)
            AddArgument(x.GetVarType(),x.GetName());
    }
    public void SetReturnValue(Token token){
        ReturnValue = token;
    }
}
