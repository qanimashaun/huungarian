import java.util.*;
import java.io.*;

public class Generate{
  public static void main(String[] args) throws IOException{
    List<Integer> dataList = new ArrayList<Integer>();
    PrintWriter writer = new PrintWriter("studentlist2.txt", "UTF-8");
    for (int i = 1; i < 101; i++) {
      dataList.add(i);
    }

    int[] num = new int[dataList.size()];
    for (int i = 0; i < dataList.size(); i++) {
      writer.print((i+1) +" ");
      writer.print("Student " + (i+1) +" ");
      Collections.shuffle(dataList);
      for (int j : dataList){
        writer.print(j+" ");
      }
      writer.println();
    }

    writer.close();
  }
}
