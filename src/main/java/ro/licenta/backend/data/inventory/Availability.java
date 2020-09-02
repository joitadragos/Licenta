package ro.licenta.backend.data.inventory;

public enum Availability {
    FORSALE("For Sale"), KEEPING("Keeping"), SOLD("Sold");

    private final String name;

    private Availability(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
