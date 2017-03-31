Package naming notes
--------------------

Take care when changing package names as some have external constraints.

* The ```mapapi``` package contains the standard entry points for library users.
* The ```indoors``` package contains classes supporting the indoor "elevator control". It depends upon the standard library but could be moved to a "UI widgets" package if desired.

* The ```location```, ```ui```, and ```web``` packages contain classes which are used by the C++ platform to access Android services. These cannot be renamed without modifications to the C++ platform code, and any such modification must be done in a backwards compatible way.

