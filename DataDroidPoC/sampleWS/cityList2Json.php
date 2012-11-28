<?php
header ("content-type: application/json; charset=utf-8");

usleep(rand(250,5000));
echo "{
    cities:{
        city:[
            {
                \"name\":\"Moscow\",
                \"postalCode\":\"101000\",
                \"state\":\"Moskovski Oblast\",
                \"country\":\"Russia\"
            },
            {
                \"name\":\"London\",
                \"postalCode\":\"W11 2BQ\",
                \"state\":\"London\",
                \"country\":\"Great Britain\"
            },
            {
                \"name\":\"Mountain View\",
                \"postalCode\":\"94043\",
                \"state\":\"California\",
                \"country\":\"USA\"
            },
            {
                \"name\":\"Dallas\",
                \"postalCode\":\"75211\",
                \"state\":\"Texas\",
                \"country\":\"USA\"
            },
            {
                \"name\":\"Düsseldorf\",
                \"postalCode\":\"40593\",
                \"state\":\"North Rhine-Westphalia\",
                \"country\":\"Germany\"
            },
            {
                \"name\":\"Paris\",
                \"postalCode\":\"75000\",
                \"state\":\"Ile de France\",
                \"country\":\"France\"
            },
            {
                \"name\":\"San Francisco\",
                \"postalCode\":\"94114\",
                \"state\":\"California\",
                \"country\":\"USA\"
            },
            {
                \"name\":\"Madrid\",
                \"postalCode\":\"28000\",
                \"state\":\"Community of Madrid\",
                \"country\":\"Spain\"
            }
        ]
    }
}";
?>
