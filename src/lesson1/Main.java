package lesson1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        //1. Написать метод, который меняет два элемента массива местами (массив может быть любого ссылочного типа);

        Integer[] intArray = {5, 3, 7};
        swapElements(intArray, 0, 1);
        System.out.println(Arrays.toString(intArray));

        //2. Написать метод, который преобразует массив в ArrayList

        ArrayList<Integer> intArrayList = arrayToArrayList(intArray);
        System.out.println(intArrayList);

        //3. Большая задача:
        //Есть классы Fruit -> Apple, Orange (больше фруктов не надо);
        //Класс Box, в который можно складывать фрукты. Коробки условно сортируются по типу фрукта,
        // поэтому в одну коробку нельзя сложить и яблоки, и апельсины;
        //Для хранения фруктов внутри коробки можно использовать ArrayList;
        //Сделать метод getWeight(), который высчитывает вес коробки,
        // зная количество фруктов и вес одного фрукта (вес яблока – 1.0f, апельсина – 1.5f. Не важно, в каких это единицах);
        //Внутри класса Коробка сделать метод compare, который позволяет сравнить текущую коробку с той,
        // которую подадут в compare в качестве параметра, true – если она равны по весу,
        // false – в противном случае (коробки с яблоками мы можем сравнивать с коробками с апельсинами);
        //Написать метод, который позволяет пересыпать фрукты из текущей коробки в другую
        // (помним про сортировку фруктов: нельзя яблоки высыпать в коробку с апельсинами).
        // Соответственно, в текущей коробке фруктов не остается, а в другую перекидываются объекты,
        // которые были в этой коробке;
        //Не забываем про метод добавления фрукта в коробку.

        Box<Apple> box1 = new Box<>();
        Box<Orange> box2 = new Box<>();
        Box<Apple> box3 = new Box<>(15);

        for (int i = 0; i < 10; i++) {
            box1.add(new Apple());
            box2.add(new Orange());
            box3.add(new Apple());
        }

        System.out.println("Box1 weights: " + box1.getWeight());
        System.out.println("Box2 weights: " + box2.getWeight());
        System.out.println("Box3 weights: " + box3.getWeight());
        System.out.println("Content of box1 weights as box2: " + box1.compare(box2));
        System.out.println("Content of box1 weights as box3: " + box1.compare(box3));

        box1.moveInto(box3);

        System.out.println("Size of box1 after moving: " + box1.getSize());
        System.out.println("Size of box3 after moving: " + box3.getSize());
    }

    public static <T> void swapElements(T[] array, int first, int second) {
        if (first == second)
            return;

        T temp = array[first];
        array[first] = array[second];
        array[second] = temp;
    }

    public static <T> ArrayList<T> arrayToArrayList(T[] array) {
        return Arrays.stream(array).collect(Collectors.toCollection(ArrayList::new));
    }
}
