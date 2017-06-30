package com.company.ProgramThree;

import com.company.Tokens.Token;

import java.util.List;

/**
 * Created by Андрей on 30.06.2017.
 */
public class Expression {
    private List<Token> value;
    public void AddItem(Token token)
    {
        value.add(token);
    }
}
