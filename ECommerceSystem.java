import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.Scanner;

/*
 * Models a simple ECommerce system. Keeps track of products for sale, registered customers, product orders and
 * orders that have been shipped to a customer
 */
public class ECommerceSystem
{
	ArrayList<Product>  products = new ArrayList<Product>();
	ArrayList<Customer> customers = new ArrayList<Customer>();	

	ArrayList<ProductOrder> orders   			= new ArrayList<ProductOrder>();
	ArrayList<ProductOrder> shippedOrders = new ArrayList<ProductOrder>();

	// These variables are used to generate order numbers, customer id's, product id's 
	int orderNumber = 500;
	int customerId = 900;
	int productId = 700;

	// General variable used to store an error message when something is invalid (e.g. customer id does not exist)  

	// Random number generator
	Random random = new Random();

	public ECommerceSystem()
	{
		
		// NOTE: do not modify or add to these objects!! - the TAs will use for testing
		// If you do the class Shoes bonus, you may add shoe products

		// Create some products
		try{
			readProducts("products.txt");
		}catch(FileNotFoundException e){
			System.out.println(e.getMessage());
		}
		
		
		
		int[][] stockCounts = {{4, 2}, {3, 5}, {1, 4,}, {2, 3}, {4, 2}};
		products.add(new Shoes("Prada", generateProductId(), 595.0, stockCounts));

		// Create some customers
		customers.add(new Customer(generateCustomerId(),"Inigo Montoya", "1 SwordMaker Lane, Florin"));
		customers.add(new Customer(generateCustomerId(),"Prince Humperdinck", "The Castle, Florin"));
		customers.add(new Customer(generateCustomerId(),"Andy Dufresne", "Shawshank Prison, Maine"));
		customers.add(new Customer(generateCustomerId(),"Ferris Bueller", "4160 Country Club Drive, Long Beach"));
	}

	private ArrayList<Product> readProducts(String filename) throws FileNotFoundException
	{
		File file = new File(filename);
		Scanner in = new Scanner(file);
		boolean done = false;
		boolean passed = false;
		while(!done)
		{
			if(!done)
			{
				passed = createProducts(in);
			}
			if(!passed)
			{
				done = true;
			}
		}
		in.close();
		return products;
	}

	private boolean createProducts(Scanner in)
	{
		if(!in.hasNextLine())
		{
			return false;
		}
		// Line 1
		String cat = in.nextLine();
		Product.Category category = null;
		if(cat.equals("CLOTHING"))
		{
			category = Product.Category.CLOTHING;
		}
		else if(cat.equals("COMPUTERS"))
		{
			category = Product.Category.COMPUTERS;
		}
		else if(cat.equals("BOOKS"))
		{
			category = Product.Category.BOOKS;
		}
		else if(cat.equals("GENERAL"))
		{
			category = Product.Category.GENERAL;
		}
		else if(cat.equals("FURNITURE"))
		{
			category = Product.Category.FURNITURE;
		}
		//Line 2
		String name = in.nextLine();
		//Line 3
		double price = Double.parseDouble(in.nextLine());
		//Line 4
		int stock = 0;
		int paperbackStock = 0;
		int hardcoverStock = 0;
		if(category == Product.Category.BOOKS)
		{
			paperbackStock = in.nextInt();
			hardcoverStock = in.nextInt();
			in.nextLine();
		}
		else
		{
			stock = Integer.parseInt(in.nextLine());
		}
		//Line 5
		if(category == Product.Category.BOOKS)
		{
			String line = in.nextLine();
			ArrayList<String> bookList = delimitColon(line);
			String title  = bookList.get(0);
			String author = bookList.get(1);
			int year = Integer.parseInt(bookList.get(2));
			products.add(new Book(name, generateProductId(), price, paperbackStock, hardcoverStock, title, author, year));
			return true;
		}
		else
		{
			products.add(new Product(name, generateProductId(), price, stock, category));
			in.nextLine();
			return true;
		}
		
	}
	private ArrayList<String> delimitColon(String line)
	{
		Scanner scan = new Scanner(line);
		scan.useDelimiter(":");
		ArrayList<String> bookList = new ArrayList<String>();
		while(scan.hasNext())
		{
			bookList.add(scan.next());
		}
		scan.close();
		return bookList;
	}

	private String generateOrderNumber()
	{
		return "" + orderNumber++;
	}

	private String generateCustomerId()
	{
		return "" + customerId++;
	}

	private String generateProductId()
	{
		return "" + productId++;
	}

	public void printAllProducts()
	{
		for (Product p : products)
			p.print();
	}

	public void printAllBooks()
	{
		for (Product p : products)
		{
			if (p.getCategory() == Product.Category.BOOKS)
				p.print();
		}
	}

	public ArrayList<Book> booksByAuthor(String author)
	{
		ArrayList<Book> books = new ArrayList<Book>();
		for (Product p : products)
		{
			if (p.getCategory() == Product.Category.BOOKS)
			{
				Book book = (Book) p;
				if (book.getAuthor().equals(author))
					books.add(book);
			}
		}
		return books;
	}

	public void printAllOrders()
	{
		for (ProductOrder o : orders)
			o.print();
	}

	public void printAllShippedOrders()
	{
		for (ProductOrder o : shippedOrders)
			o.print();
	}

	public void printCustomers()
	{
		for (Customer c : customers)
			c.print();
	}
	/*
	 * Given a customer id, print all the current orders and shipped orders for them (if any)
	 */
	public void printOrderHistory(String customerId)
	{
		// Make sure customer exists
		int index = customers.indexOf(new Customer(customerId));
		if (index == -1)
		{
			throw new UnknownCustomerException("Customer " + customerId + " not found");
		}	
		System.out.println("Current Orders of Customer " + customerId);
		for (ProductOrder order: orders)
		{
			if (order.getCustomer().getId().equals(customerId))
				order.print();
		}
		System.out.println("\nShipped Orders of Customer " + customerId);
		for (ProductOrder order: shippedOrders)
		{
			if (order.getCustomer().getId().equals(customerId))
				order.print();
		}
	}

	public String orderProduct(String productId, String customerId, String productOptions)
	{
		// Get customer
		int index = customers.indexOf(new Customer(customerId));
		if (index == -1)
		{
			throw new UnknownCustomerException("Customer " + customerId + " not found");
		}
		Customer customer = customers.get(index);

		// Get product 
		index = products.indexOf(new Product(productId));
		if (index == -1)
		{
			throw new UnknownProductException("Product "+ productId + " not found");
		}
		Product product = products.get(index);

		// Check if the options are valid for this product (e.g. Paperback or Hardcover or EBook for Book product)
		if (!product.validOptions(productOptions))
		{
			throw new InvalidProductOptionsException("Product " + product.getName() + " ProductId " + productId + " Invalid Options: " + productOptions);
		}
		// Is it in stock?
		if (product.getStockCount(productOptions) == 0)
		{
			throw new ProductOutOfStockException("Product " + product.getName() + " ProductId " + productId + " Out of Stock");
		}
		// Create a ProductOrder
		ProductOrder order = new ProductOrder(generateOrderNumber(), product, customer, productOptions);
		product.reduceStockCount(productOptions);

		// Add to orders and return
		orders.add(order);

		return order.getOrderNumber();
	}

	/*
	 * Create a new Customer object and add it to the list of customers
	 */

	public void createCustomer(String name, String address)
	{
		// Check to ensure name is valid
		if (name == null || name.equals(""))
		{
			throw new InvalidCustomerNameException("Invalid Customer Name " + name);
		}
		// Check to ensure address is valid
		if (address == null || address.equals(""))
		{
			throw new InvalidCustomerAddressException("Invalid Customer Address " + address);
		}
		Customer customer = new Customer(generateCustomerId(), name, address);
		customers.add(customer);
	}

	public ProductOrder shipOrder(String orderNumber)
	{
		// Check if order number exists
		int index = orders.indexOf(new ProductOrder(orderNumber,null,null,""));
		if (index == -1)
		{
			throw new InvalidOrderNumberException("Order " + orderNumber + " Not Found");
		}
		ProductOrder order = orders.get(index);
		orders.remove(index);
		shippedOrders.add(order);
		return order;
	}

	/*
	 * Cancel a specific order based on order number
	 */
	public void cancelOrder(String orderNumber)
	{
		// Check if order number exists
		int index = orders.indexOf(new ProductOrder(orderNumber,null,null,""));
		if (index == -1)
		{
			throw new InvalidOrderNumberException("Order " + orderNumber + " Not Found");
		}
		ProductOrder order = orders.get(index);
		orders.remove(index);
	}

	// Sort products by increasing price
	public void sortByPrice()
	{
		Collections.sort(products, new PriceComparator());
	}

	private class PriceComparator implements Comparator<Product>
	{
		public int compare(Product a, Product b)
		{
			if (a.getPrice() > b.getPrice()) return 1;
			if (a.getPrice() < b.getPrice()) return -1;	
			return 0;
		}
	}

	// Sort products alphabetically by product name
	public void sortByName()
	{
		Collections.sort(products, new NameComparator());
	}

	private class NameComparator implements Comparator<Product>
	{
		public int compare(Product a, Product b)
		{
			return a.getName().compareTo(b.getName());
		}
	}

	// Sort products alphabetically by product name
	public void sortCustomersByName()
	{
		Collections.sort(customers);
	}

	public String addToCart(String productId, String customerId, String productOptions)
	{
		int index = customers.indexOf(new Customer(customerId));
		if (index == -1)
		{
			throw new UnknownCustomerException("Customer " + customerId + " Not Found");
		}
		Customer customer = customers.get(index);

		index = products.indexOf(new Product(productId));
		if (index == -1)
		{
			throw new UnknownProductException("Product " + productId + " Not Found");
		}
		Product product = products.get(index);

		if (!product.validOptions(productOptions))
		{
			throw new InvalidProductOptionsException("Product " + product.getName() + " ProductId " + productId + " Invalid Options: " + productOptions);
		}

		CartItem item = new CartItem(product, productOptions, 1);
		customer.getCart().addToCart(item);
		return "Succesfully added item to cart!";
	}

	public void printCart(String customerId)
	{
		int index = customers.indexOf(new Customer(customerId));
		if (index == -1)
		{
			throw new UnknownCustomerException("Customer " + customerId + " Not Found");
		}
		Customer customer = customers.get(index);

		customer.getCart().printCart();
	}

	public String removeFromCart(String customerId, String productId)
	{
		int index = customers.indexOf(new Customer(customerId));
		if (index == -1)
		{
			throw new UnknownCustomerException("Customer " + customerId + " Not Found");
		}
		Customer customer = customers.get(index);

		index = products.indexOf(new Product(productId));
		if (index == -1)
		{
			throw new UnknownProductException("Product " + productId + " Not Found");
		}
		Product product = products.get(index);

		Cart custCart = customer.getCart();
		custCart.removeFromCart(product);
		return productId + "successfully removed from cart";
	}

	public String orderItems(String customerId)
	{
		int index = customers.indexOf(new Customer(customerId));
		if (index == -1)
		{
			throw new UnknownCustomerException("Customer " + customerId + " Not Found");
		}
		Customer customer = customers.get(index);

		Cart custCart = customer.getCart();
		ArrayList<CartItem> cartList = custCart.getShoppingCart();
		for(CartItem item : cartList)
		{	
			Product product = item.getProduct();
			ProductOrder order = new ProductOrder(generateOrderNumber(), product, customer, null);
			product.reduceStockCount(null);
			orders.add(order);
			System.out.println("Order #"+order.getOrderNumber());
		}
		custCart.getShoppingCart().clear();
		return "Ordered all items in cart successfully!";
	}
}

class UnknownCustomerException extends RuntimeException
{
	public UnknownCustomerException() {}
	public UnknownCustomerException(String message)
	{
		super(message);
	}
}

class UnknownProductException extends RuntimeException
{
	public UnknownProductException() {}
	public UnknownProductException(String message)
	{
		super(message);
	}
}

class InvalidProductOptionsException extends RuntimeException
{
	public InvalidProductOptionsException() {}
	public InvalidProductOptionsException(String message)
	{
		super(message);
	}
}

class ProductOutOfStockException extends RuntimeException
{
	public ProductOutOfStockException() {}
	public ProductOutOfStockException(String message)
	{
		super(message);
	}
}

class InvalidCustomerNameException extends RuntimeException
{
	public InvalidCustomerNameException() {}
	public InvalidCustomerNameException(String message)
	{
		super(message);
	}
}

class InvalidCustomerAddressException extends RuntimeException
{
	public InvalidCustomerAddressException() {}
	public InvalidCustomerAddressException(String message)
	{
		super(message);
	}
}

class InvalidOrderNumberException extends RuntimeException
{
	public InvalidOrderNumberException() {}
	public InvalidOrderNumberException(String message)
	{
		super(message);
	}
}