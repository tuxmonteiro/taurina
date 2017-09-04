package com.globo.ateam.taurina.util;

public class Result {
    private final long id;
    private final byte[] result;

    public Result(long id, byte[] result) {
        this.id = id;
        this.result = result;
    }

    public long getId() {
        return id;
    }

    public byte[] getResult() {
        return result;
    }
}
