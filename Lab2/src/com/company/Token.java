/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.company;

import java.util.regex.Matcher;

/**
 *
 * @author Андрей
 */
public class Token {
    private Lexem lexem;
    private String match;

    public Token(){}
    public Token(Lexem lexem, String match){
        this.lexem = lexem;
        this.match = match;
    }
    
    public Lexem getLexem(){
        return lexem;
    }
    
    public String getMatch(){
        return match;
    }

    @Override
    public String toString(){
        return String.format("Lexem: %s - Match: %s", lexem.toString(), match);
    }
}
