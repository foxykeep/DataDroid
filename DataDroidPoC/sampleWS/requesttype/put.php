<?php
    header ("content-type: application/json; charset=utf-8");

    $number = $_PUT['number'];

    echo "{value:" . ($number * $number) . "}";
?>