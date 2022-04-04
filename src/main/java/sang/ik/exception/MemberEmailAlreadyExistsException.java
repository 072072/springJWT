package sang.ik.exception;

public class MemberEmailAlreadyExistsException extends RuntimeException{

    public MemberEmailAlreadyExistsException(String message){
        super(message);
    }
}
