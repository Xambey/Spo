/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.company;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Андрей
 */
public class Lexer {
    private Set<Lexem> lexems;
    private List<String> data;
    private List<Token> tokens;
    
    public Lexer(String path) throws IOException{
         data = Files.readAllLines(Paths.get(path));
         InitialLexems();
    }

    private void InitialLexems(){
        lexems = new HashSet<Lexem>();
        lexems.add(new Lexem("CONDITION",0,"^(if|while|for)$"));
        lexems.add(new Lexem("VARTYPE",1,"^(int|int32|string|double|long|float|short|byte|void|char)$"));
        lexems.add(new Lexem("VARNAME",2,"^[a-zA-Z_]\\w*$"));
        lexems.add(new Lexem("DIGIT",1,"^(0\\b|[1-9]\\d*)$"));
        lexems.add(new Lexem("OPERATION",2,"^(-|\\+|>|<|\\*|\\/|>=|<=)$"));
    }

    public void ShowMatches(){
        if(tokens.isEmpty())
            return;
        for(Token item: tokens){
            System.out.println(item.toString());
        }
    }

    public void ShowText(){
        if(tokens.isEmpty())
            return;
        for(String item: data){
            System.out.println(item);
        }
    }

    public void Parse() {
        tokens = new ArrayList<>();
        if (data.isEmpty())
            return;
        String list = data.toString();

        int i = 0;
        List<Lexem> current;
        List<Lexem> prev;

        while(i < list.length()){
            current = new ArrayList<Lexem>();
            prev = new ArrayList<Lexem>();

            int j = 0;
            while(j < list.length() && (j == 0 || !current.isEmpty())){
                prev = current;
                String str = list.substring(i,i + j + 1);
                current = FindLexem(str);
                j++;
            }

            if(!prev.isEmpty() && current.isEmpty()){
                prev.sort((a,b) -> Integer.compare(a.getPriority(),b.getPriority()));
                tokens.add(new Token(prev.get(0), list.substring(i, --j + i)));
            }
            i += j;
        }
    }

    private List<Lexem> FindLexem(String text){
        return lexems.stream().filter(p -> p.isMatches(text)).collect(Collectors.toList());
    }
}
