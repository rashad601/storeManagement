
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class StoreManagement {

	private static String[] columnNames;
	private static ArrayList<Item> itemList = new ArrayList<>();
	private static String database_filename = "stock.csv";
	private static ArrayList<CartItem> shoppingBasket = new ArrayList();

	private static Set categoryList = new HashSet();
	private static String output_filename = "file_receipt.txt";

	public static void main(String[] args) {
		try {
			getStock();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		showModes();
	}

//    read from 'stock.txt' and populate arrary list
	private static void getStock() throws IOException {
		File csvFile = new File(database_filename);
		String line;
		if (csvFile.isFile()) {
			BufferedReader buffReader = new BufferedReader(new FileReader(csvFile));
			int count = 0;
			while ((line = buffReader.readLine()) != null) {
				String[] data = line.split(",");
				if (count == 0) {
					columnNames = new String[data.length];
					for (int i = 0; i < data.length; i++) {
						columnNames[i] = data[i];
					}
				} else {
					Item i = new Item();
					i.setId(data[0]);
					i.setName(data[1]);
					i.setDescription(data[2]);
					i.setCategory(data[3]);
					i.setQuantity(Integer.parseInt(data[4]));
					i.setPrice(Double.parseDouble(data[5]));
					itemList.add(i);
				}
				count++;
			}
			buffReader.close();
		} else {
			System.err.println("file not found");
			System.exit(0);
		}
	}

	// prints current list of items
	private static void printItemList() {
		System.out.println("");
		System.out.println("***********Items List***********");
		String formatSpaces = "";

		System.out.print("| ");
		for (int i = 0; i < columnNames.length; i++) {
			formatSpaces = "%20s|";
			System.out.format(formatSpaces, columnNames[i]);
		}
		System.out.println("");

		for (int i = 0; i < itemList.size(); i++) {
			System.out.print("|");
			formatSpaces = "%20s|%20s|%20s|%20s|%20s|%20s|";
			System.out.format(formatSpaces, itemList.get(i).getId(), itemList.get(i).getName(),
					itemList.get(i).getDescription(), itemList.get(i).getCategory(), itemList.get(i).getQuantity(),
					itemList.get(i).getPrice());
			System.out.println("");
		}
		System.out.println("");
	}

	// print available modes of operation
	private static void showModes() {
		System.out.println("***********Select Mode***********" + "\n1. Inventory Management" + "\n2. Sales"
				+ "\n*********************************");
		Scanner input = new Scanner(System.in);
		int menuOption = input.nextInt();
		switch (menuOption) {
		case 1:
			showInventoryMenu();
			break;
		case 2:
			showSalesMenu();
			break;
		default:
			System.err.println("Invalid option");
			showModes();
		}
	}

	// print inventory menu
	private static void showInventoryMenu() {
		System.out.println("***********Inventory Menu***********" + "\n1. Restock" + "\n2. Out of Stock"
				+ "\n3. Add new item" + "\n4. Save and Exit " + "\n***********************************");

		Scanner input = new Scanner(System.in);
		int menuOption = input.nextInt();
		switch (menuOption) {
		case 1:
			restockItems();
			showInventoryMenu();
			break;
		case 2:
			outOfStockItems();
			showInventoryMenu();
			break;
		case 3:
			addNewItem();
			showInventoryMenu();
			break;
		case 4:
			saveAndExit();
			break;
		default:
			System.err.println("Invalid option");
			showInventoryMenu();
		}
	}

	// restock the items
	private static void restockItems() {
		printItemList();
		Scanner in = new Scanner(System.in);
		System.out.println("Enter item id: ");
		String itemId = in.nextLine();
		int qty = 0;

		for (int i = 0; i < itemList.size(); i++) {
			if (itemList.get(i).getId().equals(itemId)) {
				Item item = itemList.get(i);
				System.out.println("Enter number of new items ");
				qty = in.nextInt();
				int oldQty = item.getQuantity();
				item.setQuantity(oldQty + qty);
				System.out.println("Updated qty: " + item.getQuantity());
				return;
			}
		}
		System.err.println("Invalid item ID");
	}

	// add new item into stock
	private static void addNewItem() {
		System.out.println("************************Add new item************************");
		Scanner in = new Scanner(System.in);
		System.out.println("Enter item id: ");
		String itemId = in.nextLine();
		for (int i = 0; i < itemList.size(); i++) {
			if (itemList.get(i).getId().equals(itemId)) {
				System.err.println("Already contains " + itemId);
				addNewItem();
				return;
			}
		}
		System.out.println("Enter item name: ");
		String itemName = in.nextLine();
		System.out.println("Enter item description: ");
		String description = in.nextLine();
		System.out.println("Enter item category: ");
		String category = in.nextLine();
		System.out.println("Enter item price: ");
		double price = in.nextDouble();
		boolean status = true;
		if (price < 0 && price > 1000) {
			while (status) {
				System.out.println("Invalid price, please enter values 0-1000");
				System.out.println("Enter item price: ");
				price = in.nextDouble();
				if (price > 0 && price < 1000) {
					status = false;
				}
			}
		}
		// set quantity 0 as default
		int quantity = 0;

		Item p = new Item();
		p.setId(itemId);
		p.setName(itemName);
		p.setDescription(description);
		p.setCategory(category);
		p.setPrice(price);
		p.setQuantity(quantity);
		itemList.add(p);
	}

	// print out of stock items
	private static void outOfStockItems() {
		System.out.println("");
		System.out.println("************************Out of Stock Items List************************");
		String formatSpaces = "";

		System.out.print("| ");
		for (int i = 0; i < columnNames.length; i++) {
			formatSpaces = "%20s|";
			System.out.format(formatSpaces, columnNames[i]);
		}
		System.out.println("");

		for (int i = 0; i < itemList.size(); i++) {
			if (itemList.get(i).getQuantity() == 0) {
				System.out.print("|");
				formatSpaces = "%20s|%20s|%20s|%20s|%20s|%20s|";
				System.out.format(formatSpaces, itemList.get(i).getId(), itemList.get(i).getName(),
						itemList.get(i).getDescription(), itemList.get(i).getCategory(), itemList.get(i).getQuantity(),
						itemList.get(i).getPrice());
				System.out.println("");
			}
		}
		System.out.println("");

		Scanner in = new Scanner(System.in);
		in.nextLine();
	}

	// save stock and exit application
	private static void saveAndExit() {
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(database_filename);
			for (int i = 0; i < columnNames.length; i++) {
				fileWriter.append(columnNames[i]);
				if (i != (columnNames.length - 1)) {
					fileWriter.append(",");
				}
			}
			fileWriter.append("\n");

			for (int i = 0; i < itemList.size(); i++) {
				fileWriter.append(itemList.get(i).getId());
				fileWriter.append(",");
				fileWriter.append(itemList.get(i).getName());
				fileWriter.append(",");
				fileWriter.append(itemList.get(i).getDescription());
				fileWriter.append(",");
				fileWriter.append(itemList.get(i).getCategory());
				fileWriter.append(",");
				fileWriter.append(String.valueOf(itemList.get(i).getQuantity()));
				fileWriter.append(",");
				fileWriter.append(String.valueOf(itemList.get(i).getPrice()));
				fileWriter.append(",");
				fileWriter.append("\n");
			}
			fileWriter.flush();
			fileWriter.close();
			System.exit(0);
		} catch (IOException ex) {
			System.err.println("Invalid file");
		} finally {
			try {
				fileWriter.close();
			} catch (IOException ex) {
				System.err.println("Unexpected error occured");
			}
		}
	}

	// print sales menu
	private static void showSalesMenu() {
		printCategoryList();
		try {
			Scanner in = new Scanner(System.in);
			System.out.println("\nEnter category number: ");
			int catId = in.nextInt();

			if (catId == 0) {
				System.out.println("Checkout");
				checkoutOrder();
				return;
			}
//            System.out.println(" category  size " + categoryList.size() + " enterered value=" + catId);
			boolean status = true;
			if (catId <= categoryList.size() && catId > 0) {
				status = false;
			} else {
				while (status) {
					System.err.println("Invalid category");
					System.out.println("Enter category number: ");
					catId = in.nextInt();
					if (catId <= (categoryList.size()) && catId > 0) {
						status = false;
					}
				}
			}
			listItemsByCategory(catId);
		} catch (Exception ex) {
//            ex.printStackTrace();
			System.err.println("Invalid option");
			showSalesMenu();
		}
	}

	// prints available categories
	private static void printCategoryList() {
		System.out.println("***********Choose Category***********");
		for (int i = 0; i < itemList.size(); i++) {
			categoryList.add(itemList.get(i).getCategory());
		}
		List list = Arrays.asList(categoryList.toArray());
		for (int i = 0; i < list.size(); i++) {
			System.out.println((i + 1) + ". " + list.get(i));
		}
		System.out.println("0. Checkout");
	}

	// list items and add items to cart
	private static void listItemsByCategory(int catId) {
		HashMap<Integer, Item> temp = new HashMap<Integer, Item>();
		List list = Arrays.asList(categoryList.toArray());
		String userItem = (String) list.get(catId - 1);
		System.out.println("************************Choose " + userItem + "************************");
		String formatSpaces = "%20s%20s%20s";
		System.out.format(formatSpaces, "Sl.No.", "Item", "Price");
		System.out.println("");
		int count = 1;
		for (int i = 0; i < itemList.size(); i++) {
			if (itemList.get(i).getCategory().equals(userItem)) {
				if (itemList.get(i).getQuantity() > 0) {
					System.out.format(formatSpaces, count, itemList.get(i).getName(), itemList.get(i).getPrice());
					System.out.println("");
					temp.put(count, itemList.get(i));
					count++;
				}
			}
		}
		System.out.println("");
		System.out.format(formatSpaces, "0", "Back", "");
		System.out.println("");

		System.out.println("Select item: ");
		Scanner in = new Scanner(System.in);
		int option = in.nextInt();

		if (option == 0) {
			showSalesMenu();
		}
		// if user selects wrong menu
		if (option > temp.size()) {
			while (true) {
				System.err.println("Invalid option, Select item again: ");
				option = in.nextInt();
				if (option <= temp.size()) {
					break;
				}
			}
		}

		System.out.println("Enter quantity required:");
		Scanner inn = new Scanner(System.in);
		int qty = inn.nextInt();

		// if user asks for more than the stock
		int oldQty = temp.get(option).getQuantity();
		if (qty > oldQty) {
			boolean status = true;
			while (status) {
				System.err.println("Sorry, only " + oldQty + " available. Please enter quantity again!");
				qty = inn.nextInt();
				if (qty <= oldQty) {
					status = false;
				}
			}
		}

		// update current lists with new qty
		itemList.get(itemList.indexOf(temp.get(option))).setQuantity(oldQty - qty);
		temp.get(option).setQuantity(oldQty - qty);

		// add to cart
		CartItem c = new CartItem();
		c.setItem(temp.get(option));
		c.setQuantity(qty);
		shoppingBasket.add(c);

		System.out.println(qty + " x " + temp.get(option).getName() + " added to basket");
		listItemsByCategory(catId);
	}

	// save and print the order
	private static void checkoutOrder() throws FileNotFoundException {
		printBill();
		writeToFile();
		saveAndExit();
	}

	// save the receipt to file
	private static void writeToFile() throws FileNotFoundException {
		PrintStream fileStream = new PrintStream(output_filename);
		System.setOut(fileStream);
		printBill();
	}

	// print result in the receipt design
	private static void printBill() {
		LocalDateTime datetime = LocalDateTime.now();
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");

		System.out.println("");
		System.out.println("***********************************Bill Receipt***********************************");
		System.out.println("Date: " + datetime.format(dateFormat));
		System.out.println("Time: " + datetime.format(timeFormat));
		System.out.println("");
		String formatSpaces = "%20s%20s%20s%20s";
		System.out.format(formatSpaces, "Item", "Unit Price", "Qty", "Cost");
		System.out.print("\n----------------------------------------------------------------------------------\n");

		double total = 0;
		for (int i = 0; i < shoppingBasket.size(); i++) {
			double cost = shoppingBasket.get(i).getQuantity() * shoppingBasket.get(i).getItem().getPrice();
			System.out.format(formatSpaces, shoppingBasket.get(i).getItem().getName(),
					shoppingBasket.get(i).getItem().getPrice(), shoppingBasket.get(i).getQuantity(), cost);
			System.out.println("");
			total = total + cost;
		}
		System.out.print("\n___________________________________________________________________________________\n");

		System.out.format(formatSpaces, "Total", "", "", "" + "" + total);
		System.out.println("\n");
		System.out.println("                              Thank you. Visit again!                               ");
		System.out.println("\n**********************************************************************************");
	}
}
