// it means that the value of the stock they bought decreased from 2000-2010
// the lowest month for sbux was during the 2008 financial crisis
// and the lowest month for sp500 was during the great depression which makes since
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
        int w = indexOfBiggestDrop(prices);

        System.out.printf(
            "%s lowest: $%.2f in %s %d%n", file, prices[l], monthName(months[l]), years[l]);
        System.out.printf(
            "%s Highest: $%.2f in %s %d%n", file, prices[h], monthName(months[h]), years[h]);
        if (w != -1) {
            double percent = (prices[w - 1] - prices[w]) / prices[w] * 100;
            System.out.printf("%s biggest drop: %.2f%% in %s %d%n", file, percent,
                monthName(months[w]), years[w]);
        } else {
            System.out.println("Price did not fall");
        }

        double result = simulate(years, months, prices);
        System.out.printf("Final value: $%,.2f%n", result);
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

    public static int indexOfBiggestDrop(double[] prices) {
        int biggest = -1;
        double drop = 0;

        for (int i = 1; i < prices.length; i++) {
            if ((prices[i - 1] - prices[i]) / prices[i - 1] > drop) {
                drop = (prices[i - 1] - prices[i]) / prices[i - 1];
                biggest = i;
            }
        }
        return biggest;
    }

    public static final double START_CASH = 10000.0;
    public static final double FEE = 10.0;

    public static double simulate(int[] years, int[] months, double[] prices) {
        double cash = START_CASH;
        int shares = 0;
        double boughtAt = 0.0;

        for (int i = 0; i < prices.length; i++) {
            double[] history = Arrays.copyOf(prices, i + 1);
            String action = decide(history, shares > 0, boughtAt);

            if (action.equals("BUY") && shares == 0) {
                int canBuy = (int) ((cash - FEE) / prices[i]);
                if (canBuy > 0) {
                    shares = canBuy;
                    cash = cash - shares * prices[i] - FEE;
                    boughtAt = prices[i];
                    System.out.printf("%s %d: BUY %d shares at $%.2f%n", monthName(months[i]),
                        years[i], shares, prices[i]);
                }
            } else if (action.equals("SELL") && shares > 0) {
                cash = cash + shares * prices[i] - FEE;
                System.out.printf("%s %d: SELL %d shares at $%.2f%n", monthName(months[i]),
                    years[i], shares, prices[i]);
                shares = 0;
                boughtAt = 0.0;
            }
        }
        return cash + shares * prices[prices.length - 1];
    }

    public static String decide(double[] history, boolean holding, double boughtAt) {
        return myStrategy(history, holding, boughtAt); // change this line to switch strategies
    }

    public static String buyAndHold(double[] history, boolean holding, double boughtAt) {
        return "BUY";
    }

    public static String dipBuyer(double[] history, boolean holding, double boughtAt) {
        if (!holding && history.length > 2
            && history[history.length - 1] < history[history.length - 2]
            && history[history.length - 2] < history[history.length - 3])
            return "BUY";

        if (holding && history[history.length - 1] > boughtAt * 1.1)
            return "SELL";

        return "HOLD";
    }

    public static String movingAverage(double[] history, boolean holding, double boughtAt) {
        double total = 0;
        double average = 0;
        if (history.length > 11) {
            for (int i = history.length - 12; i < history.length; i++) {
                total += history[i];
            }
            average = total / 12;
            if (!holding && history[history.length - 1] > average)
                return "BUY";
            if (holding && history[history.length - 1] < average)
                return "SELL";
        }
        return "HOLD";
    }

    public static String myStrategy(double[] history, boolean holding, double boughtAt) {
        double total3 = 0;
        double average3 = 0;
        double total6 = 0;
        double average6 = 0;
        double highest3 = 0;
        
        //3Month Average
        if (history.length > 2) {
            for (int i = history.length - 3; i < history.length; i++) {
                total3 += history[i];
                if(history[i] > highest3) highest3 = history[i];
            }
            average3 = total3 / 3;
        }
        
		//6 month average
        if (history.length > 5) {
            for (int i = history.length - 6; i < history.length; i++) {
                total6 += history[i];
            }
            average6 = total6 / 6;
        }
        
        //revovery buy
        if (!holding && history.length > 3
            && history[history.length - 1] > history[history.length - 2]
            && history[history.length - 2] < history[history.length - 3]
            && history[history.length - 3] < history[history.length - 4]
            && history[history.length - 1] > average3) return "BUY";
         
        //Momentum Buy
        if (!holding && history.length > 5
            && history[history.length - 1] > history[history.length - 2]
            && history[history.length - 1] > history[history.length - 3]
            && history[history.length - 1] > history[history.length - 4]
            && average3 > average6) return"BUY";
        
        //stop loss
        if (holding && history[history.length - 1]  <= boughtAt * 0.88) return "SELL";
        
        //downtrend sell
        if(holding && history.length > 5
            && history[history.length - 1] < history[history.length - 2]
            && history[history.length - 2] < history[history.length - 3]
            && history[history.length - 1] < average3
            && history[history.length - 1] < average6) return "SELL";
        
        //profit protection
        if(holding && history[history.length - 1] > boughtAt * 1.15 
        && history[history.length - 1] < highest3 * 0.90) return"SELL";
        
        return"HOLD";
    }
}

