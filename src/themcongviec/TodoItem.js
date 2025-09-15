function TodoItem({ item, onDelete }) {
  return (
    <li>
      {item} <button type="button" onClick={onDelete}>Xóa</button>
    </li>
  );
}
export default TodoItem;