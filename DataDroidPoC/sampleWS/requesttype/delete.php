<?php
    header ("content-type: application/json; charset=utf-8");

    $number = $_DELETE['number'];

    echo "{value:" . ($number * $number) . "}";
?>