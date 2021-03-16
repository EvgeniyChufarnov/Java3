package lesson1;

import java.util.ArrayList;
import java.util.List;

public class Box<T extends Fruit> {
    private static final int DEFAULT_CAPACITY = 10;
    private final ArrayList<T> fruits;
    private final int capacity;

    public Box() {
        this.fruits = new ArrayList<>();
        this.capacity = DEFAULT_CAPACITY;
    }

    public Box(int capacity) {
        this.fruits = new ArrayList<>();
        this.capacity = capacity;
    }

    public float getWeight() {
        return fruits.stream().map(Fruit::getWeight).reduce(Float::sum).orElse(0f);
    }

    public boolean compare(Box<?> otherBox) {
        return Math.abs(this.getWeight() - otherBox.getWeight()) < 0.0001;
    }

    public void add(T fruit) {
        if (this.getSize() == capacity)
            throw new IllegalArgumentException("Max capacity reached");
        fruits.add(fruit);
    }

    public void add(List<T> fruits) {
        if (this.getSize() + fruits.size() > capacity)
            throw new IllegalArgumentException(String.format("Trying to put %d fruits in the box with capacity of %d", this.getSize() + fruits.size(), capacity));

        this.fruits.addAll(fruits);
    }

    public void remove(T fruit) {
        fruits.remove(fruit);
    }

    public void remove(List<T> fruits) {
        this.fruits.removeAll(fruits);
    }

    public void removeAll() {
        this.fruits.clear();
    }

    public void moveInto(Box<T> anotherBox) {
        if (anotherBox.getSize() + this.getSize() <= anotherBox.getCapacity()) {
            anotherBox.add(fruits);
            this.removeAll();
        } else {
            List<T> partToMove = fruits.subList(0, anotherBox.getCapacity() - anotherBox.getSize());
            anotherBox.add(partToMove);
            this.remove(partToMove);
        }
    }

    public int getCapacity() {
        return capacity;
    }

    public int getSize() {
        return fruits.size();
    }
}
