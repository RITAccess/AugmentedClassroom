package edu.rit.simulatingclassroom.exceptions;

/**
 * Created by Stefan on 2/19/2015.
 */
public class IncorrectOrderException extends RuntimeException{
    public IncorrectOrderException(String detailMessage) {
        super(detailMessage);
    }
}
