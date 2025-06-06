package br.com.Dio.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BoardGenerator {
    public static List<List<Space>> generateRandomBoard() {
        List<List<Space>> spaces = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < 9; i++) {
            List<Space> row = new ArrayList<>();
            for (int j = 0; j < 9; j++) {
                boolean isFixed = random.nextDouble() < 0.3; // 30% de chance de ser fixo
                int value = isFixed ? random.nextInt(9) + 1 : 0;
                row.add(new Space(value, isFixed));
            }
            spaces.add(row);
        }
        return spaces;
    }
}
