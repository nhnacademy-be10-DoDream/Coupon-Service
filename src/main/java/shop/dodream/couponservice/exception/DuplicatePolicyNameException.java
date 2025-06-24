package shop.dodream.couponservice.exception;

public class DuplicatePolicyNameException extends RuntimeException {
    public DuplicatePolicyNameException(String name) {
        super("Duplicate policy name: " + name);
    }
}
