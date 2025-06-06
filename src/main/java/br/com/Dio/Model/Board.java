package br.com.Dio.Model;

import java.util.Collection;
import java.util.List;
import java.util.Random;

import static br.com.Dio.Model.GameStatusEnum.*;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class Board {
    private final List<List<Space>>spaces;
    public Board(final List<List<Space>>spaces){
        this.spaces=spaces;
    }

    public List<List<Space>>getSpaces(){
        return spaces;
    }

    public GameStatusEnum getStatus(){
        if(spaces.stream().flatMap(Collection::stream).noneMatch(s->!s.isFixed() && nonNull(s.getActual()))){
        return NON_STARTED;
    }
  return spaces.stream().flatMap(Collection::stream).anyMatch(s->isNull(s.getActual()))? INCOMPLETE:COMPLETE;
    }

    boolean isValidMove(int row, int col, int num) {
        // Verifica se o número já está na mesma linha ou coluna
        for (int i = 0; i < 9; i++) {
            if (spaces.get(row).get(i).getActual() != null && spaces.get(row).get(i).getActual() == num) {
                return false; // Número já existe na linha
            }
            if (spaces.get(i).get(col).getActual() != null && spaces.get(i).get(col).getActual() == num) {
                return false; // Número já existe na coluna
            }
        }

        // Verifica se o número já está no mesmo bloco 3x3
        int boxRow = (row / 3) * 3;
        int boxCol = (col / 3) * 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (spaces.get(boxRow + i).get(boxCol + j).getActual() != null &&
                        spaces.get(boxRow + i).get(boxCol + j).getActual() == num) {
                    return false; // Número já existe no bloco 3x3
                }
            }
        }

        return true; // O movimento é válido
    }



    public boolean hasErrors(){
        if(getStatus()==NON_STARTED){
            return false;
        }
        return spaces.stream().flatMap(Collection::stream)
                .anyMatch(s->nonNull(s.getActual())&&!s.getActual().equals(s.getExpected()));
    }

    public boolean changeValue(final int col, final int row, final Integer value) {
        var space = spaces.get(col).get(row);
        if (space.isFixed()) {
            return true;
        }
        space.setActual(value);
        return true;
    }
    public boolean clearValue(final int col, final int row){
        var space=spaces.get(col).get(row);
        if(space.isFixed()){
        return false;
    }
    space.clearSpace();
    return false;
    }
    public void reset(){
        spaces.forEach(c->c.forEach(Space::clearSpace));
    }
    public boolean gameIsFinished(){
        return !hasErrors()&&getStatus().equals(COMPLETE);
    }

    public boolean isValid() {
        return isRowsValid() && isColsValid() && isSubGridsValid();
    }

    private boolean isRowsValid() {
        for (int i = 0; i < 9; i++) {
            boolean[] seen = new boolean[10]; // Array para rastrear números vistos
            for (int j = 0; j < 9; j++) {
                Integer num = spaces.get(i).get(j).getActual();
                if (num != null && num != 0) {
                    if (seen[num]) return false; // Número repetido encontrado na linha
                    seen[num] = true; // Marca o número como visto
                }
            }
        }
        return true;
    }

    private boolean isColsValid() {
        for (int j = 0; j < 9; j++) {
            boolean[] seen = new boolean[10]; // Array para rastrear números vistos
            for (int i = 0; i < 9; i++) {
                Integer num = spaces.get(i).get(j).getActual();
                if (num != null && num != 0) {
                    if (seen[num]) return false; // Número repetido encontrado na coluna
                    seen[num] = true;
                }
            }
        }
        return true;
    }

    private boolean isSubGridsValid() {
        for (int gridRow = 0; gridRow < 3; gridRow++) {
            for (int gridCol = 0; gridCol < 3; gridCol++) {
                boolean[] seen = new boolean[10];
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        Integer num = spaces.get(gridRow * 3 + i).get(gridCol * 3 + j).getActual();
                        if (num != null && num != 0) {
                            if (seen[num]) return false; // Número repetido encontrado no bloco 3x3
                            seen[num] = true;
                        }
                    }
                }
            }
        }
        return true;
    }



    public boolean isComplete() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                Integer num = spaces.get(i).get(j).getActual();
                if (num == null || num == 0) {
                    return false; // Ainda há espaços vazios
                }
            }
        }
        return isValid(); // Confirma que a solução é válida
    }



}