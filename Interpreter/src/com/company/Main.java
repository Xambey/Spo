package com.company;

import com.company.Tokens.Token;
import com.company.Vocabulary.Lexer;
import com.sun.deploy.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        String filename = "text.txt";
        Path path = Paths.get(filename);
        if(Files.notExists(path)) {
            System.out.printf("File %s not exist!", filename);
            return;
        }
        String list = StringUtils.join(Files.readAllLines(Paths.get("text.txt")), ", ");

        Lexer lexer = new Lexer();
        List<Token> tokens = lexer.GetTokens(list);

        System.out.println("Tokens list: ");
        for(Token item : tokens)
            System.out.println(item.toString());
    }
}
