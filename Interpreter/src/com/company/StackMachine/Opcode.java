package com.company.StackMachine;

import java.util.HashMap;
import java.util.Map;

public enum Opcode
{
    nop,

    ret,
    callc,
    calls,
    jmp,

    jne,
    jeq,
    jls,
    jle,
    jgr,
    jge,

    callprint,
    callread,

    pushm,
    pushc,
    pushm2,
    pushc2,
    pop,
    copys,

    comps,

    addi,
    subi,
    muli,
    divi,

    inc,
    dec;

    private static Map<Opcode, Integer> sizeMap;

    public static Integer getSize(Opcode opcode)
    {
        if(sizeMap == null)
        {
            sizeMap = new HashMap<>();
            sizeMap.put(Opcode.nop, 1);
            sizeMap.put(Opcode.ret, 1);
            sizeMap.put(Opcode.callc, 2);
            sizeMap.put(Opcode.calls, 1);
            sizeMap.put(Opcode.jmp, 2);

            sizeMap.put(Opcode.jne, 2);
            sizeMap.put(Opcode.jeq, 2);
            sizeMap.put(Opcode.jls, 2);
            sizeMap.put(Opcode.jle, 2);
            sizeMap.put(Opcode.jgr, 2);
            sizeMap.put(Opcode.jge, 2);

            sizeMap.put(Opcode.callprint, 1);
            sizeMap.put(Opcode.callread, 1);

            sizeMap.put(Opcode.pushm, 2);
            sizeMap.put(Opcode.pushc, 2);
            sizeMap.put(Opcode.pushm2, 3);
            sizeMap.put(Opcode.pushc2, 3);
            sizeMap.put(Opcode.pop, 2);
            sizeMap.put(Opcode.copys, 2);

            sizeMap.put(Opcode.comps, 1);

            sizeMap.put(Opcode.addi, 1);
            sizeMap.put(Opcode.subi, 1);
            sizeMap.put(Opcode.muli, 1);
            sizeMap.put(Opcode.divi, 1);

            sizeMap.put(Opcode.inc, 2);
            sizeMap.put(Opcode.dec, 2);
        }

        return sizeMap.get(opcode);
    }
}