package com.company;
import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        Lexer lexer = new Lexer();
        List<Token> tokens = lexer.Parse("text.txt");
        lexer.ShowMatches();
    }
}
