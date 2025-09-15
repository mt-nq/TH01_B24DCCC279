import { useState } from "react";

function Post({ text }) {
  const [likes, setLikes] = useState(0);
  const [dislikes, setDislikes] = useState(0);

  const handleLike = () => {
    setLikes(likes + 1); 
  };

  const handleDislike = () => {
    setDislikes(dislikes + 1); 
  };

  return (
    <div style={{ border: "1px solid #ccc", padding: 16, borderRadius: 6 }}>
      <p style={{ marginTop: 0 }}>{text}</p>
      <div style={{ display: "flex", gap: 8 }}>
        <button onClick={handleLike}>
          👍 <span>{likes}</span>
        </button>
        <button onClick={handleDislike}>
          👎 <span>{dislikes}</span>
        </button>
      </div>
    </div>
  );
}
export default Post;
