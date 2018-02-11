package code;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс использует паттерн одиночка во избежания возможности утери настроек и для достука к ним в разных частях программы.
 */
public class ComplexityManager {

    private static ComplexityManager instance;

    private FileWorker fileWorker;
    private List<Complexity> complexities;

    private ComplexityManager() {
        complexities = new ArrayList<>();
        complexities.add(new Complexity("Новичок", 9, 9, 10));
        complexities.add(new Complexity("Любитель", 16, 16, 40));
        complexities.add(new Complexity("Профессионал", 16, 30, 99));

        fileWorker = new FileWorker("src\\files\\property");

    }

    public Complexity getCurrentComplexity() {
        return complexities.get(fileWorker.getIndexOfComplexityListFromFile());
    }

    public void setCurrentComplexity(int index){
        fileWorker.setIndexOfComplexityToFile(index);
    }

    public static ComplexityManager getInstance(){
        if(instance == null)
            instance = new ComplexityManager();
            return instance;
    }

    /**
     * Класс для считывание текущей сложности игры с файла.
     * P.S. Если файл удалили или испортили, программа все-равно будет успешно функционировать.
     */
    private class FileWorker {
        private File file;

        public FileWorker(String filepath) {
            this.file = new File(filepath);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e){}
            }
        }

        private int getIndexOfComplexityListFromFile(){
            int complexityIndex = 0;
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                complexityIndex = Integer.parseInt(br.readLine().trim());
            } catch (Exception e){
                System.err.println("Файл был удален или испорчен");
            }
            return complexityIndex;
        }

        private void setIndexOfComplexityToFile(int complexityIndex){
            FileWriter writer = null;
            try {
                writer = new FileWriter(file);
                writer.write(String.valueOf(complexityIndex));
                writer.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }

    }

}
