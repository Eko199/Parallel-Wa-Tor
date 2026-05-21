public class Entity {
    private EntityType type;
    private int age = 0;
    private int energy = 0;
    private int lastChronon = 0;

    public Entity(EntityType type) {
        this.type = type;

        if (type == EntityType.SHARK) {
            this.energy = 5; // Give sharks some starting energy
        }
    }

    public EntityType getType() {
        return type;
    }

    public void setType(EntityType type) {
        this.type = type;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public int getLastChronon() {
        return lastChronon;
    }

    public void setLastChronon(int lastChronon) {
        this.lastChronon = lastChronon;
    }

    public enum EntityType {
        EMPTY,
        FISH,
        SHARK
    }
}