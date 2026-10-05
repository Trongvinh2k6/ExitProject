import React, { useState, useRef, useEffect } from 'react';
import { sendChatMessage } from '../services/chatService';

const Chatbot = () => {
    const [isOpen, setIsOpen] = useState(false);
    const [messages, setMessages] = useState([
        { sender: 'bot', text: 'Xin chào! Mình là tư vấn viên ProjectSkateBoard. Bạn cần tìm mẫu giày nào?' }
    ]);
    const [input, setInput] = useState('');
    const [loading, setLoading] = useState(false);
    const chatEndRef = useRef(null);

    // Tự động cuộn xuống tin nhắn mới nhất
    useEffect(() => {
        chatEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    }, [messages, loading]);

    const handleSend = async (e) => {
        e.preventDefault();
        if (!input.trim() || loading) return;

        const userText = input;
        setInput('');
        setMessages((prev) => [...prev, { sender: 'user', text: userText }]);
        setLoading(true);

        try {
            const data = await sendChatMessage(userText);
            setMessages((prev) => [
                ...prev,
                { sender: 'bot', text: data.reply, products: data.products }
            ]);
        } catch (error) {
            setMessages((prev) => [
                ...prev,
                { sender: 'bot', text: 'Rất tiếc, đã có lỗi xảy ra khi kết nối. Vui lòng thử lại sau!' }
            ]);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="fixed bottom-6 right-6 z-50">
            {/* Cửa sổ Chat */}
            {isOpen && (
                <div className="w-80 sm:w-96 h-[500px] bg-white rounded-2xl shadow-2xl border border-gray-200 flex flex-col mb-4 overflow-hidden animate-in fade-in slide-in-from-bottom-5">
                    {/* Header */}
                    <div className="bg-black text-white p-4 flex justify-between items-center">
                        <div className="flex items-center gap-2">
                            <span className="w-3 h-3 bg-green-500 rounded-full inline-block"></span>
                            <h3 className="font-bold text-sm tracking-wide">ProjectSkateBoard AI</h3>
                        </div>
                        <button 
                            onClick={() => setIsOpen(false)}
                            className="text-gray-400 hover:text-white text-xl font-bold focus:outline-none"
                        >
                            ✕
                        </button>
                    </div>

                    {/* Nội dung tin nhắn */}
                    <div className="flex-1 p-4 overflow-y-auto space-y-3 bg-gray-50 text-sm">
                        {messages.map((msg, idx) => (
                            <div key={idx} className={`flex flex-col ${msg.sender === 'user' ? 'items-end' : 'items-start'}`}>
                                <div className={`max-w-[85%] p-3 rounded-2xl ${
                                    msg.sender === 'user' 
                                        ? 'bg-black text-white rounded-br-none' 
                                        : 'bg-white text-gray-800 border border-gray-200 rounded-bl-none shadow-sm'
                                }`}>
                                    <p className="whitespace-pre-line">{msg.text}</p>
                                </div>

                                {/* Render danh sách sản phẩm gợi ý nếu có */}
                                {msg.products && msg.products.length > 0 && (
                                    <div className="mt-2 w-full space-y-2">
                                        {msg.products.map((prod, pIdx) => (
                                            <div key={pIdx} className="p-2 bg-white rounded-lg border border-gray-200 flex justify-between items-center text-xs shadow-sm">
                                                <span className="font-semibold text-gray-800 truncate max-w-[160px]">{prod.name}</span>
                                                <span className="text-black font-bold">{prod.price?.toLocaleString()} đ</span>
                                            </div>
                                        ))}
                                    </div>
                                )}
                            </div>
                        ))}
                        {loading && (
                            <div className="flex items-center gap-2 text-gray-500 text-xs italic">
                                <span>Bot đang suy nghĩ...</span>
                            </div>
                        )}
                        <div ref={chatEndRef} />
                    </div>

                    {/* Input nhập tin nhắn */}
                    <form onSubmit={handleSend} className="p-3 bg-white border-t border-gray-200 flex gap-2">
                        <input
                            type="text"
                            placeholder="Hỏi về sản phẩm, giá..."
                            value={input}
                            onChange={(e) => setInput(e.target.value)}
                            className="flex-1 px-3 py-2 border rounded-xl text-sm focus:outline-none focus:border-black"
                        />
                        <button
                            type="submit"
                            disabled={loading}
                            className="bg-black text-white px-4 py-2 rounded-xl text-sm font-semibold hover:bg-gray-800 transition disabled:opacity-50"
                        >
                            Gửi
                        </button>
                    </form>
                </div>
            )}

            {/* Nút Nổi Toggle Chat */}
            <button
                onClick={() => setIsOpen(!isOpen)}
                className="w-14 h-14 bg-black text-white rounded-full shadow-lg flex items-center justify-center text-2xl hover:scale-105 transition-transform duration-200 focus:outline-none"
            >
                💬
            </button>
        </div>
    );
};

export default Chatbot;