/***************************************************************
 * @file: SeparateChainingHashTable.java
 * @description: Generic hash table implementation using
 *               separate chaining for collision handling.
 * @author: Ben Martin
 * @date: December 15, 2025
 ***************************************************************/

import java.util.LinkedList;
import java.util.List;

public class SeparateChainingHashTable<AnyType>
{
    private static final int DEFAULT_TABLE_SIZE = 101;

    private List<AnyType>[] theLists;
    private int currentSize;

    public SeparateChainingHashTable()
    {
        this(DEFAULT_TABLE_SIZE);
    }

    public SeparateChainingHashTable(int size)
    {
        theLists = new LinkedList[nextPrime(size)];
        for (int i = 0; i < theLists.length; i++)
        {
            theLists[i] = new LinkedList<>();
        }

        currentSize = 0;
    }

    public void insert(AnyType x)
    {
        int whichList = myhash(x);
        List<AnyType> list = theLists[whichList];

        if (!list.contains(x))
        {
            list.add(x);
            currentSize++;

            // Rehash if load factor exceeds 1.0
            if (currentSize > theLists.length)
            {
                rehash();
            }
        }
    }

    public void remove(AnyType x)
    {
        int whichList = myhash(x);
        List<AnyType> list = theLists[whichList];

        if (list.remove(x))
        {
            currentSize--;
        }
    }

    public boolean contains(AnyType x)
    {
        int whichList = myhash(x);
        return theLists[whichList].contains(x);
    }

    public void makeEmpty()
    {
        for (int i = 0; i < theLists.length; i++)
        {
            theLists[i].clear();
        }

        currentSize = 0;
    }

    public static int hash(String key, int tableSize)
    {
        int hashVal = 0;

        for (int i = 0; i < key.length(); i++)
        {
            hashVal = 37 * hashVal + key.charAt(i);
        }

        hashVal %= tableSize;
        if (hashVal < 0)
        {
            hashVal += tableSize;
        }

        return hashVal;
    }

    @SuppressWarnings("unchecked")
    private void rehash()
    {
        List<AnyType>[] oldLists = theLists;

        theLists = new List[nextPrime(2 * oldLists.length)];
        for (int i = 0; i < theLists.length; i++)
        {
            theLists[i] = new LinkedList<>();
        }

        currentSize = 0;

        for (List<AnyType> oldList : oldLists)
        {
            for (AnyType item : oldList)
            {
                insert(item);
            }
        }
    }

    private int myhash(AnyType x)
    {
        int hashVal = x.hashCode();

        hashVal %= theLists.length;
        if (hashVal < 0)
        {
            hashVal += theLists.length;
        }

        return hashVal;
    }

    private static int nextPrime(int n)
    {
        if (n % 2 == 0)
        {
            n++;
        }

        while (!isPrime(n))
        {
            n += 2;
        }

        return n;
    }

    private static boolean isPrime(int n)
    {
        if (n == 2 || n == 3)
        {
            return true;
        }

        if (n == 1 || n % 2 == 0)
        {
            return false;
        }

        for (int i = 3; i * i <= n; i += 2)
        {
            if (n % i == 0)
            {
                return false;
            }
        }

        return true;
    }
}
