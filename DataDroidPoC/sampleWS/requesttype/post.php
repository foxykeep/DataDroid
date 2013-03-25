<?php
    header ("content-type: application/json; charset=utf-8");

    $number = $_POST['number'];

    echo "{value:" . ($number * $number) . "}";
?>