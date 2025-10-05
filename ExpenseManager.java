import java.util.Scanner;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
//import java.util.Random;

public class ExpenseManager {
    static ArrayList<Expense> expenses = new ArrayList<>();
    static String categoryList = ""; //used to show the user a list of the categories they have previously created
    static Scanner input = new Scanner(System.in);
    static boolean shouldExit;
    public static void main(String [] args) {
        System.out.println("Welcome.");
        while (!shouldExit) {
            selectOption();
        }

        System.out.println("Goodbye.");
    }

    private static void selectOption() {
        // this method gives the user a list of actions and calls the corresponding method //

        String option = setValidString("How would you like to manage your expenses?", "add", "remove", "view", "exit");

        switch (option) {
            case "add": addExpense(); break;
            case "remove": removeExpense(); break;
            case "view": viewExpense(); break;
            case "exit": shouldExit = true; break;
            //case "random": AddDebugValueToExpenses();
        }
    }

    private static void addExpense() {
        // this method gets information about the expense, and adds it to the list
        
        String desc = getNonNullString("What is the description of your expenses? ");

        float amount = getValidInt("What is the amount? $", 0, Integer.MAX_VALUE);

        Calendar date = getValidDate("What is the date?");

        System.out.print("What is the category? ");
        //if the category list is not empty, show it to the user
        if (!categoryList.isBlank()) {
            System.out.print("(previously-used categories are: " + categoryList + ".): ");
        }

        //get the new category name
        String category = getNonNullString("");
        
        //add a comma to the end of the list if it is not empty, then add the category to categoryList if it is new
        if (!categoryList.contains(category)) {
            if (!categoryList.isBlank()) {
                categoryList += ", ";
            }
            categoryList += category;
        }

        Expense newExpense = new Expense(desc, amount, date, category);
        expenses.add(newExpense);
        System.out.println("The expense was added.");
    }

    private static void removeExpense() {
        // this method shows a list of the expenses' desccriptions, asks the user which expense they wish to remove, and removes it //

        if (expenses.isEmpty()) {
            System.out.print("There are no expenses listed in the tracker, so none can be removed.");
            return;
        }

        //print list of expenses
        System.out.println("These are the expenses currently in the tracker:");
        int i = 1;
        for (Expense e: expenses) {
            System.out.print(i + ": " + e.description);

            //add comma between expenses, but not after the last
            if (i != expenses.size()) {
                System.out.print(", ");
            }

            i++;
        }

        System.out.println(""); //empty line
        int indexToRemove = getValidInt("What is the index of the expense you would like to remove?: ", 1, expenses.size());
        expenses.remove(indexToRemove - 1); //the list of indexes for the user begins with 1 since that is more intuative, but the computer's list begins with 0
        System.out.println("The expense was removed.");
    }

    private static void viewExpense() {
        // this method asks the user for a time frame and prints all expenses within it //

        if (expenses.isEmpty()) {
            System.out.print("There are no expenses listed in the tracker.");
            return;
        }

        // Calendar today = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        Calendar minDate = Calendar.getInstance();
        Calendar maxDate = Calendar.getInstance();

        String period = getValidString("What period of time would you like to view the expenses for?", "all", "month", "week", "custom");

        switch (period) {
            case "all":
                System.out.println("All expenses added to tracker:");
                minDate.set(0,0,1);
                maxDate.set(9999,11,31);
                break;
            case "month":
                System.out.println("Expenses added to tracker this month: " + today.get(Calendar.YEAR) + today.get(Calendar.MONTH));
                minDate.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), 1);
                maxDate.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), 31);
                break;
            case "week":
                System.out.println("Expenses added to tracker this week: ");
                minDate.add(Calendar.DAY_OF_WEEK, -1);
                maxDate.add(Calendar.DAY_OF_WEEK, 7);
                break;
            case "custom":
                //ask the user for the minimum and maximum dates
                minDate = getValidDate("What is the minimum date of the custom time period?");
                maxDate = getValidDate("What is the maximum date?");

                //convert the min and max dates to strings
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                String minDateStr = dateFormat.format(minDate.getTime());
                String maxDateStr = dateFormat.format(maxDate.getTime());

                System.out.println("Expenses added to tracker between " + minDateStr + " and " + maxDateStr);
        }

        //print the expenses between the specified dates
        for (Expense e: expenses) {
            if (e.date.after(minDate) && maxDate.after(e.date)) {
                e.toString();
            }
        }
    }
    
    private static Calendar getValidDate(String message) {
        // this method reads input, ensures that it can be converted to a Calendar object, and returns it //

        //get valid input
        String dateStr;
        do {
            System.out.print(message + " (formatted as MM/DD/YYYY): ");
            dateStr = input.nextLine();
        } while (!isDateFormattingValid(dateStr));

        //create and return Calendar
        Calendar date = Calendar.getInstance();
        date.set(Integer.parseInt(dateStr.substring(6)), Integer.parseInt(dateStr.substring(0,2)) - 1, Integer.parseInt(dateStr.substring(3,5)));
        return date;
    }

    private static String getValidString(String message, String... choices) {
        // this method gets input, ensures that it is included in the list of choices, and returns it //

        boolean isValid;
        String response;

        //update the message to include the list of choices
        message += " (";
        for (int i = 0; i < choices.length; i++) {
            message += choices[i];

            //add a comma between choices, but not after the last choice
            if (i != choices.length - 1) {
                message += ", ";
            }
        }
        message += "): ";

        //get input and check validity
        do {
            System.out.println(""); //blank line
            System.out.print(message);
            response = input.nextLine().toLowerCase();

            isValid = Arrays.stream(choices).anyMatch(response::equals);

            if (!isValid) {
                System.out.println("Error: Your response is not one of the available choices.");
            }

        } while (!isValid);

        return response;
    }

    private static String getNonNullString(String message) {
        // this method gets input, ensures that it is not null or empty, and returns it //

        boolean isValid;
        String response;

        //get input and check validity
        do {
            System.out.println(""); //blank line
            System.out.print(message);
            response = input.nextLine().toLowerCase();

            isValid = !response.isBlank();

        } while (!isValid);

        return response;
    }

    private static int getValidInt(String message, int minSize, int maxSize) {
        // this method gets input, ensures that it is a non-negative number, and returns it //

        int integer = -1;
        boolean isValid;

        do {
            System.out.print(message);
            isValid = true;

            //check that the input is a number
            try {
                integer = Integer.parseInt(input.nextLine());
            }
            catch (NumberFormatException e) {
                System.out.println("Error: Your input is not a valid number.");
                isValid = false;
                continue; //the loop does not need to continue if the input is not an integer
            }
            
            //check that the input is within range
            if (integer < minSize) {
                System.out.println("Error: The amount must be " + minSize + " or greater.");
                isValid = false;
            }
            else if (integer > maxSize) {
                System.out.println("Error: The amount must be " + maxSize + " or less.");
                isValid = false;
            }
        } while (!isValid);

        return integer;
    }

    private static boolean isDateFormattingValid(String date) {
        // this method checks whether the given String is a valid date //

        //check that the input isn't null.
        if(date.equals("")) {
            //there is no need for a specific error message since the default gives the user all the information they need
            printErrorMessages();
            return false;
        }

        //check that the input is correctly formatted
        if (date.length() != 10 || !date.substring(2,3).equals("/") || !date.substring(5,6).equals("/")) {
            //the length is checked first so the indexes for the substring will not be out of bounds
            System.out.println("Error: The date is missing one or both slashes or the month, day, or year have been formatted with the incorrect number of characters.");
            printErrorMessages();
            return false;
        }

        //check that the input can be translated into a date
        try {
            int month = Integer.parseInt(date.substring(0, 2));
            int day = Integer.parseInt(date.substring(3, 5));
            int year = Integer.parseInt(date.substring(6));

            if (month <= 0 || month > 12) {
                System.out.println("Error: The month should be between 1 (January) and 12 (December).");
            }
            else if (day <= 0 || day > 31) {
                System.out.println("Error: The day should be between 1 and 31.");
            }
            else if (year < 0) {
                System.out.println("Error: This expense tracker does not account for dates before the common era. Please enter a non-negative year.");
            }
            else if (month == 2 && day == 29) {
                boolean isLeapYear = getValidString("Was the year " + year + " a leap year?", "yes", "no").equals("yes");
                if (isLeapYear) {
                    return true;
                }

                System.out.println("Error: February only has 29 days during leap years.");
            }
            else if (month == 2 && day > 29) {
                System.out.println("Error: February only has 28 days (or 29 during leap years).");
            }
            else {
                return true;
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: The input could not be read.");
            return false;
        }

        // print error messages if one of the if-statements was true (except if the date is the 29th of February during a leap year)
        printErrorMessages();
        return false;
    }

    private static void printErrorMessages() {
        System.out.println("Please enter the date as two digits for the month, then a slash (/), two digits for the day, another slash, and four digits for the year.");
        System.out.println("Example: September 28th, 2025 should be formatted as 09/28/2025.");
    }

    /* private static void addDebugValueToExpenses() {
        //used for debugging ViewExpense()

        Calendar date = Calendar.getInstance();
        Random generator = new Random();

        date.set(2025, generator.nextInt(0, 11), generator.nextInt(1, 31)); //to randomize the year, replace 2025 with generator.nextInt(0,9999)
        expenses.add(new Expense("N/A", 0, date, "N/A"));

        if (expenses.size() < 5) {
            AddDebugValueToExpenses();
        }
    } */
}

class Expense {
    String description;
    float amount;
    Calendar date;
    String category;

    public Expense(String desc, float amount, Calendar date, String category) {
        this.description = desc;
        this.amount = amount;
        this.date = date;
        this.category = category;
    }

    void toString() {
        String dateStr = new SimpleDateFormat("MM/dd/yyyy").format(date.getTime());
        System.out.printf("Description: %s, amount: %.2f, date: %s, category: %s %n", description, amount, dateStr, category);
    }

}
