//it means that the value of the stock they bought decreased from 2000-2010
//the lowest month for sbux was during the 2008 financial crisis 
	//and the lowest month for sp500 was during the great depression which makes since
//


import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class StockTrader {
    public static void main(String[] args) throws FileNotFoundException {
		Scanner s = new Scanner(System.in);
		System.out.print("Input the file name: ");
		String file = s.next();
		
        Scanner in = new Scanner(new File(file + ".txt"));
        String symbol = in.next();
        int n = in.nextInt();

        // create the three arrays, each with n elements
        int[] years = new int[n];
        int[] months = new int[n];
        double[] prices = new double[n];

        // call loadData, then printData
        loadData(in, years, months, prices);
        printData(years, months, prices);
        
        int l = indexOfLowest(prices);
        int h = indexOfHighest(prices);
        
        System.out.printf("%s lowest: $%.2f in %s %d%n", file, prices[l], monthName(months[l]), years[l]);
        System.out.printf("%s Highest: $%.2f in %s %d%n", file, prices[h], monthName(months[h]), years[h]);
    }

    public static void loadData(Scanner in, int[] years, int[] months, double[] prices) {
        for (int i = 0; i < years.length; i++) {
            years[i] = in.nextInt();
            months[i] = in.nextInt();
            prices[i] = in.nextDouble();
        }
    }

    public static String monthName(int month) {
        String[] names = {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return names[month - 1];
    }

    public static void printData(int[] years, int[] months, double[] prices) {
        for (int i = 0; i < years.length; i++) {
            System.out.printf("%s %d: $%.2f%n", monthName(months[i]), years[i], prices[i]);
        }
    }

    public static int indexOfLowest(double[] prices) {
        int lowest = 0;

        for (int i = 0; i < prices.length; i++) {
            if (prices[i] < prices[lowest])
                lowest = i;
        }
        return lowest;
    }

    public static int indexOfHighest(double[] prices) {
        int highest = 0;

        for (int i = 0; i < prices.length; i++) {
            if (prices[i] > prices[highest])
                highest = i;
        }
        return highest;
    }
}
