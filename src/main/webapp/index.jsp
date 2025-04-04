<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.User" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <link rel="icon" type="image/icon" href="./indexCJI/Images/favicon.PNG">
    <meta charset="UTF-8">
    <title>Online Grocery Order Management System</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css" integrity="sha512-Evv84Mr4kqVGRNSgIGL/F/aIDqQb7xQ2vcrdIwxfjThSH8CSR7PBEakCr51Ck+w+/U6swU2Im1vVX0SVk9ABhg==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.css" />
    <link rel="stylesheet" href="css/index.css">
</head>
<body style="
background: rgb(255,255,255);
background: radial-gradient(circle, rgba(255,255,255,1) 0%, rgba(244,255,240,1) 100%);
">
<header class="Header">
    <a href="#" class="logo"><i class="fa-solid fa-basket-shopping"></i> Grocery</a>
    <nav class="navbar">
        <a href="#home">Home</a>
        <a href="#features">Features</a>
        <a href="#Deals">Deals</a>
        <a href="#categories">Categories</a>
    </nav>
    <div class="icons">
        <div class="fa-solid fa-bars" id="menu-btn"></div>
        <%
            User loggedInUser = (User) session.getAttribute("user");
            if (loggedInUser != null) {
        %>
        <a href="${pageContext.request.contextPath}/UserProfileServlet" class="icon-link">
            <i class="fa-solid fa-user profile-icon"></i>
        </a>
        <a href="${pageContext.request.contextPath}/LogoutServlet" class="icon-link">
            <i class="fa-solid fa-sign-out-alt logout-icon"></i>
        </a>
        <% } else { %>
        <a href="${pageContext.request.contextPath}/userLogin/login.jsp" class="icon-link">
            <i class="fa-solid fa-user login-icon"></i>
        </a>
        <% } %>
    </div>
</header>

<!-- Home Banners -->
<section class="Banner" id="Banner">
    <div class="swiper home-slider">
        <div class="swiper-wrapper">
            <!-- Slide 1 -->
            <div class="swiper-slide slide" style="background: url('https://essstr.blob.core.windows.net/uiimg/Carousel/FreshVegetables/FreshVegetablesWebBanner.png') no-repeat center/cover;">
                <div class="content">
                    <h3>Fresh <span>Vegetables</span></h3>
                    <p>Discover the best organic produce for your healthy lifestyle.</p>
                    <a href="${pageContext.request.contextPath}/CartServlet?category=Produce" class="btn">Shop Now</a>
                </div>
            </div>
            <!-- Slide 2 -->
            <div class="swiper-slide slide" style="background: url('https://essstr.blob.core.windows.net/uiimg/Carousel/slide1.jpg') no-repeat center/cover;">
                <div class="content">
                    <h3>Exclusive <span>Deals</span></h3>
                    <p>Save big on your favorite grocery items this week!</p>
<%--                    <a href="${pageContext.request.contextPath}/CartServlet?category=Deals" class="btn">Shop Now</a>--%>
                    <a href="#categories" class="btn">Shop Now</a>
                </div>
            </div>

            <!-- Slide 3 -->
            <div class="swiper-slide slide" style="background: url('https://t3.ftcdn.net/jpg/06/14/08/90/360_F_614089075_9zP2Ybcr5fwsnHCzGsPNLLkpThUru9Zq.jpg') no-repeat center/cover;">
                <div class="content">
                    <h3>Fresh <span>Proteins</span> Products</h3>
                    <p>Save big on your favorite grocery items this week!</p>
                    <a href="${pageContext.request.contextPath}/CartServlet?category=Proteins" class="btn">Shop Now</a>
                </div>
            </div>

            <!-- Slide 4 -->
            <div class="swiper-slide slide" style="background: url('https://media.istockphoto.com/id/1471438213/photo/dairy-products-bottles-of-milk-cheese-cottage-cheese-yogurt-butter-on-meadow-of-cows.jpg?s=612x612&w=0&k=20&c=hIUSgarP7-7h1KDF4AuPzzMNCPbJ5h5ofPF30G0rGhc=') no-repeat center/cover;">
                <div class="content">
                    <h3>Fresh <span>Dairy</span> Products</h3>
                    <p>Save big on your favorite grocery items this week!</p>
                    <a href="${pageContext.request.contextPath}/CartServlet?category=Dairy" class="btn">Shop Now</a>
                </div>
            </div>

            <!-- Slide 5 -->
            <div class="swiper-slide slide" style="background: url('https://t4.ftcdn.net/jpg/06/27/46/27/360_F_627462785_DyaFl6hi7cAmpmB4obBFewgFrM6A488N.jpg') no-repeat center/cover;">
                <div class="content">
                    <h3>Fresh <span>Bakery</span> Products</h3>
                    <p>Save big on your favorite grocery items this week!</p>
                    <a href="${pageContext.request.contextPath}/CartServlet?category=Bakery" class="btn">Shop Now</a>
                </div>
            </div>

            <!-- Slide 6 -->
            <div class="swiper-slide slide" style="background: url('https://media.istockphoto.com/id/1227210244/photo/flat-lay-view-at-kitchen-table-full-with-non-perishable-foods-spase-for-text.jpg?s=612x612&w=0&k=20&c=yoKYTbSTaHdBtRjgOUsDYBSB_0B10QxrR6lKH_36Hps=') no-repeat center/cover;">
                <div class="content">
                    <h3>Fresh <span>Pantry</span> Products</h3>
                    <p>Save big on your favorite grocery items this week!</p>
                    <a href="${pageContext.request.contextPath}/CartServlet?category=Pantry" class="btn">Shop Now</a>

                </div>
            </div>

            <!-- Slide 7 -->
            <div class="swiper-slide slide" style="background: url('https://as1.ftcdn.net/jpg/03/68/66/94/1000_F_368669476_Cl7gGRuBWRYnPLwwY8pBgmeH1lGvpQ1r.jpg') no-repeat center/cover;">
                <div class="content">
                    <h3>Tasty <span>Snacks</span></h3>
                    <p>Save big on your favorite grocery items this week!</p>
                  <a href="${pageContext.request.contextPath}/CartServlet?category=Snacks" class="btn">Shop Now</a>
                </div>
            </div>


        </div>
        <!-- Pagination -->
        <div class="swiper-pagination"></div>
        <!-- Navigation buttons -->
        <div class="swiper-button-next"></div>
        <div class="swiper-button-prev"></div>
    </div>
</section>

<section class="features" id="features">
    <h1 class="heading">Our <span>Features</span></h1>
    <div class="box-container">
        <div class="box">
            <img src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQwMUypuoJ7jxCE6Ltc7o0v9mHbWFLyHj55VA&s">
            <h3>Fresh And Organic</h3>
            <p>"Savor the crisp, nutrient-rich taste of our fresh, organic vegetables, grown naturally for your health and the planet."</p>
            <a href="#" class="btn">Read More</a>
        </div>
        <div class="box">
            <img src="https://png.pngtree.com/png-clipart/20230211/original/pngtree-free-delivery-truck-icon-png-image_8951758.png">
            <h3>Free Delivery</h3>
            <p>"Savor the crisp, nutrient-rich taste of our fresh, organic vegetables, grown naturally for your health and the planet."</p>
            <a href="#" class="btn">Read More</a>
        </div>
        <div class="box">
            <img src="https://img.freepik.com/free-vector/hands-holding-credit-card-mobile-phone-with-banking-app-person-paying-with-bank-card-transferring-money-shopping-online-flat-vector-illustration-payment-finance-concept_74855-24760.jpg">
            <h3>Easy Payment</h3>
            <p>"Savor the crisp, nutrient-rich taste of our fresh, organic vegetables, grown naturally for your health and the planet."</p>
            <a href="#" class="btn">Read More</a>
        </div>
    </div>
</section>

<section class="Deals" id="Deals">
    <h1 class="heading">New <span>Deals</span></h1>
    <div class="swiper product-slider">
        <div class="swiper-wrapper">
            <div class="swiper-slide box">
                <img src="./indexCJI/Images/product-1.png">
                <h1>Fresh Orange</h1>
                <div class="price">$4.99/- - $8.99/-</div>
                <div class="starts">
                    <i class="fa-solid fa-star"></i>
                    <i class="fa-solid fa-star"></i>
                    <i class="fa-solid fa-star"></i>
                    <i class="fa-solid fa-star"></i>
                    <i class="fa-solid fa-star-half-stroke"></i>
                </div>
            </div>
            <div class="swiper-slide box">
                <img src="./indexCJI/Images/product-2.png">
                <h1>Fresh Onion</h1>
                <div class="price">$2.99/- - $5.99/-</div>
                <div class="starts">
                    <i class="fa-solid fa-star"></i>
                    <i class="fa-solid fa-star"></i>
                    <i class="fa-solid fa-star"></i>
                    <i class="fa-solid fa-star"></i>
                    <i class="fa-solid fa-star-half-stroke"></i>
                </div>
            </div>
            <div class="swiper-slide box">
                <img src="./indexCJI/Images/product-3.png">
                <h1>Fresh Meat</h1>
                <div class="price">$9.99/- - $14.99/-</div>
                <div class="starts">
                    <i class="fa-solid fa-star"></i>
                    <i class="fa-solid fa-star"></i>
                    <i class="fa-solid fa-star"></i>
                    <i class="fa-solid fa-star"></i>
                    <i class="fa-solid fa-star-half-stroke"></i>
                </div>
            </div>
            <div class="swiper-slide box">
                <img src="./indexCJI/Images/product-4.png">
                <h1>Fresh Cabbage</h1>
                <div class="price">$3.99/- - $6.99/-</div>
                <div class="starts">
                    <i class="fa-solid fa-star"></i>
                    <i class="fa-solid fa-star"></i>
                    <i class="fa-solid fa-star"></i>
                    <i class="fa-solid fa-star"></i>
                    <i class="fa-solid fa-star-half-stroke"></i>
                </div>
            </div>
            <div class="swiper-slide box">
                <img src="./indexCJI/Images/product-5.png">
                <h1>Fresh Potato</h1>
                <div class="price">$2.99/- - $5.99/-</div>
                <div class="starts">
                    <i class="fa-solid fa-star"></i>
                    <i class="fa-solid fa-star"></i>
                    <i class="fa-solid fa-star"></i>
                    <i class="fa-solid fa-star"></i>
                    <i class="fa-solid fa-star-half-stroke"></i>
                </div>
            </div>
            <div class="swiper-slide box">
                <img src="./indexCJI/Images/product-6.png">
                <h1>Fresh Avocado</h1>
                <div class="price">$3.99/- - $4.99/-</div>
                <div class="starts">
                    <i class="fa-solid fa-star"></i>
                    <i class="fa-solid fa-star"></i>
                    <i class="fa-solid fa-star"></i>
                    <i class="fa-solid fa-star"></i>
                    <i class="fa-solid fa-star-half-stroke"></i>
                </div>
            </div>
            <div class="swiper-slide box">
                <img src="./indexCJI/Images/product-7.png">
                <h1>Fresh Carrot</h1>
                <div class="price">$3.99/- - $6.99/-</div>
                <div class="starts">
                    <i class="fa-solid fa-star"></i>
                    <i class="fa-solid fa-star"></i>
                    <i class="fa-solid fa-star"></i>
                    <i class="fa-solid fa-star"></i>
                    <i class="fa-solid fa-star-half-stroke"></i>
                </div>
            </div>
            <div class="swiper-slide box">
                <img src="./indexCJI/Images/product-8.png">
                <h1>Fresh Lemon</h1>
                <div class="price">$0.99/- - $2.99/-</div>
                <div class="starts">
                    <i class="fa-solid fa-star"></i>
                    <i class="fa-solid fa-star"></i>
                    <i class="fa-solid fa-star"></i>
                    <i class="fa-solid fa-star"></i>
                    <i class="fa-solid fa-star-half-stroke"></i>
                </div>
            </div>
        </div>
        <div class="swiper-pagination"></div>
    </div>
</section>

<section class="categories" id="categories">
    <h1 class="heading">Product <span>Category</span></h1>
    <div class="box-container">
        <div class="box">
            <img src="https://t4.ftcdn.net/jpg/00/51/27/87/360_F_51278711_lK9RGYczSSU1GXlIwjwyM4QzVmPfuLSV.jpg">
            <h3>Produce</h3>
            <p>Upto 15% Off</p>
            <a href="${pageContext.request.contextPath}/CartServlet?category=Produce" class="btn">Shop Now</a>
        </div>
        <div class="box">
            <img src="https://media.istockphoto.com/id/1185677996/photo/group-of-raw-seafood-isolated-on-white-background.jpg?s=612x612&w=0&k=20&c=dS10THUgufhCZPdO1Kn5LOhtpc2ZSlHXKmI9SdfXpoQ=">
            <h3>Proteins</h3>
            <p>Upto 15% Off</p>
            <a href="${pageContext.request.contextPath}/CartServlet?category=Proteins" class="btn">Shop Now</a>
        </div>
        <div class="box">
            <img src="https://img.freepik.com/premium-photo/different-fresh-dairy-products-isolated-white-background-isometry_660230-86599.jpg">
            <h3>Dairy</h3>
            <p>Upto 15% Off</p>
            <a href="${pageContext.request.contextPath}/CartServlet?category=Dairy" class="btn">Shop Now</a>
        </div>
        <div class="box">
            <img src="https://s1.1zoom.me/big0/970/Pastry_Buns_Bread_White_background_547126_1280x938.jpg">
            <h3>Bakery</h3>
            <p>Upto 15% Off</p>
            <a href="${pageContext.request.contextPath}/CartServlet?category=Bakery" class="btn">Shop Now</a>
        </div>
        <div class="box">
            <img src="https://media.istockphoto.com/id/504102317/photo/stack-of-different-sprouting-seeds-growing-in-a-glass-jar.jpg?s=612x612&w=0&k=20&c=_tG4Xl41hHrBBKo_sHDbZ3xYQRMebdUyiPb9txj_IfQ=">
            <h3>Pantry</h3>
            <p>Upto 15% Off</p>
            <a href="${pageContext.request.contextPath}/CartServlet?category=Pantry" class="btn">Shop Now</a>
        </div>
        <div class="box">
            <img src="https://img.freepik.com/premium-photo/assorted-american-junk-food-assortment-isolated-white-background-isometry_660230-86599.jpg">
            <h3>Snacks</h3>
            <p>Upto 15% Off</p>
            <a href="${pageContext.request.contextPath}/CartServlet?category=Snacks" class="btn">Shop Now</a>
        </div>
    </div>
</section>

<section class="home" id="home">
    <div class="content">
        <h3>Fresh and <span>Organic</span> Product For You</h3>
        <p>"Enjoy fresh, organic produce straight from local farms naturally grown, nutrient-rich, and delivered to your door for a healthy, sustainable lifestyle."</p>
        <a href="${pageContext.request.contextPath}/CartServlet?category=Produce" class="btn">Shop Now</a>
    </div>
</section>

<section class="footer" id="footer">
    <div class="box-container">
        <div class="box">
            <h3><i class="fa-solid fa-basket-shopping"></i> Grocery</h3>
            <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque pretium lacus porttitor placerat malesuada.</p>
            <div class="share">
                <a href="#" class="fa-brands fa-facebook-f"></a>
                <a href="#" class="fa-brands fa-x-twitter"></a>
                <a href="#" class="fa-brands fa-instagram"></a>
                <a href="#" class="fa-brands fa-linkedin-in"></a>
            </div>
        </div>
        <div class="box">
            <h3>Contact Info</h3>
            <a href="#" class="links"> <i class="fa-solid fa-phone"></i> +94 711641638</a>
            <a href="#" class="links"> <i class="fa-solid fa-phone"></i> +94 704563428</a>
            <a href="#" class="links"> <i class="fa-solid fa-envelope"></i> example@gmail.com</a>
            <a href="#" class="links"> <i class="fa-solid fa-location-dot"></i> Sliit, Sri Lanka - 10115</a>
        </div>
        <div class="box">
            <h3>Quick Links</h3>
            <a href="#" class="links"><i class="fa-solid fa-arrow-right"></i> Home</a>
            <a href="#" class="links"><i class="fa-solid fa-arrow-right"></i> Features</a>
            <a href="#" class="links"><i class="fa-solid fa-arrow-right"></i> Deals</a>
            <a href="#" class="links"><i class="fa-solid fa-arrow-right"></i> Categories</a>
        </div>
        <div class="box">
            <h3>News Letter</h3>
            <p>Subscribe For Latest Grocery Update</p>
            <input type="email" placeholder="Your Email" class="email">
            <input type="submit" value="Subscribe" class="btn">
            <img src="https://www.shutterstock.com/image-vector/rivne-ukraine-may-25-2023-260nw-2308151527.jpg" class="payment-img">
        </div>
    </div>
    <div class="credit">PGNO-<span>278</span> | All Rights Reserved</div>
</section>

<script src="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.js"></script>
<script src="js/index.js"></script>
</body>
</html>