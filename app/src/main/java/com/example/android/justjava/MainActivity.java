package com.example.android.justjava;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;


//This app displays an order form to order coffee.
public class MainActivity extends AppCompatActivity {
    //create global variables that will be initialized during onCreate
    public int quantity = 0;
    public TextView textViewQuantity;
    public Button buttonDecrement;
    public Button buttonIncrement;
    public CheckBox checkBoxWhippedCream;
    public CheckBox checkBoxChocolate;
    public EditText editTextName;
    public CheckBox checkBoxCinnamon;

    double priceForChocolate;
    double priceForCinnamon;
    double priceForWhippedCream;
    double priceForSingleCoffee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize global variables
        editTextName = findViewById(R.id.editTextName);
        textViewQuantity = findViewById(R.id.quantity_text_view);
        buttonDecrement = findViewById(R.id.decrementButton);
        checkBoxWhippedCream = findViewById(R.id.checkBoxWhippedCream);
        checkBoxChocolate = findViewById(R.id.checkBoxChocolate);
        checkBoxCinnamon = findViewById(R.id.checkBoxCinnamon);
        buttonIncrement = findViewById(R.id.incrementButton);

        priceForChocolate = getPrice("chocolate");
        priceForCinnamon = getPrice("cinnamon");
        priceForWhippedCream = getPrice("whipped cream");
        priceForSingleCoffee = getPrice("coffee");

        //listens for enter key to be pressed so the soft keyboard will close when user presses enter.
        editTextName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(editTextName.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });
    }

    /**
     * Returns price of an item.
     *
     * @param getPriceOf returns the price of the specified item.
     * @return price which can be found in res/values/prices.xml
     */
    public double getPrice(String getPriceOf) {
        switch (getPriceOf.toLowerCase()) {
            case "coffee":
                return Double.parseDouble(getString(R.string.priceOfCoffee));
            case "chocolate":
                return Double.parseDouble(getString(R.string.priceOfChocolate));
            case "cinnamon":
                return Double.parseDouble(getString(R.string.priceOfCinnamon));
            case "whipped cream":
                return Double.parseDouble(getString(R.string.priceOfWhippedCream));
            default:
                return -1;
        }
    }

    /**
     * Called when the order button is pressed.
     * <p>
     * It verifies quantity is greater than zero, checks if any toppings are selected, and saves these as local boolean values. These values will be passed
     * into the calculateTotalPrice and createOrderSummary methods to calculate the price and generate an itemized receipt respectively.
     */
    public void submitOrder(View view) {
        if (quantity == 0) {
            Toast.makeText(getApplicationContext(), R.string.textViewOrderSummaryDetails,
                    Toast.LENGTH_SHORT).show();
        } else if (quantity > 0) {
            boolean whippedCream = checkBoxWhippedCream.isChecked(); // is whipped cream checkbox checked?
            boolean chocolate = checkBoxChocolate.isChecked(); // is chocolate checkbox checked?
            boolean cinnamon = checkBoxCinnamon.isChecked(); //is cinnamon checkbox checked?


            double totalPrice = calculateTotalPrice(priceForSingleCoffee, chocolate, cinnamon, whippedCream); //calculates the price and stores in local variable
            String orderSummary = createOrderSummary(chocolate, cinnamon, whippedCream, totalPrice); //creates a detailed receipt
            composeEmail(getString(R.string.emailSubjectLine) + " " + editTextName.getText(), orderSummary); // passes receipt into email
        }

    }

    /**
     * Generates a detailed receipt.
     *
     * @param addChocolate    boolean value if user wants chocolate
     * @param addCinnamon     boolean value if user wants cinnamon
     * @param addWhippedCream boolean value if user wants whipped cream
     * @param totalPrice      double value of the total price
     *                        An if statement evaluates each boolean value to determine if that item should be added to the receipt.
     */
    public String createOrderSummary(boolean addChocolate, boolean addCinnamon, boolean addWhippedCream, double totalPrice) {
        String nameForOrder = editTextName.getText().toString(); // create local variable to make it easier to reference this later
        String inclChocolateOnReceipt = ""; //initialize to empty string
        String inclCinnamonOnReceipt = "";
        String inclWhippedCreamOnReceipt = "";

        if (addChocolate) { //create receipt entry for each boolean item that evaluates to true and add the string to the above String variables
            inclChocolateOnReceipt = "\n   +$" + convertLocale(calculateItemPrice(quantity, priceForChocolate)) + " " + getString(R.string.strChocolate) + " ($" + convertLocale(priceForChocolate) + "/" + getString(R.string.strEach) + ")";
        }

        if (addCinnamon) {
            inclCinnamonOnReceipt = "\n   +$" + convertLocale((calculateItemPrice(quantity, priceForCinnamon))) + " " + getString(R.string.strCinnamon) + " ($" + convertLocale(priceForCinnamon) + "/" + getString(R.string.strEach) + ")";
        }

        if (addWhippedCream) {
            inclWhippedCreamOnReceipt = "\n   +$" + convertLocale(calculateItemPrice(quantity, priceForWhippedCream)) + " " + getString(R.string.strWhipped_cream) + " ($" + convertLocale(priceForWhippedCream) + "/" + getString(R.string.strEach) + ")";
        }

        String receipt = getString(R.string.strName, nameForOrder) + //create the actual receipt
                "\n" +
                "$" + convertLocale(calculateItemPrice(quantity, priceForSingleCoffee)) + " " + getString(R.string.strBase_price) + " ($" + convertLocale(priceForSingleCoffee) + "/" + getString(R.string.strEach) + ")" +
                inclChocolateOnReceipt +
                inclCinnamonOnReceipt +
                inclWhippedCreamOnReceipt +
                "\n" +
                "\n" + getString(R.string.strTotal) + convertLocale(totalPrice) +
                "\n" + getString(R.string.strThank_you);
        return receipt; //return the receipt as a string so it can be emailed
    }

    /**
     * Evaluates total cost including base price and price of selected toppings.
     *
     * @param addChocolate    boolean value if user wants chocolate
     * @param addCinnamon     boolean value if user wants cinnamon
     * @param addWhippedCream boolean value if user wants whipped cream
     *                        An if statement evaluates each boolean value to determine what to include in the price. The boolean toppings that evaluate to true are added into priceForToppings.
     * @return totalPrice (derived from priceforCoffee + priceForToppings).
     */
    public double calculateTotalPrice(double baseCoffeePrice, boolean addChocolate, boolean addCinnamon, boolean addWhippedCream) {
        double priceForCoffee = quantity * baseCoffeePrice; //determine base coffee price
        double priceForToppings = 0.0; // initialize toppings to zero in case user doesn't want any toppings.

        if (addChocolate) { //if user wants chocolate
            priceForToppings += calculateItemPrice(quantity, priceForChocolate); // set priceForToppings = to the cost of chocolate times the quantity of coffees the chocolate will be placed on.
        }

        if (addCinnamon) {
            priceForToppings += calculateItemPrice(quantity, priceForCinnamon);
        }

        if (addWhippedCream) {
            priceForToppings += calculateItemPrice(quantity, priceForWhippedCream);
        }

        double totalPrice = priceForCoffee + priceForToppings; // add base price and toppings price to get total price

        return (totalPrice);
    }

    /**
     * This method makes it easy to calculate price of an individual item alone without having to calculate the total price.
     * It allows for easy generation of itemized receipt entries. For example, if you want a receipt entry for 3 chocolate toppings you can get
     * of just 3 chocolate toppings without having to calculate the entire order price.
     *
     * @param numberOfItems quantity of coffees, toppings, etc
     * @param pricePerItem  price per individual item ($3 per coffee, or $1 per topping, etc)
     */
    public double calculateItemPrice(int numberOfItems, double pricePerItem) {
        return numberOfItems * pricePerItem;
    }

    /**
     * Used by increment and decrement button to display text to textViewQuantity.
     */
    public void setQuantity(int number) {
        textViewQuantity.setText("" + number);
    }

    /**
     * Converts a double to appropriate locale with 2 trailing decimal points to represent cents. In USD it is more common to see "Total: $3.50" rather than "Total: $3.5".
     *
     * @param number is the double to convert.
     */
    public String convertLocale(double number) {
        Locale currentLocale = Locale.getDefault(); //get the current locale and save it for later use

        switch (currentLocale.toString()) { //if locale is....
            case "en_AU": //english
            case "en_CA":
            case "en_ES":
            default:
                try {
                    DecimalFormat decFormatter = new DecimalFormat("#,###.00"); // formatting like so: 1,234.00 -- each # represents any valid number, where "00" represents trailing zeros *IF* there is no decimal value > 0.

                    DecimalFormatSymbols symbols = new DecimalFormatSymbols(); // create symbols object so we can set decimal/grouping separators.
                    symbols.setDecimalSeparator('.');
                    symbols.setGroupingSeparator(',');

                    decFormatter.setDecimalFormatSymbols(symbols);
                    String strFormattedNumber = decFormatter.format(number); //save to a string before formatting. Some locales would show 1,234.56 as "1 234,56" which is not a valid double value.

                    Log.i("JustJava en_US", "Number is: " + strFormattedNumber);

                    return strFormattedNumber; // returns the newly formatted number

                } catch (Exception ex) {
                    Log.e("JustJava en_US", "Error: " + ex.getMessage());
                }

            case "es_ES":
                try {
                    DecimalFormat decFormatter = new DecimalFormat("#,###.00"); // formatting like so: 1,234.00 -- each # represents any valid number, where "00" represents trailing zeros *IF* there is no decimal value > 0.

                    DecimalFormatSymbols symbols = new DecimalFormatSymbols(); // create symbols object so we can set decimal/grouping separators.
                    symbols.setDecimalSeparator(',');
                    symbols.setGroupingSeparator('.');

                    decFormatter.setDecimalFormatSymbols(symbols);
                    String strFormattedNumber = decFormatter.format(number); //save to a string before formatting. Some locales would show 1,234.56 as "1 234,56" which is not a valid double value.

                    Log.i("JustJava es_ES", "Number is: " + strFormattedNumber);

                    return strFormattedNumber; // returns the newly formatted number

                } catch (Exception ex) {
                    Log.e("JustJava es_ES", "Error: " + ex.getMessage());
                }

        }
        return null;
    }


    /**
     * Quantity picker -- increments or decrements global variable quantity.
     */
    public void adjustQuantity(View view) {
        int selectedNumber = Integer.parseInt(textViewQuantity.getText().toString());

        switch (view.getId()) {
            case R.id.decrementButton:

                if (selectedNumber == 1) {
                    quantity = 0; // execute this code at 1, but set quantity equal to zero since it is currently 1 and decrement was pressed.
                    buttonDecrement.setEnabled(false);
                    setQuantity(quantity);
                } else {
                    quantity--;
                    setQuantity(quantity);
                    buttonIncrement.setEnabled(true);
                }
                break;

            case R.id.incrementButton:

                if (selectedNumber == 99) {
                    quantity = 100;
                    buttonIncrement.setEnabled(false);
                    setQuantity(quantity);

                    Toast.makeText(getApplicationContext(), R.string.cannotExceed100Message,
                            Toast.LENGTH_SHORT).show();
                } else {
                    quantity++;
                    setQuantity(quantity);
                    buttonDecrement.setEnabled(true);

                    break;
                }

        }
    }

    public void composeEmail(String subjectOfEmail, String bodyOfEmail) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_SUBJECT, subjectOfEmail);
        intent.putExtra(Intent.EXTRA_TEXT, bodyOfEmail);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

}