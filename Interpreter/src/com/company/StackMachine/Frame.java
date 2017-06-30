package com.company.StackMachine;

class Frame
{
    int pointer;
    final int[] localVariable;

    boolean flag_equality;
    boolean flag_larger;
    boolean flag_less;

    Frame(int pointer, int stackSize)
    {
        this.pointer = pointer;
        localVariable = new int[stackSize];
    }
}