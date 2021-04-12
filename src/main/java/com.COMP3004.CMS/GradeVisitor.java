package com.COMP3004.CMS;

public class GradeVisitor {
    public static String visit(String grade) {
        int gradeVal = Integer.parseInt(grade);
        if (gradeVal >= 50 && gradeVal < 53) {
            return "D-";
        } else if (gradeVal >= 53 && gradeVal < 57) {
            return "D";
        } else if (gradeVal >= 57 && gradeVal < 60) {
            return "D+";
        } else if (gradeVal >= 60 && gradeVal < 63) {
            return "C-";
        } else if (gradeVal >= 63 && gradeVal < 67) {
            return "C";
        } else if (gradeVal >= 67 && gradeVal < 70) {
            return "C+";
        } else if (gradeVal >= 70 && gradeVal < 73) {
            return "B-";
        } else if (gradeVal >= 73 && gradeVal < 77) {
            return "B";
        } else if (gradeVal >= 77 && gradeVal < 80) {
            return "B+";
        } else if (gradeVal >= 80 && gradeVal < 85) {
            return "A-";
        } else if (gradeVal >= 85 && gradeVal < 89) {
            return "A";
        } else if (gradeVal >= 90) {
            return "A+";
        } else {
            return "F";
        }
    }
}
