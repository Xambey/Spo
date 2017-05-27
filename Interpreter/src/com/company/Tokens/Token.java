package com.company.Tokens;

import com.company.Vocabulary.Lexeme;

public class Token {
    private final Lexeme lexeme;
    private final String value;

    public Token(Lexeme lexeme, String match)
    {
        this.lexeme = lexeme;
        value = match;
    }
    public Lexeme GetLexeme(){ return lexeme; }
    public String GetMatch(){ return value; }
    @Override
    public String toString() { return String.format("Match: %s Lexeme: %s }",value,lexeme.toString()); }
}
