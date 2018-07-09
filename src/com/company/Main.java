package com.company;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static java.nio.file.Files.newBufferedReader;

public class Main {


    static String url;
    static String input;
    static String output;
    static List<String> list = new ArrayList();

    public static void main(String[] args) {
        readFromFile();
        cleanResultsOldLaunches(input);
        cleanResultsOldLaunches(output);
        downloadFileFromURL(url, input);
        readFileIntoList();

        try {
            list = listWithOnlyDomains(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        writeToFile(topTenDomain(list));
    }

    public static List<String> topTenDomain(List<String> list) {
        Map<String, Integer> map = countWords((ArrayList<String>) list);
        List<String> mylist = new ArrayList<>();

        for (Map.Entry<String, Integer> pair : map.entrySet()) {
            mylist.add(pair.getKey() + " " + pair.getValue());
        }
        return mylist;
    }

    public static Map<String, Integer> countWords(ArrayList<String> list) {
        HashMap<String, Integer> result = new HashMap<String, Integer>();
        for (int i = 0; i < list.size(); i++) {
            String slovo = list.get(i);//разбиваем лист на строки
            int num = 0;//число для каждого слова
            for (int j = 0; j < list.size(); j++) {
                if (slovo.equals(list.get(j)))//подсчет количества слов
                {
                    num++;
                }
            }
            result.put(slovo, num);

            if (list.contains(slovo))//удаляем это слово из листа, т.к. оно уже использовано.
            {
                list.remove(slovo);
            }
        }
        return result;
    }

    public static void writeToFile(List<String> list) {
        try (FileWriter writer = new FileWriter(output, false)) {
            for (int i = 0; i < list.size(); i++) {
                String s = list.get(i).toString();
                writer.write(s);
                // запись по символам
                writer.append('\n');
                writer.append('E');

                writer.flush();
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void readFromFile() {
        File file = new File("settings.txt");
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] arrays = line.split("=");
            System.out.println(Arrays.toString(arrays));

            if (arrays[0].equals("input")) {
                input = arrays[1];
            }
            if (arrays[0].equals("output")) {
                output = arrays[1];
            }
            if (arrays[0].equals("url")) {
                url = arrays[1];
            }
        }
        scanner.close();
    }

    private static void cleanResultsOldLaunches(String url) {
        File file = new File(url);
        if (file.exists())
            file.delete();
        else
            System.out.println("Error");
    }


    public static void downloadFileFromURL(String path, String file) {
        URL url = null;
        try {
            url = new URL(path);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(file);
             ReadableByteChannel inputStream = Channels.newChannel(url.openStream());) {
            fileOutputStream.getChannel().transferFrom(inputStream, 0, Long.MAX_VALUE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<String> readFileIntoList() {
        String file = "urls.txt";
        List<String> list = new ArrayList<>();

        try (BufferedReader reader = newBufferedReader(Paths.get(file))) {
            list = reader.lines().collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static List<String> listWithOnlyDomains(String input) throws IOException {
        String fileName = input.replaceAll("input=", "");
        File file = new File(fileName);
        List<String> strings = new ArrayList<String>();
        Scanner sc = new Scanner(file);

        try {

            sc = new Scanner(file);

            while (sc.hasNextLine()) {
                List<String> line = Arrays.asList(sc.nextLine().trim().replaceAll("www.", "").split("/")[0]);
                String newLine = line.get(0);
                strings.add(newLine);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            sc.close();
        }
        return strings;
    }
}
