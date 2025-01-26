package exceptions;

public class InsufficientItemsException extends Exception{
    public InsufficientItemsException(){
        super("Insufficient Items");
    }
    public InsufficientItemsException(String message){
        super(message);
    }
}
