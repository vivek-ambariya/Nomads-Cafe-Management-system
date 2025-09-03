-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Aug 22, 2025 at 06:25 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `vivek`
--

-- --------------------------------------------------------

--
-- Table structure for table `menu_items`
--

CREATE TABLE `menu_items` (
  `id` int(11) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `category` varchar(50) DEFAULT NULL,
  `price` decimal(6,2) DEFAULT NULL,
  `availability` varchar(10) DEFAULT NULL,
  `popularity` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `menu_items`
--

INSERT INTO `menu_items` (`id`, `name`, `category`, `price`, `availability`, `popularity`) VALUES
(1, 'Samosa', 'Snacks', 140.00, '1', 7),
(2, 'Garlic Bread', 'Snacks', 154.40, '1', 4),
(3, 'Nachos', 'Snacks', 72.40, '1', 11),
(4, 'French Fries', 'Snacks', 82.40, '1', 5),
(5, 'Potato Wedges', 'Snacks', 95.00, '1', 0),
(6, 'Onion Rings', 'Snacks', 110.00, '1', 0),
(7, 'Paneer Tikka', 'Snacks', 160.00, '1', 10),
(8, 'Spring Rolls', 'Snacks', 120.00, '1', 0),
(9, 'Chicken Club', 'Sandwich', 154.73, '1', 0),
(10, 'Grilled Cheese', 'Sandwich', 155.48, '1', 0),
(11, 'Veggie Delight', 'Sandwich', 124.65, '1', 0),
(12, 'Tuna Melt', 'Sandwich', 71.96, '1', 0),
(13, 'BLT Sandwich', 'Sandwich', 145.00, '1', 0),
(14, 'Avocado Sandwich', 'Sandwich', 135.00, '1', 0),
(15, 'Turkey Club', 'Sandwich', 165.00, '1', 0),
(16, 'Egg Salad Sandwich', 'Sandwich', 125.00, '1', 0),
(17, 'Beetroot Juice', 'Juice', 64.11, '1', 0),
(18, 'Orange Juice', 'Juice', 168.95, '1', 0),
(19, 'Apple Juice', 'Juice', 53.84, '1', 5),
(20, 'Carrot Juice', 'Juice', 148.44, '1', 0),
(21, 'Pineapple Juice', 'Juice', 120.00, '1', 0),
(22, 'Watermelon Juice', 'Juice', 110.00, '1', 0),
(23, 'Cranberry Juice', 'Juice', 130.00, '1', 0),
(24, 'Mixed Fruit Juice', 'Juice', 140.00, '1', 0),
(25, 'Chocolate Shake', 'Milkshake', 176.73, '1', 0),
(26, 'Vanilla Shake', 'Milkshake', 176.07, '1', 0),
(27, 'Oreo Shake', 'Milkshake', 151.39, '1', 0),
(28, 'Strawberry Shake', 'Milkshake', 69.37, '1', 0),
(29, 'Butterscotch Shake', 'Milkshake', 160.00, '1', 0),
(30, 'Cookie Dough Shake', 'Milkshake', 170.00, '1', 0),
(31, 'Peanut Butter Shake', 'Milkshake', 165.00, '1', 0),
(32, 'Mint Chocolate Shake', 'Milkshake', 155.00, '1', 0),
(33, 'Mango Tango', 'Smoothie', 93.97, '1', 0),
(34, 'Banana Peanut', 'Smoothie', 131.43, '1', 0),
(35, 'Green Detox', 'Smoothie', 157.51, '1', 0),
(36, 'Berry Blast', 'Smoothie', 162.85, '1', 0),
(37, 'Tropical Paradise', 'Smoothie', 140.00, '1', 0),
(38, 'Peach Melba', 'Smoothie', 135.00, '1', 0),
(39, 'Pina Colada', 'Smoothie', 150.00, '1', 2),
(40, 'Acai Berry', 'Smoothie', 170.00, '1', 0),
(41, 'Chocolate Lava Cake', 'Dessert', 115.43, '1', 1),
(42, 'Brownie', 'Dessert', 101.11, '1', 1),
(43, 'Cheesecake', 'Dessert', 107.93, '1', 0),
(44, 'Tiramisu', 'Dessert', 171.96, '1', 0),
(45, 'Gulab Jamun', 'Dessert', 90.00, '1', 0),
(46, 'Apple Pie', 'Dessert', 120.00, '1', 0),
(47, 'Ice Cream Sundae', 'Dessert', 140.00, '1', 0),
(48, 'Chocolate Mousse', 'Dessert', 130.00, '1', 0),
(49, 'Omelette', 'Breakfast', 104.08, '1', 0),
(50, 'Pancakes', 'Breakfast', 42.79, '1', 0),
(51, 'Paratha', 'Breakfast', 109.05, '1', 0),
(52, 'Avocado Toast', 'Breakfast', 117.74, '1', 0),
(53, 'Waffles', 'Breakfast', 125.00, '1', 0),
(54, 'French Toast', 'Breakfast', 115.00, '1', 0),
(55, 'Bagel with Cream Cheese', 'Breakfast', 95.00, '1', 0),
(56, 'Eggs Benedict', 'Breakfast', 150.00, '1', 0),
(57, 'Espresso', 'Hot Coffee', 90.00, '1', 0),
(58, 'Cappuccino', 'Hot Coffee', 130.00, '1', 0),
(59, 'Caffè Latte', 'Hot Coffee', 140.00, '1', 0),
(60, 'Americano', 'Hot Coffee', 120.00, '1', 2),
(61, 'Mocha', 'Hot Coffee', 150.00, '1', 0),
(62, 'Flat White', 'Hot Coffee', 135.00, '1', 0),
(63, 'Macchiato', 'Hot Coffee', 125.00, '1', 0),
(64, 'Double Shot Espresso', 'Hot Coffee', 110.00, '1', 0),
(65, 'Affogato', 'Hot Coffee', 160.00, '1', 0),
(66, 'Turkish Coffee', 'Hot Coffee', 150.00, '1', 0),
(67, 'Irish Coffee', 'Hot Coffee', 190.00, '1', 0),
(68, 'Vietnamese Coffee', 'Hot Coffee', 140.00, '1', 0),
(69, 'Cold Brew', 'Cold Coffee', 150.00, '1', 2),
(70, 'Iced Latte', 'Cold Coffee', 145.00, '1', 0),
(71, 'Frappe', 'Cold Coffee', 180.00, '1', 0),
(72, 'Iced Mocha', 'Cold Coffee', 155.00, '1', 0),
(73, 'Iced Americano', 'Cold Coffee', 130.00, '1', 0),
(74, 'Vanilla Frappe', 'Cold Coffee', 185.00, '1', 0),
(75, 'Hazelnut Cold Brew', 'Cold Coffee', 160.00, '1', 0),
(76, 'Iced Caramel Macchiato', 'Cold Coffee', 180.00, '1', 0),
(77, 'Coffee Float', 'Cold Coffee', 175.00, '1', 0),
(78, 'Dalgona Coffee', 'Cold Coffee', 165.00, '1', 0),
(79, 'Nitro Cold Brew', 'Cold Coffee', 170.00, '1', 0),
(80, 'Coconut Cold Brew', 'Cold Coffee', 160.00, '1', 0),
(81, 'Masala Chai', 'Tea & Infusions', 70.00, '1', 6),
(82, 'Green Tea', 'Tea & Infusions', 90.00, '1', 3),
(83, 'Herbal Infusion', 'Tea & Infusions', 100.00, '1', 2),
(84, 'Iced Lemon Tea', 'Tea & Infusions', 110.00, '1', 3),
(85, 'Assam Black Tea', 'Tea & Infusions', 85.00, '1', 0),
(86, 'Ginger Lemon Tea', 'Tea & Infusions', 100.00, '1', 0),
(87, 'Chamomile Tea', 'Tea & Infusions', 95.00, '1', 0),
(88, 'Jasmine Green Tea', 'Tea & Infusions', 105.00, '1', 0),
(89, 'Rose Milk Tea', 'Tea & Infusions', 110.00, '1', 5),
(90, 'Earl Grey', 'Tea & Infusions', 95.00, '1', 2),
(91, 'Peppermint Tea', 'Tea & Infusions', 90.00, '1', 0),
(92, 'Hibiscus Tea', 'Tea & Infusions', 100.00, '1', 1),
(93, 'Blueberry Muffin', 'Bakery', 70.00, '1', 0),
(94, 'Chocolate Croissant', 'Bakery', 80.00, '1', 0),
(95, 'Chocolate Muffin', 'Bakery', 90.00, '1', 0),
(96, 'Croissant', 'Bakery', 100.00, '1', 0),
(97, 'Banana Bread', 'Bakery', 80.00, '1', 0),
(98, 'Red Velvet Cake', 'Bakery', 150.00, '1', 0),
(99, 'Almond Croissant', 'Bakery', 120.00, '1', 0),
(100, 'Paneer Tikka Platter', 'Starters', 220.00, '1', 1),
(101, 'Hara Bhara Kabab', 'Starters', 180.00, '1', 0),
(102, 'Mushroom Tikka', 'Starters', 200.00, '1', 0),
(103, 'Dahi Ke Kabab', 'Starters', 190.00, '1', 1),
(104, 'Vegetable Seekh Kabab', 'Starters', 170.00, '1', 0),
(105, 'Cheese Corn Balls', 'Starters', 210.00, '1', 1),
(106, 'Stuffed Mushrooms', 'Starters', 230.00, '1', 0),
(107, 'Crispy Spinach', 'Starters', 160.00, '1', 0),
(108, 'Paneer Butter Masala', 'Main Course', 250.00, '1', 0),
(109, 'Dal Makhani', 'Main Course', 220.00, '1', 0),
(110, 'Malai Kofta', 'Main Course', 240.00, '1', 0),
(111, 'Shahi Paneer', 'Main Course', 260.00, '1', 0),
(112, 'Mixed Vegetable Curry', 'Main Course', 210.00, '1', 0),
(113, 'Kadai Paneer', 'Main Course', 240.00, '1', 0),
(114, 'Navratan Korma', 'Main Course', 270.00, '1', 0),
(115, 'Palak Paneer', 'Main Course', 230.00, '1', 0),
(116, 'Vegetable Chettinad', 'Main Course', 230.00, '1', 0),
(117, 'Avial', 'Main Course', 210.00, '1', 0),
(118, 'Paneer Ghee Roast', 'Main Course', 250.00, '1', 0),
(119, 'Vegetable Stew', 'Main Course', 200.00, '1', 0),
(120, 'Kerala Style Vegetable Curry', 'Main Course', 220.00, '1', 0),
(121, 'Bendakaya Pulusu', 'Main Course', 190.00, '1', 0),
(122, 'Mushroom Pepper Fry', 'Main Course', 240.00, '1', 0),
(123, 'Vegetable Kootu', 'Main Course', 200.00, '1', 0),
(124, 'Garlic Naan', 'Breads', 80.00, '1', 0),
(125, 'Butter Naan', 'Breads', 70.00, '1', 0),
(126, 'Lachha Paratha', 'Breads', 90.00, '1', 0),
(127, 'Tandoori Roti', 'Breads', 60.00, '1', 0),
(128, 'Missi Roti', 'Breads', 70.00, '1', 0),
(129, 'Stuffed Kulcha', 'Breads', 100.00, '1', 0),
(130, 'Roomali Roti', 'Breads', 50.00, '1', 0),
(131, 'Cheese Naan', 'Breads', 120.00, '1', 0),
(132, 'Vegetable Biryani', 'Rice Dishes', 220.00, '1', 0),
(133, 'Paneer Biryani', 'Rice Dishes', 240.00, '1', 2),
(134, 'Jeera Rice', 'Rice Dishes', 150.00, '1', 0),
(135, 'Vegetable Pulao', 'Rice Dishes', 180.00, '1', 0),
(136, 'Kashmiri Pulao', 'Rice Dishes', 200.00, '1', 0),
(137, 'Lemon Rice', 'Rice Dishes', 160.00, '1', 0),
(138, 'Curd Rice', 'Rice Dishes', 140.00, '1', 0),
(139, 'Tomato Rice', 'Rice Dishes', 170.00, '1', 0),
(140, 'Rasmalai', 'Dessert', 120.00, '1', 2),
(141, 'Kheer', 'Dessert', 100.00, '1', 0),
(142, 'Gajar Ka Halwa', 'Dessert', 130.00, '1', 0),
(143, 'Rasgulla', 'Dessert', 90.00, '1', 0),
(144, 'Jalebi', 'Dessert', 80.00, '1', 1),
(145, 'Shahi Tukda', 'Dessert', 150.00, '1', 0),
(146, 'Badam Halwa', 'Dessert', 160.00, '1', 0),
(147, 'Moong Dal Halwa', 'Dessert', 140.00, '1', 0),
(148, 'Masala Chaas', 'Beverages', 70.00, '1', 0),
(149, 'Aam Panna', 'Beverages', 90.00, '1', 0),
(150, 'Rose Lassi', 'Beverages', 100.00, '1', 0);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `menu_items`
--
ALTER TABLE `menu_items`
  ADD PRIMARY KEY (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
