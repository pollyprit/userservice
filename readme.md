[E-Commerce System Design]


![E-Commerce System Design](ecom.jpg)

User service is at the heart of the e-commerce application. It allows users to register themselves with the system so that later they can place orders in the system. 

Primary functionalities provided by the User Service:
a.	New User Sign-up
b.	User login
c.	User logout
d.	List all the Users in the system
e.	Validate a provided user token

User Service API endpoints:
1. GET /users
2. POST /users/signup
3. POST /users/login
4. POST /users/logout
5. POST /users/validate/{token}
