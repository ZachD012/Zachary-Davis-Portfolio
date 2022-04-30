public class CartItem extends Product
{
    private Product product;
    private String productOptions;
    private int quantity;

    public CartItem(Product product, String productOptions, int quantity)
    {
        this.product = product;
        this.productOptions = productOptions;
        this.quantity = quantity;
    }

    public Product getProduct()
    {
        return product;
    }

    public void setProductOptions(String productOption)
    {
        this.productOptions = productOption;
    }

    public String getProductOptions()
    {
        return productOptions;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public void setQuantity(int quantity)
    {
        this.quantity = quantity;
    }

    public void print()
    {
        product.print();
    }
}