<?php 
header ("content-type: application/json; charset=utf-8");

if (array_key_exists('version', $_GET)) {
    $version = $_GET['version'];
} else {
    $version = 1;
}

if ($version == 1) {
    echo "{
        cities:{
            city:[
                {
                    name:'Saint Herblain',
                    postalCode:44800,
                    countyNumber:44,
                    countyName:'Loire Atlantique'
                },
                {
                    name:'Cesson Sévigné',
                    postalCode:35510,
                    countyNumber:35,
                    countyName:'Ille et Vilaine'
                }
            ]
        }
    }";
} elseif ($version == 2) {
    usleep(rand(250,3000));
    echo "{
        cities:{
            city:[
                {
                    \"name\":\"Saint Herblain\",
                    \"postalCode\":\"44800\",
                    \"state\":\"Loire Atlantique\",
                    \"country\":\"France\"
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
                    \"name\":\"London\",
                    \"postalCode\":\"W11 2BQ\",
                    \"state\":\"London\",
                    \"country\":\"Great Britain\"
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
}
?>
