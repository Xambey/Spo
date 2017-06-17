package com.company.ProgramThree;

import java.util.List;

public abstract class Node {
    protected List<Node> children;
    protected int level;

    public Node(){ this.level = 0; }
    public Node(int level) { this.level = level; }

    private StringBuilder TextGenerator(int level)
    {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < level;i++) sb.append("\t");
        sb.append(getClass().getSimpleName());
        sb.append(System.lineSeparator());
        return sb;
    }

    protected String toString(int level)
    {
        return TextGenerator(level).toString();
    }
}
