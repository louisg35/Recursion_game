package assignment3;

import java.awt.Color;

public class BlobGoal extends Goal{

    public BlobGoal(Color c) {
        super(c);
    }

    @Override
    public int score(Block board) {
        Color[][] flattened_array = board.flatten();
        int[] blobs = new int[flattened_array.length * flattened_array.length];
        int colour = 0;
        for (int i = 0; i < flattened_array.length; i++) {
            for (int j = 0; j < flattened_array.length; j++) {
                if (flattened_array[i][j].equals(this.targetGoal)) {
                    blobs[colour] = undiscoveredBlobSize(i, j, flattened_array, new boolean[flattened_array.length][flattened_array.length]);
                    colour++;
                }
            }
        }

        int maxBlobSize = 0;
        for (int blob : blobs) {
            if (blob > maxBlobSize) {
                maxBlobSize = blob;
            }
        }

        return maxBlobSize;
    }



    @Override
    public String description() {
        return "Create the largest connected blob of " + GameColors.colorToString(targetGoal)
                + " blocks, anywhere within the block";
    }


    public int undiscoveredBlobSize(int i, int j, Color[][] unitCells, boolean[][] visited) {
      if (i<0 || i>=unitCells.length || j<0 || j>=unitCells[i].length || !(unitCells[i][j].equals(this.targetGoal)) || visited[i][j]) {
          return 0;
      }

      visited [i][j] = true;
      int size = 1;

      size += this.undiscoveredBlobSize(i, j-1, unitCells, visited);
      size += this.undiscoveredBlobSize(i, j+1, unitCells, visited);
      size += this.undiscoveredBlobSize(i-1, j, unitCells, visited);
      size += this.undiscoveredBlobSize(i+1, j, unitCells, visited);

      return size;

    }

}