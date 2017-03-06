package com.company;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        Lexer lexer = new Lexer("text.txt");
        lexer.Parse();
        lexer.ShowText();
        lexer.ShowMatches();
    }
}
