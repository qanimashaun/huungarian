import java.io.*;
import java.util.*;

public class ProjectMatcher{
  private static int[][] costMatrix; // The matrix between projects and students
  static String[] UID; //Each student
  static String[] names;
  private int rows, cols, dim;
  static int[] rowsIndexCovered;
  static int[] coloumnsIndexCovered;
  static ArrayList<List<Integer>> lists = new ArrayList<>();
  static String[] projectIDs;
  static int n;

  public static void main(String[] args) {
    if(args.length<2){
      System.err.println("<usage>\n\tjava ProjectMatcher <projectFile> <studentFile>");
			System.exit(1);
    }else{
      n = Integer.parseInt(args[0]);
      costMatrix = new int[n][n];
      projectIDs = new String[n];
      UID = new String[n];
      names = new String[n];
      readFile(args[1]);
      readProjects(args[2]);
      printMatrix(costMatrix);
      //subtract rows , then transpose, then subtract coloumns then transpose again
      for(int i = 0; i<n; i++){
        costMatrix[i] = optimizeRow(costMatrix[i]);
      }
      costMatrix = rotate(costMatrix);
      for(int i = 0; i<n; i++){
        costMatrix[i] = optimizeRow(costMatrix[i]);
      }
      costMatrix = rotate(costMatrix);
      System.out.println("");
      System.out.println("");
      System.out.println("");
      printMatrix(costMatrix);
      System.out.println("");
      System.out.println("");
      int[][] coverCostMatrix = new int[n][n];

      for(int i = 0; i < coverCostMatrix.length; i++){
        coverCostMatrix[i] = costMatrix[i].clone();
      }
      int minLines = getMinLines(coverCostMatrix);

      while(minLines!=n){
        System.out.println(" Yaddi ya ");
        reduceMatrix(costMatrix);
        for(int i = 0; i < coverCostMatrix.length; i++){
          coverCostMatrix[i] = costMatrix[i].clone();
        }
        printMatrix(coverCostMatrix);
        minLines = getMinLines(coverCostMatrix);
        System.out.println(minLines);
        System.out.println(Arrays.toString(rowsIndexCovered));
        System.out.println(Arrays.toString(coloumnsIndexCovered));


      }
      printMatrix(costMatrix);

      for(int i=0; i<costMatrix.length; i++){
        System.out.println(getZerosLeftIndexes(costMatrix[i]));
        lists.add(getZerosLeftIndexes(costMatrix[i]));
      }



      designate(lists);
    }
  }

  public static int mustDesignate(ArrayList<List<Integer>> options){
    for(int i = 0; i<options.size();i++){
      if(options.get(i).size() == 1){
        return i;
      }
    }
    return -1;
  }

  public static int multipleDesignate(ArrayList<List<Integer>> options){
    for(int i = 0; i<options.size();i++){
      if(options.get(i).size() > 1){
        return i;
      }
    }
    return -1;
  }

  public static ArrayList<List<Integer>> removeOption(ArrayList<List<Integer>> options, int toRemove){
    for(int i = 0; i<options.size();i++){
      List<Integer> currentList = options.get(i);
      for(int j = 0; j<currentList.size(); j++){
        if(currentList.get(j) == toRemove){
          options.get(i).remove(j);
        }
      }
    }
    return options;
  }

  /*
   * This method will split the up the projects depending on the minimum cost
   */
  public static void designate(ArrayList<List<Integer>> options){
    int assigned = 0;
    int[] designation = new int[options.size()];
    boolean[] designated = new boolean[options.size()];
    while(assigned<options.size()){
      while(mustDesignate(options)!=-1){
        int index = mustDesignate(options);
        int numberToBeDesignated = options.get(index).get(0);
        designated[numberToBeDesignated] = true;
        designation[index] = numberToBeDesignated;
        options.get(index).remove(0);
        options = removeOption(options, numberToBeDesignated);
        assigned++;
        System.out.println(Arrays.toString(designation));
        System.out.println(Arrays.toString(designated));
      }
      if(assigned<options.size()&&
      multipleDesignate(options)!= -1){
        int index = multipleDesignate(options);
        int numberToBeDesignated = options.get(index).get(0);
        designated[numberToBeDesignated] = true;
        designation[index] = numberToBeDesignated;
        options.get(index).clear();
        options = removeOption(options, numberToBeDesignated);
        assigned++;
        System.out.println(Arrays.toString(designation));
        System.out.println(Arrays.toString(designated));
      }

    }

    for(int i = 0; i<options.size(); i++){
      System.out.print(names[i] + "   ->    ");
      System.out.println(projectIDs[designation[i]]);
    }
  }

  /*
   * This method will read and process the project File and procceses it
   */
  public static void readFile(String file){
    int counter=0;
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
      String line;

      while ((line = br.readLine()) != null) {
         // process the line.
         String[] splitted = line.split(" ");
         System.out.println(Arrays.toString(splitted));
         UID[counter] =splitted[0];
         names[counter] = splitted[1] + " " + splitted[2];
         for(int i = 3; i<splitted.length; i++){
           costMatrix[counter][i-3] = Integer.parseInt(splitted[i]);
         }
         counter++;
      }
    }catch(Exception e){
      e.printStackTrace();
    }

    while (counter<n){
      names[counter] = "Fake Student " + counter;
      for(int i = 0; i<n; i++){
        costMatrix[counter][i] = 0;
      }
      counter++;
    }
  }

  /*
   * This method will read and process the project File and procceses it
   */
  public static void readProjects(String file){
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
      String line;
      int counter=0;
      while ((line = br.readLine()) != null) {
         // process the line.
         String[] splitted = line.split(" ");

         projectIDs[counter] =line.substring(splitted[0].length() + 1);
         counter++;
      }
    }catch(Exception e){
      e.printStackTrace();
    }
  }

  /*
   * This method will gets the indexes of the matrix which have zeros for each of the rows
   */
  public static List<Integer> getZerosLeftIndexes(int[] array){
    List<Integer> list = new ArrayList<Integer>();
    for(int i = 0; i<array.length; i++){
      if(array[i]==0){
        list.add(i);
      }
    }
    return list;
  }

  /*
   * This method will revise the matrix by taking the lowest non-zero value of the matrix
   * adds that value to all the values covered twice by a line and takes one for all values not
   * covered by any lines
   */
  public static int[][] reduceMatrix(int[][] matrix){
    int lowest = lowesCost(matrix);
    for(int i = 0; i< rowsIndexCovered.length; i++){
      for(int j = 0; j<coloumnsIndexCovered.length; j++){
        if(rowsIndexCovered[i]==1 && coloumnsIndexCovered[j]==1){
          matrix[i][j]+=lowest;
        }else if(rowsIndexCovered[i]==1){

        }else if(coloumnsIndexCovered[j]==1){

        }else{
          matrix[i][j]-=lowest;
        }
      }
    }
    return matrix;
  }

  /*
   * This method will look for the lowestCost item on the matrix that is not zero
   * and then return it to be be used to revise the matrix to add another line to the matrix
   */
  public static int lowesCost(int[][] matrix){
    int lowest = Integer.MAX_VALUE;
    for(int i = 0; i< rowsIndexCovered.length; i++){
      for(int j = 0; j<coloumnsIndexCovered.length; j++){
        if(rowsIndexCovered[i]==0 && coloumnsIndexCovered[j]==0){
          if(matrix[i][j]<lowest){
            lowest = matrix[i][j];
          }
        }
      }
    }

    System.out.println("The lowest cost is "+ lowest);
    return lowest;
  }

  /*
   * This method will find the minimum lines that need to be assigned to the matrix
   * for all of the zeros to be covered
   */
  public static int getMinLines(int[][] matrix){
    int rowsCovered = 0;
    int coloumnsCovered = 0;
    rowsIndexCovered = new int[matrix.length];
    coloumnsIndexCovered = new int[matrix[0].length];
    for(int i = 0; i< rowsIndexCovered.length; i++){
      for(int j = 0; j<coloumnsIndexCovered.length; j++){
        if(matrix[i][j] == 0){

          int direction = getDirection(matrix, i, j);
          if(direction == 1){
            // row selected
            if(rowsIndexCovered[i] == 0){
              rowsIndexCovered[i] =1;
              coverRow(matrix[i]);
              rowsCovered++;
              System.out.println(i+"  "+j+ "    " + "horizontal");
            }
          }else if (direction== -1){
            if(coloumnsIndexCovered[j] == 0){
              coloumnsIndexCovered[j] =1;
              coverCol(matrix,j);
              coloumnsCovered++;
              System.out.println(i+"  "+j+ "    " + "vertical");
            }
          }
        }
      }
    }
    return rowsCovered+coloumnsCovered;
  }

  /*
   * Helper method to "Cross out" the rows/coloumns of the matrix
   *
   */
  public static void coverRow(int[] array){
    for(int i = 0;i < array.length;i++){
			array[i] +=1;
    }
  }
  public static void coverCol(int[][] matrix, int j){
    for(int i = 0;i < matrix.length;i++){
			matrix[i][j] +=1;
    }
  }

  /*
   * This method will look to see whether there should be a line in the horizontal
   * or vertical direction of the matrix
   *
   */
  public static int getDirection(int[][] matrix, int row, int coloumn){
    int rowCounter =0;
    int coloumnCounter = 0;
    for(int i=0; i< matrix.length; i++){
      if(matrix[i][coloumn]==0){
        coloumnCounter++;
      }
    }
    for(int i =0; i<matrix[row].length; i++){
      if(matrix[row][i]==0){
        rowCounter++;
      }
    }
    if(rowCounter>=coloumnCounter){
      return 1;
    }
    return -1;
  }

  /*
   * This method will optimize the row by taking the lowest value
   * of the matrix off the row of the whole row of the matrix
   */
  public static int[] optimizeRow(int[] row){
    int lowest = checkRow(row);
    if(lowest!=0){
      for(int i = 0; i<row.length; i++){
        row[i] = row[i] - lowest;
      }
    }
    return row;
  }

  /*
   * This method will check the the row of the matrix to find the lowest value
   *
   */
  public static int checkRow(int[] row){
    int low = Integer.MAX_VALUE;
    for(int i : row){
      if(i<low){
        low=i;
      }
    }
    return low;
  }

  /*
   * This method will rotate the matrix, transposing it so the
   * cloumns will now be the rows
   *
   */
  public static int[][] rotate(int[][] matrix){
		int[][] roatedMatrix = new int[matrix[0].length][matrix.length];

		for(int i=0;i < matrix.length;i++){
			for(int j=0;j < matrix[i].length;j++){
				roatedMatrix[j][i] = matrix[i][j];
			}
		}
		return roatedMatrix;
	}

  /*
   * This method will Print the matrix (used for testing)
   *
   */
  public static void printMatrix(int matrix[][]) {
    for (int row = 0; row < matrix.length; row++) {
        for (int column = 0; column < matrix[row].length; column++) {
            System.out.print(matrix[row][column] + "     ");
        }
        System.out.println();
    }
}

}
