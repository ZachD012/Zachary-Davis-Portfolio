import java.util.ArrayList;

public class Cart
{
    private ArrayList<CartItem> shoppingCart;
    
    public Cart()
    {
        this.shoppingCart = new ArrayList<CartItem>();
    }

    public void addToCart(CartItem item)
    {
        shoppingCart.add(item);
    }
    //Unknown why it does not work
    public void removeFromCart(Product product)
    {
        for(CartItem item : shoppingCart)
        {
            Product itemProduct = item.getProduct();
            if(product.equals(item))
            {
                shoppingCart.remove(item);
            }
        }
    }

    public ArrayList<CartItem> getShoppingCart()
    {
        return shoppingCart;
    }

    public void printCart()
    {
        for(CartItem item : shoppingCart)
        {
            item.print();
        }
    }
}
