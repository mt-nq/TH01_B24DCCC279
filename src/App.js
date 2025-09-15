import { useState } from "react";
import TodoItem from "./themcongviec/TodoItem";
import DoiMau from "./doimaunen/DoiMau";
import CartItem from "./giohang/GioHang";
import Post from "./quanlyluotthich/Post";
import Quiz from "./cauhoi/Cauhoi";

function Main() {
  // Bài 1: Ứng dụng To-do List
  const [task, setTask] = useState("");
  const [tasks, setTasks] = useState([]);

  const handleAdd = () => {
    if (task.trim() !== "") {
      setTasks([...tasks, task]);
      setTask("");
    }
  };

  const handleDelete = (index) => {
    const newTasks = tasks.filter((_, i) => i !== index);
    setTasks(newTasks);
  };
  //bài 2: Đổi màu
  const [color, setColor] = useState("white");
  const colors = ["red", "blue", "yellow"];

  // Bài 3: Ứng dụng Giỏ hàng
  const [cart, setCart] = useState([]);

  const addItems = (item) => {
    setCart([...cart, item]);
  };
  const items = [{ name: "Iphone 14", price: 2000 }, { name: "Samsung S23", price: 1500 }, { name: "Xiaomi 13", price: 1000 }];
  const total = cart.reduce((sum, item) => sum + item.price, 0);
  // Bài 4: Like/Dislike Post

  const posts = ["Học ReactJS có khó không?,", "Bạn có thích lập trình không?", "Bạn có muốn học thêm ngôn ngữ mới không?"];
  // Bài 5: Quiz App

  const QUESTIONS = [
  {
    text: "ReactJS dùng để làm gì?",
    options: ["Mobile App", "Web UI", "Hệ điều hành", "Cơ sở dữ liệu"],
    correct: 1,
  },
  {
    text: "State trong React là gì?",
    options: ["Thư viện CSS", "Dữ liệu động của component", "Router", "Hook useApi"],
    correct: 1,
  },
  {
    text: "Props dùng để?",
    options: ["Truyền dữ liệu từ cha xuống con", "Gọi API", "Lưu trữ cục bộ", "Quản lý route"],
    correct: 0,
  }

  
];
  return (
    <div>
      <h3>Bài 1: To-do List</h3>
      <input
        type="text"
        placeholder="Nhập công việc"
        value={task}
        onChange={(e) => setTask(e.target.value)}
      />
      <button onClick={handleAdd}>Thêm</button>

      <ul>
        {tasks.map((item, index) => (
          <TodoItem
            key={index}
            item={item}
            onDelete={() => handleDelete(index)}
          />
        ))}
      </ul>

      <hr />

      <h3>Bài 2: Color Picker</h3>
        {colors.map((c) => (
        <button
          key={c}
          onClick={() => setColor(c)}
          style={{ marginRight: 5 }}
        >
          {c}
        </button>
      ))}
      <DoiMau color={color} />

      <hr />

      <h3>Bài 3: Giỏ hàng</h3>
      <h4> Sản phẩm </h4>
      <ul>
        {items.map((item, index) => (
          <li key={index} style={{ marginBottom: 6 }}>
            {item.name} - ${item.price}{" "}
            <button onClick={() => addItems(item)}>Thêm vào giỏ</button>
          </li>
        ))}
      </ul>

      <h4>Giỏ hàng</h4>
      <ul>
        {cart.map((item, index) => (
          <CartItem key={index} item={item} />
        ))}
      </ul>

      <p>
        <strong>Tổng tiền: ${total}</strong>
      </p>

      <hr/>

      <h3>Bài 4: Like/Dislike Post</h3>
      <div style={{ display: "grid", gap: 16 }}>
        {posts.map((t, i) => (
          <Post key={i} text={t} />
        ))}
      </div>

      <hr/>

      <h3>Quiz App</h3>
      <Quiz questions={QUESTIONS} />
    </div>

  );
}
export default Main;