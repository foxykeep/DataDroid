<?php 

function protectFields($value, $db)
{
	$value = stripslashes($value);
	$value = htmlspecialchars($value);
	$value = htmlentities($value);
	return $db->quote($value);
}

 ?>