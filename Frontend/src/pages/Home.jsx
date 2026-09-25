import { Link } from "react-router-dom";

function Home() {

    return (

        <div className="home">

            <section className="hero">

                <div className="hero-content">

                    <p className="hero-small">
                        STEP INTO YOUR STYLE
                    </p>

                    <h1>
                        FIND YOUR<br />
                        PERFECT SHOES
                    </h1>

                    <p>
                        Discover our collection of sneakers
                        designed for your everyday style.
                    </p>

                    <Link
                        to="/products"
                        className="shop-btn"
                    >
                        SHOP NOW
                    </Link>

                </div>

                <div className="hero-shoe">
                    👟
                </div>

            </section>


            <section className="features">

                <div>
                    <h3>Free Shipping</h3>
                    <p>For orders over 1.000.000 ₫</p>
                </div>

                <div>
                    <h3>Easy Returns</h3>
                    <p>30-day return policy</p>
                </div>

                <div>
                    <h3>Secure Payment</h3>
                    <p>Your payment is protected</p>
                </div>

            </section>

        </div>

    );
}

export default Home;