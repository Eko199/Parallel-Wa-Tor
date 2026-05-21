void main() {
    World world = new World(100, 50);

    for (int i = 0; i < 10000; ++i) {
        world.nextChronon();
    }

    System.out.println(world);
}
