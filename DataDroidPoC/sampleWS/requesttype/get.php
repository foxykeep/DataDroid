<?php
    header ("content-type: application/json; charset=utf-8");

    $number = $_GET['number'];

    echo "{value:" . ($number * $number) . "}";
?>