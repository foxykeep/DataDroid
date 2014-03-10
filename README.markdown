DataDroid
=========

This project is a development library for the Android platform. The purpose of this library is to ease the data management in an Android application. It is based on the first solution shown in the presentation "[Developing RESTful Android applications](http://www.google.com/events/io/2010/sessions/developing-RESTful-android-apps.html)" that was given at Google I/O 2010.

This project is now deprecated.


How to use DataDroid in your project
------------------------------------

In order to use DataDroid in your project you have to do the following steps :

1.    Download the current version of the DataDroid project on your computer using git (`git clone git@github.com:foxykeep/DataDroid.git`). The repository contains the following folders :
    * DataDroid : the library. You'll import this library in your project
    * DataDroidPoC : the demonstration application. This project contains code showing how to use the DataDroid as well as skeleton classes you can fill for your project. You can find the application on the market [here](https://play.google.com/store/apps/details?id=com.foxykeep.datadroidpoc)
2. Import the project DataDroid in your Eclipse workspace.
3. Add the library to your project using the Android Library Projects feature in the ADT plugin. More information in the [Android documentation website](http://developer.android.com/guide/developing/projects/projects-eclipse.html#ReferencingLibraryProject)
4. Copy the skeleton classes from DataDroidPoC and change the code according to your project.

If you want to add a database in your project, you can check the ContentProviderCodeGenerator project to help you create the code for it.
([More information](https://github.com/foxykeep/ContentProviderCodeGenerator))


Software Requirements
---------------------

DataDroid has been developed for Android 2.2 and greater.

Credits and License
-------------------

Foxykeep ([http://www.datadroidlib.com](http://www.datadroidlib.com/))

Licensed under the Beerware License :

> You can do whatever you want with this stuff. If we meet some day, and you think this stuff is worth it, you can buy me a beer in return.
