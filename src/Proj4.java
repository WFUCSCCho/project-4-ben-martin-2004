/**************************************************
 * @file: Proj4.java
 * @description: Project 4 driver. Loads dataset, builds sorted/shuffled/reversed lists,
 *               times hash table insert/search/delete, prints results, appends CSV to analysis.txt.
 * @author: Ben Martin
 * @date: December 15, 2025
 **************************************************/

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Proj4
{
    public static void main(String[] args)
    {
        if (args.length < 2)
        {
            System.out.println("Usage: java Proj4 {dataset-file} {number-of-lines}");
            return;
        }

        String datasetFile = args[0];
        int n;

        try
        {
            n = Integer.parseInt(args[1]);
        }
        catch (NumberFormatException e)
        {
            System.out.println("Second argument must be an integer number of lines.");
            return;
        }

        if (n <= 0)
        {
            System.out.println("Number of lines must be > 0.");
            return;
        }

        ArrayList<String> data;

        try
        {
            // Uses the parser I wrote for you earlier. If your class name differs, update this line.
            data = Parser.loadTitles(datasetFile, n);
        }
        catch (IOException e)
        {
            System.out.println("Error reading dataset: " + e.getMessage());
            return;
        }

        int actualN = data.size();

        if (actualN == 0)
        {
            System.out.println("No data loaded. Check file path and format.");
            return;
        }

        // Prepare the three cases from the same loaded data
        ArrayList<String> sorted = new ArrayList<>(data);
        Collections.sort(sorted);

        ArrayList<String> shuffled = new ArrayList<>(data);
        Collections.shuffle(shuffled);

        ArrayList<String> reversed = new ArrayList<>(sorted);
        Collections.sort(reversed, Collections.reverseOrder());

        // Run tests
        TimingResult rSorted = timeCase("already_sorted", sorted);
        TimingResult rShuffled = timeCase("shuffled", shuffled);
        TimingResult rReversed = timeCase("reversed", reversed);

        // Print human-readable output
        System.out.println();
        System.out.println("Dataset file: " + datasetFile);
        System.out.println("Lines requested: " + n + " | Lines loaded: " + actualN);
        System.out.println();

        printResult(rSorted, actualN);
        printResult(rShuffled, actualN);
        printResult(rReversed, actualN);

        // Append CSV to analysis.txt
        try
        {
            appendCsv("analysis.txt", actualN, rSorted);
            appendCsv("analysis.txt", actualN, rShuffled);
            appendCsv("analysis.txt", actualN, rReversed);
            System.out.println();
            System.out.println("Appended results to analysis.txt");
        }
        catch (IOException e)
        {
            System.out.println("Could not write analysis.txt: " + e.getMessage());
        }
    }

    private static TimingResult timeCase(String caseName, ArrayList<String> list)
    {
        SeparateChainingHashTable<String> table = new SeparateChainingHashTable<>();

        long insertStart = System.nanoTime();
        for (String key : list)
        {
            table.insert(key);
        }
        long insertEnd = System.nanoTime();

        long searchStart = System.nanoTime();
        for (String key : list)
        {
            table.contains(key);
        }
        long searchEnd = System.nanoTime();

        long deleteStart = System.nanoTime();
        for (String key : list)
        {
            table.remove(key);
        }
        long deleteEnd = System.nanoTime();

        // Table should be empty now (assignment requirement)
        // If your hash table has makeEmpty() you can call it, but this should already be empty.

        return new TimingResult(caseName,
                insertEnd - insertStart,
                searchEnd - searchStart,
                deleteEnd - deleteStart);
    }

    private static void printResult(TimingResult r, int n)
    {
        System.out.println("Case: " + r.caseName);
        System.out.println("N = " + n);

        System.out.printf("  Insert:  %.6f s%n", r.insertNs / 1_000_000_000.0);
        System.out.printf("  Search:  %.6f s%n", r.searchNs / 1_000_000_000.0);
        System.out.printf("  Delete:  %.6f s%n", r.deleteNs / 1_000_000_000.0);

        System.out.println();
    }

    private static void appendCsv(String filePath, int n, TimingResult r) throws IOException
    {
        File f = new File(filePath);
        boolean needHeader = (!f.exists() || f.length() == 0);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true)))
        {
            if (needHeader)
            {
                bw.write("N,case,insert_seconds,search_seconds,delete_seconds");
                bw.newLine();
            }

            double insertS = r.insertNs / 1_000_000_000.0;
            double searchS = r.searchNs / 1_000_000_000.0;
            double deleteS = r.deleteNs / 1_000_000_000.0;

            bw.write(n + "," + r.caseName + "," + insertS + "," + searchS + "," + deleteS);
            bw.newLine();
        }
    }

    private static class TimingResult
    {
        String caseName;
        long insertNs;
        long searchNs;
        long deleteNs;

        TimingResult(String caseName, long insertNs, long searchNs, long deleteNs)
        {
            this.caseName = caseName;
            this.insertNs = insertNs;
            this.searchNs = searchNs;
            this.deleteNs = deleteNs;
        }
    }
}
