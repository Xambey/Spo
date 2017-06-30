package com.company.Vocabulary;

import com.company.Enums.Term;
import com.company.Tokens.Token;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private final List<Lexeme> lexems = new ArrayList<>();

    public boolean AddLexeme(Lexeme lexeme)
    {
        if(!lexems.contains(lexeme)) {
            lexems.add(lexeme);
            return true;
        }
        else
            return false;
    }
    public boolean AddLexeme(String pattern, int priority, Term terminal)
    {
        Lexeme lex = new Lexeme(pattern,priority,terminal);
        if(lexems.contains(lex))
            return false;
        else
        {
            lexems.add(lex);
            return true;
        }
    }
    public Lexer()
    {
        AddLexeme("^/\\*$",1,Term.COMMENT_START);
        AddLexeme("^\\*/$",1,Term.COMMENT_END);
        AddLexeme("^//$",1,Term.COMMENT);
        AddLexeme("^(int|double|float|char|string|short|long|ulong|uint|bool|void)$",2,Term.VAR_TYPE);
        AddLexeme("^if$",3,Term.IF);
        AddLexeme("^else$",3,Term.ELSE);
        AddLexeme("^for$",4,Term.FOR);
        AddLexeme("^while$",4,Term.WHILE);
        AddLexeme("^return$",5,Term.RETURN);
        AddLexeme("^\\($",6,Term.OPENING_BRACKET);
        AddLexeme("^\\)$",7,Term.CLOSING_BRACKET);
        AddLexeme("^\\{$",8,Term.OPENING_BODY);
        AddLexeme("^\\}$",9,Term.CLOSING_BODY);
        AddLexeme("^,\\*/$",10,Term.COMMA);
        AddLexeme("^;$",11,Term.LINE_END);
        AddLexeme("^(!|--|\\+\\+)$",12,Term.UNARY_OP);
        AddLexeme("^(==|!=|<|>|<=|>=)$",13,Term.CONDITION_OP);
        AddLexeme("^(-|\\+|\\*|\\/)$",14,Term.BINARY_OP);
        AddLexeme("^(=|\\*=|\\+=|-=|\\/=)$",15,Term.ASSIGN_OP);
        AddLexeme("^\\d+\\.\\d+$",16,Term.NATURAL_DIGIT);
        AddLexeme("^(0\\b|[1-9]\\d*)$",17,Term.DIGIT);
        AddLexeme("^(true|false)$",18,Term.BOOLEAN);
        AddLexeme("^[a-zA-Z_]\\w*$",19,Term.NAME);
    }
    public List<Lexeme> SearchMatch(String text)
    {
        List<Lexeme> result = new ArrayList<>();
        for (Lexeme lexeme : lexems) {
            if(lexeme.isMatch(text) == true)
                result.add(lexeme);
        }
        return result;
    }

    public List<Token> GetTokens(String text)
    {
        text += " ";
        List<Token> tokens = new ArrayList<>();
        for(int i = 0; i < text.length();) {
            List<Lexeme> newList = new ArrayList<>();
            List<Lexeme> oldList = new ArrayList<>();

            int j; //while(a > 6)
            for(j = 0; j + i < text.length() && (!newList.isEmpty() || j == 0); j++) {
                oldList = newList;
                newList = SearchMatch(text.substring(i,i + j + 1));
            }
            if(!oldList.isEmpty() && newList.isEmpty()) {
                oldList.sort((left,right)-> Integer.compare(left.GetPriority(),right.GetPriority()));
                tokens.add(new Token(oldList.get(0), text.substring(i, --j + i)));
            }
            i += j;
        }
        return tokens;
    }
}
