function CartItem({ item }) {
  return (
    <li style={{ marginBottom: 6 }}>
      {item.name} - ${item.price}
    </li>
  );
}
export default CartItem;
