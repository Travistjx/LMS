# LMS
<li><a href = "#about">About</a></li>
<li><a href = "#project-status">Project Status</a></li>
<li><a href = "#documentation">Documentation</a></li>
<li><a href = "#installation--setup">Installation</a></li>
<li><a href = "#reflection">Reflection</a></li>

# ABOUT
The Library Management System (LMS) is a web application designed to efficiently manage books in a library setting. Built with Java Spring Boot, Thymeleaf, HTML, and CSS, this system provides a user-friendly interface for librarians and library staff to handle book inventory, user accounts, and borrowing transactions, as well as for the regular users or library patrons to browse the book catalog, manage their loans, fines and payments.

# PROJECT STATUS
This project is currently in development. 

# DOCUMENTATION
<a href = "https://github.com/Travistjx/LMS/blob/main/Documentation/User%20Requirements">User Manual</a> <br/>
<a href = "https://github.com/Travistjx/LMS/blob/main/Documentation/User%20Man.pdf">User Requirements</a>

# INSTALLATION / SETUP


# REFLECTION
This personal project took me around 4 months to complete. I wanted to build a library management system that fulfills the basic needs, which is for administrators to manage the book inventory, user accounts, the ongoing loans, fines and payments, and for regular library patrons to be able to browse books, manage loans, fines and payment. The main goal was to use this project to learn Spring Boot using the Controller-Service-Repository structure. 

Honestly, the project took me longer than I expected, mainly due to real life commitments. It was also more tedious than I thought, as there were a lot of functions involved and I had to learn from scratch. I also believed I should have made the project more compact and focus mainly on some functions, like for instance, just managing the books itself instead of trying to complete a total library management system on my own. But once I was halfway through, I decided to do the whole package.

One obstacle I faced when doing the project was actually the search filter which is used when managing books, accounts, loans, fines and payments. There were multiple layers of filter, namely the query, the sort order, the sort by, the search by, and the status filter. There were also some conflicts with pagination, which added an addtional layer in the URL. I had to gradually figure out how to manage them in the repositories using diffrent functions and queries. Spring Security also gave me some issues at the beginning as it was my first time using it, but some trial and error eventually fixed it.

At the end of the day, the technologies used are Java Spring Boot, Thymeleaf, HTML, CSS and MySql. I used XAMPP to run tests, as it has MySql and Apache (Web Server). It also had phpMyAdmin which was an easy way for me to manage my database. 


