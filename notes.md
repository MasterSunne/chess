# Class Design Guidelines
Keep data private.
Only write getters/setters when other classes need access.
Use standard structure to define classes.
Break up classes w/too much responsibility.
Names of classes and methods should reflect their purpose.
Classes have noun names, methods have verb names.
Use static methods as an exception.

Use inheritance for Is-A relationships.

Use references for Has-A or Uses-A relationships.

# Overriding equals method
Overriding equals method for Classes
When overriding the equals method for a class check to see if either instance is null first
if it's not then use the built-in isInstance method to check that the 2nd is an instance of the
same class as the first object. If it is then continue to compare internal variables for equality.

