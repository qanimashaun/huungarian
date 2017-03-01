import java.util.*;
import java.io.*;

public class GenerateStudentList{
  public static void main(String[] args) throws IOException{
    List<Integer> dataList = new ArrayList<Integer>();
    PrintWriter writer = new PrintWriter("projectList.txt", "UTF-8");
    for (int i = 1; i < 101; i++) {
      writer.println(i+" Project " + i);
    }
    writer.close();
  }
}
