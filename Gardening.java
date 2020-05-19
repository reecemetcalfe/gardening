import java.io.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

// TODO - optimize by not checking top or bottom row of garden

public class Gardening {
    private static final char EMPTY = ' ';

    //Stores garden layout read in from file
    private char[][] garden;
    //Stores plants which need to be planted
    private Queue<Plant> plants;

    //Initialise Garden and Plants
    private void init(String[] args) throws Exception {
        FileReader gardenFile;
        FileReader plantsFile;
        if (args.length > 0) {
            gardenFile = new FileReader(args[0]);
            System.out.println("Initialising Garden: " + args[0]);
            initGarden(gardenFile);
        }
        if (args.length > 1) {
            plantsFile = new FileReader(args[1]);
            System.out.println("Initialising Plants: " + args[1]);
            initPlants(plantsFile);
        }

        if (garden == null) throw new Exception("Garden not initialized");
        if (plants == null) throw new Exception("Plants not initialized");
    }

    //Reads garden info from file and stores in garden object
    private void initGarden(FileReader gardenFile) throws Exception {

        BufferedReader br = new BufferedReader(gardenFile);
        String str;
        ArrayList<char[]> lines = new ArrayList<>();

        try {
            //Read first line to get width of garden
            str = br.readLine();
            if (str == null) throw new Exception("First line is null");
            char[] firstLine = str.toCharArray();
            int width = firstLine.length;
            lines.add(firstLine);

            //Read other lines and pad if needed
            while ((str = br.readLine()) != null) {
                String padded = String.format("%1$-"+width+"s", str);
                lines.add(padded.toCharArray());
            }
        } catch (Exception e) {
            throw new Exception("Error initialising Garden: " + e.getMessage());
        }
        garden = lines.toArray(new char[0][0]);
    }

    //Reads plant info from file and stores in plants object.
    private void initPlants(FileReader plantsFile) throws Exception {

        BufferedReader br = new BufferedReader(plantsFile);
        plants = new ArrayDeque<>();
        String str;
        try {
            while ((str = br.readLine()) != null) {
                String[] spl = str.split(",");
                if (spl.length != 3) {
                    throw new Exception("Plant definition format incorrect, expecting <name>,<qty>,<distance>: " + str);
                }
                //Create number of plants of this type required
                int plantQty = Integer.valueOf(spl[1]);
                for (int i=0; i<plantQty; ++i)
                    plants.add(new Plant(spl[0], Integer.valueOf(spl[2])));
            }
        } catch (Exception e) {
            throw new Exception("Error initialising Plant: " + e.getMessage());
        }
    }

    //Print garden
    private void display() {
        System.out.println("---------------------");
        for (char[] row : garden) {
            for (char space : row) {
                System.out.print(space);
            }
            System.out.print('\n');
        }
        System.out.println("---------------------");
    }

    //Checks if plant is too near others of the same name
    private boolean canPlant(Plant plant, int x, int y) {
        int minDistance = plant.minDistance;

        for (int i=0; i<garden.length; ++i) {
            for (int j=0; j<garden[i].length; ++j) {
                if (garden[i][j] == plant.name) {
                    //Found same plant name - check if manhattan distance within range
                    if ((Math.abs(i - x) + Math.abs(j - y)) < minDistance)
                        return false;
                }
            }
        }
        return true;
    }

    //Finds a suitable space to plant and recurses
    private boolean solve(Queue<Plant> plants) {
        if (plants.isEmpty()) return true;

        //Try pot this plant by looking for an empty space
        Plant plant = plants.remove();
        for (int i = 0; i < garden.length; ++i) {
            for (int j = 0; j < garden[i].length; ++j) {
                if (garden[i][j] == EMPTY) {
                    //Found a planting location
                    if (canPlant(plant, i, j)) {
                        //Fits minimum distance requirements
                        garden[i][j] = plant.name;

                        //Start backtracking recursively with other plants
                        if (solve(plants)) {
                            return true;
                        } else {
                            //Not a solution so reset cell and continue
                            garden[i][j] = EMPTY;
                        }
                    }
                }
            }
        }
        return false;
    }

    public class Plant {
        char name;
        int minDistance;

        Plant(String n, int d) {
            name = n.charAt(0);
            minDistance = d;
        }
    }

    public static void main(String[] args) {
        try {
            Gardening gardening = new Gardening();
            gardening.init(args);
            gardening.display();

            if (gardening.solve(gardening.plants)) {
                System.out.println("Solved");
                gardening.display();
            } else {
                System.out.println("Couldn't solve");
            }
        } catch (Exception e) {
            System.out.println("Error encountered: " + e.getMessage());
        }
    }
}
