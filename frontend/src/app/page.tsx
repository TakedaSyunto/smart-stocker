"use client";

import { useEffect, useState, useCallback } from "react";

interface Stock {
  id: number;
  itemName: string;
  quantity: number;
  minQuantity: number;
  estimatedOutDate: string | null;
}

export default function Home() {
  const [stocks, setStocks] = useState<Stock[]>([]);
  const [loading, setLoading] = useState(true);
  
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [newItemName, setNewItemName] = useState("");
  const [newQuantity, setNewQuantity] = useState(1);
  const [aiSuggestion, setAiSuggestion] = useState<string | null>(null);

  // 1. fetchStocks を useCallback で定義（警告回避のため）
  const fetchStocks = useCallback(async () => {
    setLoading(true);
    try {
      const res = await fetch("http://localhost:8080/api/stocks");
      const data = await res.json();
      setStocks(data.sort((a: Stock, b: Stock) => a.quantity - b.quantity));
    } catch (err) {
      console.error("Fetch error:", err);
    } finally {
      setLoading(false);
    }
  }, []);

  // 2. 初回読み込み
  useEffect(() => {
    fetchStocks();
  }, [fetchStocks]);

  const handleNameChange = async (name: string) => {
    setNewItemName(name);
    if (name.length >= 2) {
      try {
        const res = await fetch(`http://localhost:8080/api/stocks/predict?itemName=${encodeURIComponent(name)}`);
        const data = await res.json();
        setAiSuggestion(data.suggestion);
      } catch (err) {
        console.error("予測エラー:", err);
      }
    } else {
      setAiSuggestion(null);
    }
  };

  const handleAddStock = async () => {
    const response = await fetch("http://localhost:8080/api/stocks", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ itemName: newItemName, quantity: newQuantity, minQuantity: 2 }),
    });

    if (response.ok) {
      setIsModalOpen(false);
      setNewItemName("");
      setNewQuantity(1);
      setAiSuggestion(null);
      fetchStocks();
    }
  };

  return (
    <main className="min-h-screen bg-[#f8f9fa] p-4 md:p-8 text-gray-900">
      <div className="max-w-md mx-auto">
        <header className="flex justify-between items-center mb-8">
          <h1 className="text-2xl font-black tracking-tight">Smart Stocker<span className="text-blue-600">.</span></h1>
          <button 
            onClick={() => setIsModalOpen(true)}
            className="bg-blue-600 text-white px-6 py-2 rounded-full font-black shadow-lg active:scale-95 transition-all"
          >＋ 追加</button>
        </header>

        {loading ? (
          <div className="text-center py-10 font-bold text-gray-400 animate-pulse">在庫を照合中...</div>
        ) : (
          <div className="grid gap-4">
            {stocks.map((stock) => (
              <div key={stock.id} className="bg-white p-5 rounded-2xl shadow-md border-2 border-gray-100 transition-all hover:border-blue-200">
                <div className="flex justify-between items-start">
                  <div className="space-y-2">
                    <h2 className="font-black text-xl leading-tight text-gray-900">{stock.itemName}</h2>
                    
                    {/* 予測表示を「買い物リスト」風に強化 */}
                    <div className="flex items-center gap-2">
                      <span className="text-[10px] font-black px-1.5 py-0.5 bg-orange-500 text-white rounded uppercase">予測</span>
                      <p className="text-sm font-black text-gray-600">
                        {stock.estimatedOutDate 
                          ? `${stock.estimatedOutDate.replace(/-/g, "/")} 頃に空になります` 
                          : "ペース計算中..."}
                      </p>
                    </div>
                  </div>
                  
                  <div className="text-right">
                    <div className="flex items-baseline justify-end bg-gray-50 px-3 py-1 rounded-xl border border-gray-100">
                      <span className="text-3xl font-black text-blue-700">{stock.quantity}</span>
                      <span className="text-sm text-gray-500 ml-1 font-black">個</span>
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}

        {/* --- 登録モーダル --- */}
        {isModalOpen && (
          <div className="fixed inset-0 bg-black/70 backdrop-blur-md flex items-end sm:items-center justify-center p-4 z-50">
            <div className="bg-white w-full max-w-sm rounded-[32px] p-8 shadow-2xl border border-gray-200 animate-in fade-in slide-in-from-bottom-10">
              <h2 className="text-2xl font-black mb-8 text-gray-900">新しい在庫を追加</h2>
              
              <div className="space-y-6">
                <div>
                  <label className="text-xs font-black text-gray-400 mb-2 block ml-1 uppercase tracking-widest">商品名</label>
                  <input 
                    type="text" 
                    value={newItemName}
                    onChange={(e) => handleNameChange(e.target.value)}
                    placeholder="例: 洗剤、お米..."
                    className="w-full bg-gray-100 border-2 border-gray-300 text-gray-900 font-bold rounded-2xl p-4 focus:ring-4 focus:ring-blue-500/20 focus:border-blue-600 outline-none transition-all placeholder:text-gray-400"
                  />
                </div>

                {aiSuggestion && (
                  <div className="bg-blue-600 p-4 rounded-2xl shadow-lg shadow-blue-100 animate-in zoom-in-95">
                    <p className="text-sm text-white font-black leading-relaxed">✨ {aiSuggestion}</p>
                  </div>
                )}

                <div>
                  <label className="text-xs font-black text-gray-400 mb-2 block ml-1 uppercase tracking-widest">個数</label>
                  <div className="flex items-center gap-6 mt-1">
                    <button 
                      onClick={() => setNewQuantity(Math.max(1, newQuantity-1))} 
                      className="w-14 h-14 rounded-2xl bg-gray-200 text-gray-900 font-black text-2xl active:scale-90 transition-transform"
                    >-</button>
                    <span className="text-3xl font-black text-gray-900 w-8 text-center">{newQuantity}</span>
                    <button 
                      onClick={() => setNewQuantity(newQuantity+1)} 
                      className="w-14 h-14 rounded-2xl bg-gray-200 text-gray-900 font-black text-2xl active:scale-90 transition-transform"
                    >+</button>
                  </div>
                </div>

                <div className="flex gap-4 pt-6 border-t border-gray-50">
                  <button onClick={() => setIsModalOpen(false)} className="flex-1 py-4 font-black text-gray-400 hover:text-gray-600 transition-colors">閉じる</button>
                  <button onClick={handleAddStock} className="flex-[2] bg-blue-600 text-white py-4 rounded-2xl font-black text-lg shadow-xl shadow-blue-200 active:scale-95 transition-all">登録する</button>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </main>
  );
}