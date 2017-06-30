package com.company.ProgramThree;

public class RootNode extends Node{
    public RootNode(){ }
    public RootNode(int level) { this.level = level; }
    public MethodNode GetMainMethod(){
        return (MethodNode) children.stream().filter(p -> p.getClass().equals(MethodNode.class)).findFirst().get();
    }
}
