/**************************************************
 * @file: Parser.java
 * @description: Loads HorrorMovie data from a CSV file into ArrayLists for Project 4.
 * @author: Ben Martin
 * @date: December 15, 2025
 **************************************************/

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Parser
{

    // Column indexes from the CSV
    private static final int TITLE_COL = 2;
    private static final int RATING_COL = 10;

    // Loads up to n movie titles from the CSV
    public static ArrayList<String> loadTitles(String csvPath, int n) throws IOException
    {
        ArrayList<String> titles = new ArrayList<>(Math.max(0, n));

        try (BufferedReader br = new BufferedReader(new FileReader(csvPath)))
        {
            String line;

            // Skip header
            br.readLine();

            while (titles.size() < n && (line = br.readLine()) != null)
            {
                if (line.isBlank())
                {
                    continue;
                }

                String[] cols = splitCsvLine(line);
                String title = getCol(cols, TITLE_COL).trim();

                if (!title.isEmpty())
                {
                    titles.add(title);
                }
            }
        }

        return titles;
    }

    // Loads up to n HorrorMovie objects from the CSV
    public static ArrayList<HorrorMovie> loadMovies(String csvPath, int n) throws IOException
    {
        ArrayList<HorrorMovie> movies = new ArrayList<>(Math.max(0, n));

        try (BufferedReader br = new BufferedReader(new FileReader(csvPath)))
        {
            String line;

            // Skip header
            br.readLine();

            while (movies.size() < n && (line = br.readLine()) != null)
            {
                if (line.isBlank())
                {
                    continue;
                }

                String[] cols = splitCsvLine(line);
                String title = getCol(cols, TITLE_COL).trim();
                double rating = parseDoubleSafe(getCol(cols, RATING_COL));

                if (!title.isEmpty())
                {
                    movies.add(new HorrorMovie(title, rating));
                }
            }
        }

        return movies;
    }

    // Safely get a column value or empty string
    private static String getCol(String[] cols, int index)
    {
        if (index < 0 || index >= cols.length)
        {
            return "";
        }

        return cols[index];
    }

    // Parse doubles defensively
    private static double parseDoubleSafe(String s)
    {
        if (s == null)
        {
            return 0.0;
        }

        s = s.trim();

        if (s.isEmpty())
        {
            return 0.0;
        }

        try
        {
            return Double.parseDouble(s);
        }
        catch (NumberFormatException e)
        {
            return 0.0;
        }
    }

    // Splits a CSV line while respecting quoted fields
    private static String[] splitCsvLine(String line)
    {
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++)
        {
            char c = line.charAt(i);

            if (c == '"')
            {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"')
                {
                    current.append('"');
                    i++;
                }
                else
                {
                    inQuotes = !inQuotes;
                }
            }
            else if (c == ',' && !inQuotes)
            {
                fields.add(current.toString());
                current.setLength(0);
            }
            else
            {
                current.append(c);
            }
        }

        fields.add(current.toString());
        return fields.toArray(new String[0]);
    }
}
