/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.company;

import java.util.regex.Pattern;

/**
 *
 * @author Андрей
 */
public class Lexem {
    private Pattern pattern;
    private String name;
    private int priority;

    public Lexem(){}
    public Lexem(String name, int priority, String pattern){
        this.pattern = Pattern.compile(pattern);
        this.name = name;
        this.priority = priority;
    }
    
    public Pattern getPattern(){
        return pattern;
    }
    
    public String getName(){
        return name;
    }

    public int getPriority(){ return priority; }

    public boolean isMatches(String str){ return pattern.matcher(str).find(); }

    @Override
    public String toString(){ return String.format("{pattern:%s - name:%s - Priority:%d}", pattern.pattern(),name, priority); }
}
