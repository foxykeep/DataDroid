<?php 

define('ERROR_UNKNOWN_USER', 1);
define('ERROR_UNKNOWN_PHONE', 2);

define('ERROR_MISSING_USER', 101);
define('ERROR_MISSING_NAME', 102);
define('ERROR_MISSING_MANUFACTURER', 103);
define('ERROR_MISSING_ANDROID_VERSION', 104);
define('ERROR_MISSING_SCREEN_SIZE', 105);
define('ERROR_MISSING_PRICE', 106);



function showError($errorCode = '')
 {
 	// TODO a modifier
 	showErrorMessage($errorCode);
 } 

function showErrorMessage($errorMessage)
 {
 	echo $errorMessage;
 } 

 ?>