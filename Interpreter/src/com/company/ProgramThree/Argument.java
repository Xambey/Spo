package com.company.ProgramThree;

import com.company.Enums.Term;
import com.company.Tokens.Token;

/**
 * Created by Андрей on 30.06.2017.
 */
public class Argument {
    private String VarType;
    private String Name;
    public String GetName()
    {
        return Name;
    }
    public String GetVarType()
    {
        return VarType;
    }
    public Argument(String varType, String name)
    {
        VarType = varType;
        Name = name;
    }
    public Argument(Token vartype, Token name){
        VarType = vartype.GetMatch();
        Name = name.GetMatch();
    }
}
