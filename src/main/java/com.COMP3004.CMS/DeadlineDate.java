package com.COMP3004.CMS;

import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;

public class DeadlineDate {

    @Getter @Setter protected String deadline;
    @Getter @Setter protected int termNum;

//    String pattern = "yyyy-MM-dd";
//    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

    /*
    Fall term = Term 1
    Winter term = Term 2
    Summer term = Term 3
     */
    public DeadlineDate (String deadline, int term) {
        this.deadline = deadline;
        if (term > 3)
            this.termNum = 3;
        else if (term < 1)
            this.termNum = 1;
        else
            this.termNum = term;
    }
}
