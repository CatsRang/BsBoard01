package com.bs.bsboard.common;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ResultVO<T> {
    @JsonProperty("code")
    int errorCode = 200000;

    @JsonProperty("msg")
    String msg = "";

    @JsonProperty("dataLength")
    long numRows = 0;

    @JsonProperty("data")
    List<T> resultRows;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getNumRows() {
        return numRows;
    }

    public void setNumRows(long numRows) {
        this.numRows = numRows;
    }

    public List<T> getResultRows() {
        return resultRows;
    }

    public void setResultRows(List<T> resultRows) {
        this.resultRows = resultRows;
    }
}
