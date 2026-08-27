package com.stayride.booking.coding;


import java.util.ArrayDeque;
import java.util.Queue;

public class Problem1 {

    public static int solution(int[][] A) {
        int n = A.length;
        int m = A[0].length;

        boolean[][] visited = new boolean[n][m];

        int countries = 0;

        // Up, Down, Left, Right
        int[] rowDirection = {-1, 1, 0, 0};
        int[] colDirection = {0, 0, -1, 1};

        for (int row = 0; row < n; row++) {
            for (int col = 0; col < m; col++) {

                // Already belongs to a country we have explored
                if (visited[row][col]) {
                    continue;
                }

                // We found a new country
                countries++;

                int color = A[row][col];

                Queue<int[]> queue = new ArrayDeque<>();

                queue.offer(new int[]{row, col});
                visited[row][col] = true;

                while (!queue.isEmpty()) {

                    int[] current = queue.poll();

                    int currentRow = current[0];
                    int currentCol = current[1];

                    // Check four neighboring cells
                    for (int direction = 0; direction < 4; direction++) {

                        int nextRow = currentRow + rowDirection[direction];
                        int nextCol = currentCol + colDirection[direction];

                        // Check boundaries
                        if (nextRow < 0 || nextRow >= n ||
                                nextCol < 0 || nextCol >= m) {
                            continue;
                        }

                        // Already visited
                        if (visited[nextRow][nextCol]) {
                            continue;
                        }

                        // Different color -> different country
                        if (A[nextRow][nextCol] != color) {
                            continue;
                        }

                        visited[nextRow][nextCol] = true;
                        queue.offer(new int[]{nextRow, nextCol});
                    }
                }
            }
        }

        return countries;
    }

    public static void main(String[] args) {

        int[][] A = {
                {5, 4, 4},
                {4, 3, 4},
                {3, 2, 2},
                {2, 3, 2},
                {3, 3, 2},
                {1, 4, 4},
                {4, 1, 1}
        };

        int result = solution(A);

        System.out.println("Number of countries: " + result);
    }
}
