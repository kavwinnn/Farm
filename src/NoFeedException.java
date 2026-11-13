/** Thrown when the farmer tries to feed an animal with no animal feed left. */
public class NoFeedException extends FarmException {
    public NoFeedException() {
        super("No animal feed - buy some at the Pets shop");
    }
}
