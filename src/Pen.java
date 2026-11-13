import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** A pen for one kind of animal. Each animal stands on one of the pen's fixed slots. */
public class Pen {
    public static final int MAX = 3;

    private final String kind;
    private final int[][] slots;   // {col, row} of each place an animal can stand
    private final List<Animal> animals = new ArrayList<>();

    public Pen(String kind, int[][] slots) {
        if (slots.length != MAX) throw new IllegalArgumentException("a pen needs " + MAX + " slots");
        this.kind = kind;
        this.slots = slots;
    }

    /** Puts a new animal on the next free slot. */
    public Animal buy() throws PenFullException {
        if (isFull()) throw new PenFullException(kind, size(), MAX);
        for (int slot = 0; slot < MAX; slot++) {
            if (animalInSlot(slot) == null) return addAt(slot);
        }
        throw new PenFullException(kind, size(), MAX);   // not reached: a pen that isn't full has a free slot
    }

    /** Puts back an animal read from a save file. */
    public void restoreAt(int slot, int love, int fedDays, boolean ready) {
        if (slot < 0 || slot >= MAX) throw new IllegalArgumentException("no slot " + slot + " in the " + kind + " pen");
        if (animalInSlot(slot) != null) throw new IllegalArgumentException("slot " + slot + " of the " + kind + " pen is taken");
        Animal animal = AnimalFactory.create(kind, slots[slot][0], slots[slot][1]);
        animal.restore(love, fedDays, ready);   // throws before the animal is added if the numbers are wrong
        animals.add(animal);
    }

    private Animal addAt(int slot) {
        Animal animal = AnimalFactory.create(kind, slots[slot][0], slots[slot][1]);
        animals.add(animal);
        return animal;
    }

    private Animal animalInSlot(int slot) {
        return animalAt(slots[slot][0], slots[slot][1]);
    }

    /** @return which slot (0, 1 or 2) the animal stands on, or -1 if it is not in this pen */
    public int slotOf(Animal animal) {
        for (int slot = 0; slot < MAX; slot++) {
            if (animalInSlot(slot) == animal) return slot;
        }
        return -1;
    }

    /** @return the animal standing on (col, row), or null */
    public Animal animalAt(int col, int row) {
        for (Animal animal : animals) {
            if (animal.getCol() == col && animal.getRow() == row) return animal;
        }
        return null;
    }

    public String getKind() { return kind; }
    public int size() { return animals.size(); }
    public boolean isFull() { return animals.size() >= MAX; }
    public List<Animal> getAnimals() { return Collections.unmodifiableList(animals); }

    /** Overnight for every animal. @return news such as "Chicken 1 was hungry (love 2 -> 1)" */
    public List<String> newDay() {
        List<String> news = new ArrayList<>();
        for (Animal animal : animals) {
            String text = animal.newDay();
            if (text != null) news.add(kind + " " + (slotOf(animal) + 1) + " " + text);
        }
        return news;
    }
}
