/**
 * IMPORTANT: Make sure you are using the correct package name.
 * This example uses the package name:
 * package com.example.android.justjava
 * If you get an error when copying this code into Android studio, update it to match teh package name found
 * in the project's AndroidManifest.xml file.
 **/

package com.example.android.justjava;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

//This app displays an order form to order coffee.
public class MainActivity extends AppCompatActivity {
    public int quantity = 0;
    public TextView textViewQuantity;
    public TextView textViewOrderSummaryDetails;
    public TextView textViewOrderSummary;
    public Button buttonDecrement;
    public CheckBox checkBoxWhippedCream;
    public CheckBox checkBoxChocolate;
    public EditText editTextName;
    public CheckBox checkBoxCinnamon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextName = (EditText) findViewById(R.id.editTextName);
        textViewQuantity = (TextView) findViewById(R.id.quantity_text_view);
        textViewOrderSummary = (TextView) findViewById(R.id.textViewOrderSummary);
        textViewOrderSummaryDetails = (TextView) findViewById(R.id.textViewOrderSummaryDetails);
        buttonDecrement = (Button) findViewById(R.id.decrementButton);
        checkBoxWhippedCream = (CheckBox) findViewById(R.id.checkBoxWhippedCream);
        checkBoxChocolate = (CheckBox) findViewById(R.id.checkBoxChocolate);
        checkBoxCinnamon = (CheckBox) findViewById(R.id.checkBoxCinnamon);

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
     * It verifies quantity is greater than zero, and then creates local variables that store the price per coffee, and the price
     * for the amount of coffees the user wants to purchase. It then checks if any toppings are selected and then passes this
     * information into the createOrderSummary method to generate an itemized receipt. After the receipt has been generated it will
     * be shown at the bottom of the screen.
     */
    public void submitOrder(View view) {
        if (quantity == 0) {
            textViewOrderSummary.setVisibility(View.GONE);
            textViewOrderSummaryDetails.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), R.string.textViewOrderSummaryDetails,
                    Toast.LENGTH_SHORT).show();
        } else if (quantity > 0) {
            double pricePerCoffee = getPrice("coffee"); //base price for coffee
            double price = calculatePrice(this.quantity, pricePerCoffee); //calculates quantity of coffees times base price per coffee.
            boolean whippedCream = checkBoxWhippedCream.isChecked(); // is the whipped cream checkbox checked?
            boolean chocolate = checkBoxChocolate.isChecked(); // is the chocolate checkbox checked?
            boolean cinnamon = checkBoxCinnamon.isChecked(); //is the cinnamon checkbox checked?

            displayMessage(createOrderSummary(price, whippedCream, chocolate, cinnamon)); //creates a receipt and displays to the user

            textViewOrderSummary.setVisibility(View.VISIBLE);
            textViewOrderSummaryDetails.setVisibility(View.VISIBLE);
        }

    }

    /**
     * Create summary of the order.
     *
     * @param price           of the order
     * @param addWhippedCream is whether or not the user wants whipped cream topping
     * @param addChocolate    is whether or not the user wants chocolate topping
     * @param addCinnamon     is whether or not the user wants cinnamon topping
     * @return text summary
     */
    public String createOrderSummary(double price, boolean addWhippedCream, boolean addChocolate, boolean addCinnamon) {
        String nameForOrder = editTextName.getText().toString();

        String inclChocolateOnReceipt = "";
        String inclCinnamonOnReceipt = "";
        String inclWhippedCreamOnReceipt = "";

        double basePrice = price;
        double priceForChocolate = calculatePrice(quantity, getPrice("chocolate"));
        double priceForCinnamon = calculatePrice(quantity, getPrice("cinnamon"));
        double priceForWhippedCream = calculatePrice(quantity, getPrice("whipped cream"));


        if (addChocolate) {
            priceForChocolate = convertDoubleToTwoDecimalPlaces(priceForChocolate);
            price += priceForChocolate;
            inclChocolateOnReceipt = "\n+$" + priceForChocolate + " Chocolate";
        }

        if (addCinnamon) {
            priceForCinnamon = convertDoubleToTwoDecimalPlaces(priceForCinnamon);
            price += priceForCinnamon;
            inclCinnamonOnReceipt = "\n+$" + priceForCinnamon + " Cinnamon";
        }

        if (addWhippedCream) {
            priceForWhippedCream = convertDoubleToTwoDecimalPlaces(priceForWhippedCream);
            price += priceForWhippedCream;
            inclWhippedCreamOnReceipt = "\n+$" + priceForWhippedCream + " W/Cream";
        }

        String priceMessage = "Name: " + nameForOrder +
                "\n" +
                "$" + basePrice + " Base Price" +
                inclChocolateOnReceipt +
                inclCinnamonOnReceipt +
                inclWhippedCreamOnReceipt +
                "\n" +
                "\nTotal: $" + String.format("%.2f", convertDoubleToTwoDecimalPlaces(price)) + //format to incl trailing zero. For example, "Total 27.4" vs "27.40".
                "\nThank You!";
        return priceMessage;
    }

    /**
     * Display the user's selected quantity to the screen.
     *
     * @param number is the number to convert to two decimal places.
     */
    public double convertDoubleToTwoDecimalPlaces(double number) {
        DecimalFormat df = new DecimalFormat("###.##");
        number = Double.parseDouble(df.format(number));
        return number;
    }


    /**
     * Print message to textViewOrderSummaryDetails (the order summary)
     *
     * @param message the text to be printed .
     */
    public void displayMessage(String message) {
        textViewOrderSummaryDetails.setText(message);
    }

    /**
     * Calculates the cost of multiple coffees or toppings.
     *
     * @param numberOfItems is the amount of items.
     * @param pricePerItem  is the price per individual item.
     * @return price for total items multiplied by the price of each item.
     */
    public double calculatePrice(int numberOfItems, double pricePerItem) {
        return numberOfItems * pricePerItem;
    }

    /**
     * Display the user's selected quantity to the screen.
     *
     * @param number is the amount the user selected via the adjustQuantity method.
     */
    public void setQuantity(int number) {
        textViewQuantity.setText("" + number);
    }


    /**
     * Quantity picker -- increments or decrements global variable quantity.
     */
    public void adjustQuantity(View view) {
        switch (view.getId()) {
            case R.id.decrementButton:
                int selectedNumber = Integer.parseInt(textViewQuantity.getText().toString());

                if (selectedNumber == 1) {
                    quantity = 0;
                    buttonDecrement.setEnabled(false);
                    setQuantity(quantity);
                } else {
                    quantity--;
                    setQuantity(quantity);
                }
                break;

            case R.id.incrementButton:
                quantity++;
                setQuantity(quantity);
                buttonDecrement.setEnabled(true);
                break;
        }
    }


}