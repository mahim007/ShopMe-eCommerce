To understand how the cost of an Order is calculated and what are the different terms used in this project.

An Order consists of a list of OrderDetails entities.

Each OrderDetails entity has the following properties:
the lowest fundamental part of the cost system is:
unit_price;

from unit_price we can calculate the product_cost (manufacturing cost etc) like below:
product_cost = unit_price * 0.8;

we can calculate the unit_price from the product_cost as well like below:
unit_price = product_cost / 0.8;

once we've determined the unit_price, we can determine the next cost term, which is:
subtotal = unit_price * quantity;

and shipping_cost;

Now the OrderDetails entity has all the information needed, we can move next to the Order entity.

shipping_cost = sum(od.shipping_cost)
product_cost = sum(od.product_cost * od.quantity)
subtotal = sum(od.subtotal)
total = subtotal + shipping_cost + tax