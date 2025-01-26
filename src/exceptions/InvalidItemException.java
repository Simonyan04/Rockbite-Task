package exceptions;

public class InvalidItemException extends Exception{
    public InvalidItemException(){
        super("Invalid Item");
    }
    public InvalidItemException(String message){
        super(message);
    }
}
