# Notely

This App helps users to 
 - Create new notes.
 - Mark them as favourite or star. 
 - Filter user's favourite/starred notes.
 
The following key points has been taken care of to improve the performance of the app as well as to reduce its size:

- VectorDrawables has been used in place of raster images to make the images scalable and to reduce the overall size of the app. 
- Used weak reference to a activity reference in Asyntasks to avoid memeory leakage problem. 
- RecyclerView has been used instead of ListView to incorporate the internal optimations used in the library for reducing the
memory usage( and App Not Responding warning) in case of a large number of list items.
- Used custom animations in FragmentTransaction to make transitions smooth. Used Hadrware layer to make the animations smooother.
- Used Filterable interface to implement filter functionality in a ListView.



