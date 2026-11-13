/** Thrown when a pen already holds as many animals as it can. */
public class PenFullException extends FarmException {
    public PenFullException(String kind, int count, int max) {
        super("The " + kind.toLowerCase() + " pen is full (" + count + "/" + max + ")");
    }
}
