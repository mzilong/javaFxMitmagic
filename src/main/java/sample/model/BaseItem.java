package sample.model;

public class BaseItem<T> {
    public int id;
    public String name;
    public T value;
    public BaseItem() {}

    public BaseItem(int id, String name, T value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }
    public BaseItem(String name, T value) {
        this.name = name;
        this.value = value;
    }
}
