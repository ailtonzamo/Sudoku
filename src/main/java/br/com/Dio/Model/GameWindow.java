package br.com.Dio.Model;

import br.com.Dio.Model.Board;
import br.com.Dio.Model.BoardGenerator;
import br.com.Dio.Model.Space;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameWindow extends JFrame {
    private final Board board;
    private final JTextField[][] fields = new JTextField[9][9];

    public GameWindow(Board board) {
        this.board = board;
        setTitle("Sudoku - Game Window");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel boardPanel = new JPanel(new GridLayout(9, 9));
        boardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initializeBoard(boardPanel);
        add(boardPanel, BorderLayout.CENTER);
        createButtons();
    }

    private void initializeBoard(JPanel boardPanel) {
        List<List<Space>> spaces = board.getSpaces();

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                fields[i][j] = new JTextField();
                fields[i][j].setHorizontalAlignment(JTextField.CENTER);
                fields[i][j].setFont(new Font("Arial", Font.BOLD, 20));

                Space space = spaces.get(i).get(j);
                if (space.isFixed()) {
                    fields[i][j].setText(String.valueOf(space.getExpected()));
                    fields[i][j].setEditable(false);
                    fields[i][j].setBackground(Color.LIGHT_GRAY);
                } else {
                    fields[i][j].setBackground(Color.WHITE);
                }

                boardPanel.add(fields[i][j]);
            }
        }
    }

    private void createButtons() {
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton validateButton = new JButton("Validar");
        validateButton.addActionListener(e -> validateBoard());

        JButton resetButton = new JButton("Resetar");
        resetButton.addActionListener(e -> resetBoard());

        JButton checkCompleteButton = new JButton("Verificar Completo");
        checkCompleteButton.addActionListener(e -> checkCompletion());

        JButton solveButton = new JButton("Resolver");
        solveButton.addActionListener(e -> autoSolve());

        buttonPanel.add(validateButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(checkCompleteButton);
        buttonPanel.add(solveButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void validateBoard() {
        if (board.isValid()) {
            JOptionPane.showMessageDialog(this, "O Sudoku está válido!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Erro! O Sudoku contém números repetidos.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetBoard() {
        board.reset();
        updateBoard();
    }

    private void checkCompletion() {
        if (board.isComplete()) {
            JOptionPane.showMessageDialog(this, "Parabéns! O Sudoku está completo e correto!", "Completo", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "O Sudoku ainda está incompleto ou tem erros!", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private boolean solveSudoku() {
        List<List<Space>> spaces = board.getSpaces();

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                Space space = spaces.get(row).get(col);
                if (space.getActual() == null || space.getActual() == 0) {
                    for (int num = 1; num <= 9; num++) {
                        if (board.isValidMove(row, col, num)) {
                            space.setActual(num);
                            fields[row][col].setText(String.valueOf(num));

                            if (solveSudoku()) { // Recursão para continuar preenchendo
                                return true;
                            }

                            // Se não for válido, desfaz e tenta outro número
                            space.setActual(null);
                            fields[row][col].setText("");
                        }
                    }
                    return false; // Nenhum número válido encontrado, volta na recursão
                }
            }
        }
        return true; // Sudoku resolvido!
    }

    private void autoSolve() {
        if (solveSudoku()) {
            JOptionPane.showMessageDialog(this, "Sudoku resolvido com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Não foi possível resolver o Sudoku!", "Erro", JOptionPane.ERROR_MESSAGE);
        }

        updateBoard();
    }


    private int findValidNumber(int row, int col) {
        for (int num = 1; num <= 9; num++) {
            if (board.isValidMove(row, col, num)) {
                return num; // Retorna um número válido que pode ser inserido
            }
        }
        return 0; // Retorna 0 se nenhum número válido for encontrado
    }

    private void updateBoard() {
        List<List<Space>> spaces = board.getSpaces();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                Space space = spaces.get(i).get(j);
                if (!space.isFixed()) {
                    fields[i][j].setText(space.getActual() == null ? "" : String.valueOf(space.getActual()));
                }
            }
        }
    }

    public static void main(String[] args) {
        List<List<Space>> spaces = BoardGenerator.generateRandomBoard();
        Board board = new Board(spaces);
        SwingUtilities.invokeLater(() -> new GameWindow(board).setVisible(true));
    }
}
