package com.company.StackMachine;
import java.util.*;

public class Core
{
    private List<Integer> program;
    private Stack<Frame> frameStack;
    private Stack<Integer> stack;
    private StringBuilder output;
    public boolean enableOut;

    public Core()
    {
        enableOut = true;
    }

    public String decompile(List<Integer> program)
    {

        StringBuilder sb = new StringBuilder();
        int pointer = 0;
        while(pointer < program.size())
        {
            sb.append(String.format("%03d: ", pointer));

            if(Opcode.values().length <= program.get(pointer))
            {
                sb.append(program.get(pointer++));
            }
            else
            {
                Opcode opcod = Opcode.values()[program.get(pointer++)];
                sb.append(opcod.toString()).append('\t');

                for(int i = 1; i < Opcode.getSize(opcod) && pointer < program.size(); i++)
                    sb.append("\t").append(program.get(pointer++).toString());
            }

            sb.append("\n");
        }

        return sb.toString();
    }

    public String run(List<Integer> program)
    {
        stack = new Stack<>();
        output = new StringBuilder();

        frameStack = new Stack<>();
        frameStack.push(new Frame(1, program.get(0)));

        this.program = program;

        while(!frameStack.isEmpty() && getPointer() >= 0 && getPointer() < program.size())
            step();

        return output.toString();
    }

    private void step()
    {
        int destination, value, source;
        switch(readOpcode())
        {
            case nop:
                break;

            case ret:
                frameStack.pop();
                break;
            case callc:
                destination = readInt();
                frameStack.push(new Frame(destination + 1, program.get(destination)));
                break;
            case calls:
                destination = stack.pop();
                frameStack.push(new Frame(destination + 1, program.get(destination)));
                break;
            case jmp:
                frameStack.lastElement().pointer = readInt();
                break;

            case jne:
                destination = readInt();
                if(!getFrame().flag_equality)
                {
                    frameStack.lastElement().pointer = destination;
                }
                break;
            case jeq:
                destination = readInt();
                if(getFrame().flag_equality)
                {
                    frameStack.lastElement().pointer = destination;
                }
                break;
            case jls:
                destination = readInt();
                if(getFrame().flag_less)
                {
                    frameStack.lastElement().pointer = destination;
                }
                break;
            case jle:
                destination = readInt();
                if(getFrame().flag_equality || getFrame().flag_less)
                {
                    frameStack.lastElement().pointer = destination;
                }
                break;
            case jgr:
                destination = readInt();
                if(getFrame().flag_larger)
                {
                    frameStack.lastElement().pointer = destination;
                }
                break;
            case jge:
                destination = readInt();
                if(getFrame().flag_equality || getFrame().flag_larger)
                {
                    frameStack.lastElement().pointer = destination;
                }
                break;

            case callprint:
                log(stack.pop().toString());
                break;
            case callread:
                stack.push(new Scanner(System.in).nextInt());
                break;

            case pushc:
                value = readInt();
                stack.push(value);
                break;
            case pushm:
                source = readInt();
                stack.push(getFrame().localVariable[source]);
                break;
            case pushc2:
                value = readInt();
                stack.push(value);
                value = readInt();
                stack.push(value);
                break;
            case pushm2:
                source = readInt();
                stack.push(getFrame().localVariable[source]);
                source = readInt();
                stack.push(getFrame().localVariable[source]);
                break;
            case pop:
                destination = readInt();
                getFrame().localVariable[destination] = stack.pop();
                break;
            case copys:
                destination = readInt();
                getFrame().localVariable[destination] = stack.lastElement();
                break;

            case comps:
                value = stack.pop();
                value = stack.pop() - value;

                getFrame().flag_equality = value == 0;
                getFrame().flag_larger = value > 0;
                getFrame().flag_less = value < 0;
                break;

            case addi:
                stack.push(stack.pop() + stack.pop());
                break;
            case subi:
                stack.push(-stack.pop() + stack.pop());
                break;
            case muli:
                stack.push(stack.pop() * stack.pop());
                break;
            case divi:
                value = stack.pop();
                stack.push(stack.pop() / value);
                break;

            case inc:
                source = readInt();
                getFrame().localVariable[source]++;
                break;
            case dec:
                source = readInt();
                getFrame().localVariable[source]--;
                break;

            default:
                break;
        }
    }

    private int readInt()
    {
        getFrame().pointer++;
        return program.get(getPointer() - 1);
    }

    private Opcode readOpcode()
    {
        getFrame().pointer++;
        return Opcode.values()[program.get(getPointer() - 1)];
    }

    private void log(String message)
    {
        if(enableOut)
            System.out.printf(">> %s\n", message);

        output.append(message).append('\n');
    }

    private Frame getFrame()
    {
        return frameStack.lastElement();
    }

    private int getPointer()
    {
        return getFrame().pointer;
    }
}