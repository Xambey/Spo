package com.company.Vocabulary;
import com.company.Enums.Term;

import java.util.regex.*;

public class Lexeme {
    private final int priority;
    private final Term terminal;
    private final Pattern pattern;

    public int GetPriority(){ return priority; }
    public Term GetTerminal(){ return terminal; }
    public Pattern GetPattern(){ return pattern; }
    public boolean isMatch(String text)
    {
        return pattern.matcher(text).find();
    }
    public Lexeme(String pattern, int priority, Term terminal)
    {
        this.pattern = Pattern.compile(pattern);
        this.priority = priority;
        this.terminal = terminal;
    }

    @Override
    public String toString()
    {
        return terminal.toString();
    }
}
