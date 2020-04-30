import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ReadFile {
  public static final String PROJECT_NAME = "hadoop";
  public static final String INPUT_FILE_AUTHOR = PROJECT_NAME + "_all_files_commits_with_author.txt";
  public static final String INPUT_FILE_POST_BUG = PROJECT_NAME + "_all_post_release_issue.txt";
  public static final String INPUT_FILE_PRE_BUG = PROJECT_NAME + "_all_pre_release_issue.txt";
  public static final String INPUT_FILE_POST_COMMITS = PROJECT_NAME + "_all_commits_after_release.txt";
  public static final String INPUT_FILE_PRE_COMMITS = PROJECT_NAME + "_all_commits_before_release.txt";
  public static final String INPUT_FILE_PRE_FILE_CHANGE = PROJECT_NAME + "_all_file_changes_before_release.txt";
  public static final String OUTPUT_FILE = PROJECT_NAME + "_metrics.csv";

  public static void main(String args[]) {

    Map<String, Integer> post_release_bugs = new HashMap<>();
    post_release_bugs = findBugs(INPUT_FILE_POST_BUG, INPUT_FILE_POST_COMMITS);
    System.out.println("Data Extracted!");

    Map<String, Integer> pre_release_bugs = new HashMap<>();
    pre_release_bugs = findBugs(INPUT_FILE_PRE_BUG, INPUT_FILE_PRE_COMMITS);
    System.out.println("Data Extracted!");

    Map<String, Integer> unique_authors = new HashMap<>();
    unique_authors = findAuthors(INPUT_FILE_AUTHOR);
    System.out.println("Data Extracted!");

    Map<String, int[]> codeChurn_change = new HashMap<>();
    codeChurn_change = findCodeChurnAndChange(INPUT_FILE_PRE_FILE_CHANGE);
    System.out.println("Data Extracted!");

    Map<String, int[]> dataset;
    dataset = mergeData(unique_authors, codeChurn_change, post_release_bugs, pre_release_bugs);

    printResult(dataset);
    
  }

  // This method convert the input file containing issues to list
  private static List<String> readBugs(String fileName) {
    List<String> bugsList = new ArrayList<String>();

    try(BufferedReader br = new BufferedReader(new FileReader(fileName))){
      String str;
      while ((str = br.readLine()) != null)
        bugsList.add(str);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return bugsList;
  }

  private static Map<String, Integer> findBugs(String bugFile, String changeFile) {

    List<String> bugList = readBugs(bugFile);
    final Map<String, Integer> result = new HashMap<>();
    for (String bug : bugList) {
      try (BufferedReader br = new BufferedReader(new FileReader(changeFile))) {

        String line;
        boolean isTargetChange = false;
        while ((line = br.readLine()) != null) {
          if (line.contains("commit--") && line.contains(bug)) {
            isTargetChange = true;
          } else if (!line.isEmpty() && line.contains(".java") && isTargetChange) {
            //result.put(line, Integer.max(result.get(line)+1, 1));
            updateCounter(result, line);
          } else if (line.isEmpty()) {
            isTargetChange = false;
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return result;
  }

  private static Map<String, Integer> findAuthors(String fileName) {

    final Map<String, Set<String>> result = new HashMap<>();
    final Map<String, Integer> authors = new HashMap<>();

    try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {

      String line;
      String author = "";
      while ((line = br.readLine()) != null) {
        if (line.contains("name--")) {
          author = line.substring("name--".length());
        } else if (!line.isEmpty() && line.contains(".java") && !line.startsWith("branch-")) {
          updateAuthor(result, line, author);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    for(Map.Entry<String, Set<String>> entry : result.entrySet()){
      authors.put(entry.getKey(), entry.getValue().size());
    }
    return authors;
  }

  private static void updateAuthor(Map<String, Set<String>> result, String line, String author){
    if (result.containsKey(line)) {
      result.get(line).add(author);
    } else {
      result.put(line, new HashSet<>());
      result.get(line).add(author);
    }
  }

  private static void updateCounter(Map<String, Integer> result, String line) {

    if (result.containsKey(line)) {
      int counter = result.get(line);
      counter++;
      result.put(line, counter);
    } else {
      result.put(line, 1);
    }
  }

  private static Map<String, int[]> findCodeChurnAndChange(String fileName){

    final Map<String, int[]> result = new HashMap<>();

    try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {

      String line;
      String file;
      int codeChurn = 0;
      String[] lineSplited;

      while ((line = br.readLine()) != null) {
        if (!line.contains("commit--") && !line.isEmpty() && line.contains(".java") && !line.contains("=>")) {
          line = line.trim();
          lineSplited = line.split("\t");
          codeChurn = Integer.parseInt(lineSplited[0]) + Integer.parseInt(lineSplited[1]);
          file = lineSplited[2];
          updateCodeChurnAndChange(result, file, codeChurn);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return result;
  }

  private static void updateCodeChurnAndChange(Map<String, int[]> result, String file, int codeChurn){

    if (result.containsKey(file)) {
      result.get(file)[0] = result.get(file)[0] + codeChurn;
      result.get(file)[1] = result.get(file)[1] + 1;
    } else {
      int[] value = new int[2];
      value[0] = codeChurn;
      value[1] = 1;
      result.put(file, value);
    }
  }

  private static Map<String, int[]> mergeData(Map<String, Integer> authors, Map<String, int[]> changes, Map<String, Integer> post_bugs, Map<String, Integer> pre_bugs) {
    Map<String, int[]> dataset = new HashMap<>();

    for (String file : authors.keySet()) {
      //value[0]=number of authors, value[1]=number of code churn, value[2]=number of changes, value[3]=post-release bugs, value[4]=pre-release bugs
      int[] values = new int[5];
      values[0] = authors.get(file);
      if (changes.containsKey(file)) {
        values[1] = changes.get(file)[0];
        values[2] = changes.get(file)[1];
      }
      if (post_bugs.containsKey(file))
        values[3] = post_bugs.get(file);
      if (pre_bugs.containsKey(file))
        values[4] = pre_bugs.get(file);
      dataset.put(file, values);
    }
    System.out.println("Done");
    return dataset;
  }

  private static void printResult(Map<String, int[]> dataset) {

    String eol = System.getProperty("line.separator");
    try (FileWriter writer = new FileWriter(OUTPUT_FILE)){

      for(Map.Entry<String, int[]> entry : dataset.entrySet()){
        writer.append(entry.getKey())
                .append(',')
                .append(String.valueOf(entry.getValue()[0]))
                .append(',')
                .append(String.valueOf(entry.getValue()[1]))
                .append(',')
                .append(String.valueOf(entry.getValue()[2]))
                .append(',')
                .append(String.valueOf(entry.getValue()[3]))
                .append(',')
                .append(String.valueOf(entry.getValue()[4]))
                .append(eol);
      }
    } catch (IOException ex) {
      ex.printStackTrace(System.err);
    }

    //bw.write(String.format(entry.getKey(), entry.getValue()[0], entry.getValue()[1], entry.getValue()[2], entry.getValue()[3], entry.getValue()[4]));

  }

}
