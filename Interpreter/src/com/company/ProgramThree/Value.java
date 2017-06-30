package com.company.ProgramThree;

import com.company.Tokens.Token;

import java.util.List;

/**
 * Created by Андрей on 30.06.2017.
 */
public class Value {
    private List<Token> expr;
    public void AddItem(Token token)
    {
        expr.add(token);
    }
}
