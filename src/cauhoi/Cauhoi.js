import { useEffect, useRef, useState } from "react";
export default function Quiz({ questions }) {
  const [idx, setIdx] = useState(0);
  const [score, setScore] = useState(0);
  const [picked, setPicked] = useState(null); 
  const [done, setDone] = useState(false);
  const timerRef = useRef(null);

  const q = questions[idx];

  useEffect(() => () => clearTimeout(timerRef.current), []);

  const pick = (i) => {
    if (picked !== null || done) return;      
    setPicked(i);
    const isCorrect = i === q.correct;
    if (isCorrect) setScore((s) => s + 1);

    timerRef.current = setTimeout(() => {
      if (idx + 1 < questions.length) {
        setIdx((x) => x + 1);
        setPicked(null);
      } else {
        setDone(true);
      }
    }, 500);
  };

  if (done) {
    return (
      <div>
        <h3>Kết quả</h3>
        <p>Bạn đúng <b>{score}</b>/<b>{questions.length}</b> câu.</p>
      </div>
    );
  }

  return (
    <div>
      <p style={{ marginBottom: 8 }}>
        Câu {idx + 1}/{questions.length} • Điểm: <b>{score}</b>
      </p>
      <p style={{ fontSize: 18 }}>{q.text}</p>
      <div style={{ display: "flex", gap: 8, flexWrap: "wrap" }}>
        {q.options.map((opt, i) => (
          <button key={i} onClick={() => pick(i)} disabled={picked !== null}>
            {opt}
          </button>
        ))}
      </div>

      {picked !== null && (
        <p style={{ marginTop: 10, fontWeight: 600, color: picked === q.correct ? "green" : "crimson" }}>
          {picked === q.correct ? "✅ Chính xác!" : `❌ Sai. Đáp án đúng: ${q.options[q.correct]}`}
        </p>
      )}
    </div>
  );
}
